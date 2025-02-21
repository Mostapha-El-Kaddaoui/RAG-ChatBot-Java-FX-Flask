package ma.enset.chatn7;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class SDocument {
    @FXML
    private Stage stage;
    private List<String> paragraphs;
    private String link;


    public SDocument(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<String> extractText(){
        try (PDDocument document = PDDocument.load(new File(this.link))) {
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);

                System.out.println(text);
            } else {
                System.out.println("The PDF is encrypted and cannot be read.");
            }
        } catch (IOException e) {
            System.err.println("Error reading the PDF: " + e.getMessage());
        }
        return null;
    }
    public void uploadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Set default directory
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

    }

}
