package ma.enset.chatn7.PRESENTATION;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ma.enset.chatn7.DAO.SingletonConnexionDB;
import ma.enset.chatn7.MODEL.LLM;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UploadFile {
    @FXML
    private Stage stage;
    @FXML
    LLM llm = new LLM();
    private VBox filespathsContainer;
    private final List<String> listFiles = new ArrayList<>();
    private Connection upconnection = SingletonConnexionDB.getConnection();

    public UploadFile() {}

    public UploadFile(Stage stage) {
        this.stage = stage; // Set the stage from the caller
    }


    public void sendFile() throws IOException {
        listFiles.add("\"/home/kalli/Desktop/FILES/enset.pdf\"");
        listFiles.add("\"/home/kalli/Desktop/FILES/exams_merged.pdf\"");
        listFiles.add("\"/home/kalli/Desktop/FILES/Intelligence.pdf\"");
        System.out.println(listFiles);
        llm.sendfileslink(listFiles);
    }
    public void upload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Set default directory
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (filespathsContainer == null) {
            System.err.println("filespathsContainer is not initialized.");
            return;
        }

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                Label pathText = new Label(file.getAbsolutePath());
                HBox pathContainer = new HBox(pathText);
                pathText.setStyle(
                        "-fx-padding: 5px;" +
                                "-fx-font-size: 14px;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-family: Arial;" +
                                "-fx-font-weight: bold;"
                );
                filespathsContainer.getChildren().add(pathContainer);
                listFiles.add(file.getAbsolutePath()); // Collect file paths
            }
        } else {
            Label pathText = new Label("No File is chosen");
            HBox pathContainer = new HBox(pathText);
            pathText.setStyle(
                    "-fx-padding: 5px;" +
                            "-fx-font-size: 14px;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: Arial;" +
                            "-fx-font-weight: bold;"
            );
            filespathsContainer.getChildren().add(pathContainer);
        }
    }

    private byte[] getByteArrayFromFile(final File file) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[10000000];
            int read;
            while ((read = in.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        }
    }

    public void uploadFilesToDB(MouseEvent mouseEvent) {
        if (listFiles == null || listFiles.isEmpty()) {
            System.out.println("No files to upload.");
            return;
        }

        if (upconnection == null) {
            System.out.println("Database connection is null.");
            return;
        }

        // Proceed with the rest of the file upload logic
        String sql = "INSERT INTO Files(nameFile, descriptionFile, File) VALUES(?, ?, ?)";
        try (PreparedStatement statement = upconnection.prepareStatement(sql)) {
            for (String filePath : listFiles) {
                File file = new File(filePath);

                if (file.exists()) {
                    byte[] fileBytes = getByteArrayFromFile(file);
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes)) {
                        statement.setString(1, file.getName()); // Set file name
                        statement.setString(2, "Uploaded on " + new java.util.Date()); // Set a description
                        statement.setBlob(3, bais); // Set file content as blob

                        statement.executeUpdate();
                        System.out.println("Uploaded: " + file.getName());
                    }
                } else {
                    System.out.println("File not found: " + filePath);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error uploading files to the database.");
        }
    }

    @FXML
    private void goToChat(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
