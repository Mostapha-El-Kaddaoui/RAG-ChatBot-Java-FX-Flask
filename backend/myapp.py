from flask import Flask, request, jsonify
import os
import spacy
import fitz  # PyMuPDF
from PIL import Image
import io
import pytesseract
from langchain.vectorstores import Chroma
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import PyPDFLoader
from langchain_core.documents import Document
import google.generativeai as genai
import requests
from bs4 import BeautifulSoup
import logging

app = Flask(__name__)

# Initialize global variables
vectorstore = None
PERSIST_DIRECTORY = "./chroma_db_nccn"

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configure API
if "API_KEY" not in os.environ:
    raise ValueError("API_KEY environment variable is not set")
genai.configure(api_key=os.environ["API_KEY"])
model = genai.GenerativeModel("gemini-1.5-flash")

try:
    nlp = spacy.load("en_core_web_md")
except OSError:
    raise OSError("Spacy model 'en_core_web_md' not found. Please install it using: python -m spacy download en_core_web_md")

def extract_text_with_ocr(pdf_path):
    """Extract text from PDF images using OCR."""
    try:
        doc = fitz.open(pdf_path)
        full_text = []
        
        for page_num in range(doc.page_count):
            page = doc[page_num]
            images = page.get_images(full=True)
            
            for img_index, img in enumerate(images):
                try:
                    image_index = img[0]
                    xref = doc.extract_image(image_index)
                    img_bytes = xref["image"]
                    image = Image.open(io.BytesIO(img_bytes))
                    text = pytesseract.image_to_string(image)
                    full_text.append(text)
                except Exception as e:
                    logger.error(f"Error processing image {img_index} on page {page_num}: {e}")
                    continue
        
        doc.close()
        return "\n".join(full_text).strip()
    except Exception as e:
        logger.error(f"Error in OCR processing: {e}")
        return ""

class SpacyEmbeddings:
    """Spacy embeddings wrapper class."""
    def embed_documents(self, texts):
        return [nlp(text).vector for text in texts]

    def embed_query(self, text):
        return nlp(text).vector

def get_data_from_website(url: str) -> str:
    """
    Retrieve and parse text content from a website.
    """
    try:
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Remove script and style elements
        for script in soup(["script", "style"]):
            script.decompose()
            
        # Get text content
        text = soup.get_text(separator='\n', strip=True)
        
        return text
        
    except Exception as e:
        logger.error(f"Error fetching {url}: {str(e)}")
        raise

def process_website_content(url: str):
    """Process website content and add it to vector store."""
    text = get_data_from_website(url)
    global vectorstore
    
    try:
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=100
        )
        
        # Create document with metadata
        doc = Document(page_content=text, metadata={"source": url})
        docs = text_splitter.split_documents([doc])
        
        embedding_function = SpacyEmbeddings()
        
        # Initialize or update vectorstore
        if vectorstore is None:
            vectorstore = Chroma.from_documents(
                docs,
                embedding_function,
                persist_directory=PERSIST_DIRECTORY
            )
        else:
            vectorstore.add_documents(docs)
        
        return True
        
    except Exception as e:
        logger.error(f"Error processing website content: {e}")
        raise

def storeFiles(links):
    """Process and store PDF files in vector database."""
    global vectorstore
    
    if not links:
        return jsonify({"error": "No links provided"}), 400
    
    processed_files = []
    failed_files = []
    
    for link in links:
        try:
            loaders = [PyPDFLoader(link)]
            docs = []
            for loader in loaders:
                docs.extend(loader.load())
            
            # If no content found, try OCR
            if not docs or not docs[0].page_content:
                pdf_text = extract_text_with_ocr(link)
                if pdf_text:
                    docs = [Document(metadata={'source': link, 'page': 0}, page_content=pdf_text)]
                else:
                    failed_files.append({"link": link, "error": "No text content found"})
                    continue
            
            text_splitter = RecursiveCharacterTextSplitter(
                chunk_size=1000,
                chunk_overlap=100
            )
            docs = text_splitter.split_documents(docs)
            
            embedding_function = SpacyEmbeddings()
            
            if vectorstore is None:
                vectorstore = Chroma.from_documents(
                    docs,
                    embedding_function,
                    persist_directory=PERSIST_DIRECTORY
                )
            else:
                vectorstore.add_documents(docs)
            
            processed_files.append(link)
            
        except Exception as e:
            failed_files.append({"link": link, "error": str(e)})
            continue
    
    return jsonify({
        "status": "completed",
        "processed_files": processed_files,
        "failed_files": failed_files
    })

def getResponseFromFile(query):
    """Generate response based on query using vector search and Gemini."""
    global vectorstore
    
    if vectorstore is None:
        return jsonify({"error": "No documents have been processed yet"}), 400
    
    try:
        results = vectorstore.similarity_search(query=query, k=3)
        context = [result.page_content for result in results]
        contextText = "\n".join(context)
        
        prompt = f"""Based on this context:
{contextText}

Answer the following question:
{query}

Please provide a clear and concise response based solely on the information found in the context."""
        
        response = model.generate_content(prompt)
        return response.text
        
    except Exception as e:
        return jsonify({"error": f"Error generating response: {str(e)}"}), 500

def getResponseFromWebSite(link,query):
    """Process website content and generate response."""
    try:
        # Get website content
        text = get_data_from_website(link)
        
        # Process and store the content
        process_website_content(text, link)
        results = vectorstore.similarity_search(query=query, k=3)
        context = [result.page_content for result in results]
        contextText = "\n".join(context)
        
        prompt = f"""Based on this context:
        {contextText}

        Answer the following question:
        {query}

        Please provide a clear and concise response based solely on the information found in the context."""
        
        response = model.generate_content(prompt)
        return response.text
        
    except Exception as e:
        logger.error(f"Error processing website {link}: {e}")
        return ""

@app.route('/files', methods=['POST'])
def receive_files():
    """Endpoint to receive and process PDF files."""
    if not request.is_json:
        return jsonify({"error": "Content-Type must be application/json"}), 400
    
    data = request.get_json()
    if "links" not in data:
        return jsonify({"error": "Missing 'links' field in request"}), 400
    
    return storeFiles(data["links"])

@app.route('/askfiles', methods=['POST'])
def receive_query():
    """Endpoint to process queries about stored files."""
    if not request.is_json:
        return jsonify({"error": "Content-Type must be application/json"}), 400
    
    data = request.get_json()
    if "query" not in data:
        return jsonify({"error": "Missing 'query' field in request"}), 400
    
    response = getResponseFromFile(data["query"])
    return jsonify({"response": response}), 200

@app.route('/askWebSite', methods=['POST'])
def receive_link():
    data = request.get_json()
    response = process_website_content(data["link"])
    print(response)
    return jsonify({"response": response}), 200

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)