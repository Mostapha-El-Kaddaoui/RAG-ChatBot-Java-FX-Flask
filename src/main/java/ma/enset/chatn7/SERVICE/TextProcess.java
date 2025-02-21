package ma.enset.chatn7.SERVICE;

import java.util.ArrayList;
import java.util.List;

public class TextProcess {
    // Step 1: Remove unnecessary characters (e.g., special characters and white spaces)
    public static String remChar(String text) {
        // Replace multiple spaces or newlines with a single space
        text = text.replaceAll("\\s+", " ");
        // Remove special characters and non-alphanumeric symbols (optional)
        text = text.replaceAll("[^\\w\\s]", "");
        return text;
    }

    // Step 2: Normalize text (convert to lowercase and trim)
    public static String normText(String text) {
        text = text.toLowerCase(); // Convert all text to lowercase
        text = text.trim(); // Remove leading and trailing spaces
        return text;
    }

    // Step 3: Remove headers and footers if needed (optional)
    public static String remHaF(String text) {
        // For example, if headers/footers have a specific format, remove them
        text = text.replaceAll("^(header text here.*|footer text here.*)", "");
        return text;
    }

    public static List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(i + chunkSize, text.length())));
        }
        return chunks;
    }
    public List<String> textCleaning(String text) {
        text = remChar(text);
        text = normText(text);
        text = remHaF(text);
        List<String> textArr = chunkText(text,1000);// Optional, based on document format
        return textArr;
    }
}
