package ma.enset.chatn7.PRESENTATION;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ma.enset.chatn7.DAO.SingletonConnexionDB;
import ma.enset.chatn7.MODEL.ChatService;
import ma.enset.chatn7.MODEL.LLM;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PChat {
    private Connection connection = SingletonConnexionDB.getConnection();

    @FXML
    private TextField messageinput;
    @FXML
    private Stage stage;
    @FXML
    protected VBox chatMessages;
    @FXML
    private ListView listMessages;
    LLM llm = new LLM();

    @FXML
    public void initialize() {
        ChatService.getInstance().setPchatController(this);
        //PChat.sendFile();
    }
    @FXML
    public void sendFile() throws IOException {
        List<String> links= new ArrayList<>();
        links.add("\"/home/kalli/Desktop/PROJECT/enset.pdf\"");
        llm.sendfileslink(links);
    }
    public void getResponseFromFile(){
        String message = messageinput.getText().trim();
        String Response = llm.askfiles(message);
        injectResponse(Response);
    }
    public void SendMessage() {
        String message = messageinput.getText().trim();
        Label messageText = new Label(message);
        messageText.setWrapText(true);
        messageText.setMaxWidth(500);
        HBox messageContainer = new HBox(messageText);
        messageText.setStyle(
                "-fx-padding: 10px;" +
                        "-fx-alignment: right;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: Arial;" +
                        "-fx-background-color: #ff7300;" +
                        "-fx-background-radius: 5px;"
        );
        messageContainer.setAlignment(Pos.CENTER_RIGHT);

        chatMessages.getChildren().add(messageContainer);

        messageinput.clear();

       injectResponse(message);
    }
    public void injectResponse(String message){
        String response = llm.askfiles(message);
        Label responseText = new Label(response);
        responseText.setWrapText(true);
        responseText.setMaxWidth(500);
        HBox responseContainer = new HBox(responseText);
        responseText.setStyle(
                "-fx-padding: 10px;" +
                        "-fx-alignment: left;" +  // Changed this to left alignment
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: black;" + // Changed to black for better visibility
                        "-fx-font-family: Arial;" +
                        "-fx-background-color: blue;" + // Changed background color for response
                        "-fx-background-radius: 5px;"
        );
        responseContainer.setAlignment(Pos.CENTER_LEFT);  // Align response to the left

        chatMessages.getChildren().add(responseContainer);
    }

    public void getLink() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ma/enset/chatn7/addWebSite.fxml"));
        Parent root = loader.load();

        Stage newStage = new Stage();
        Scene scene = new Scene(root, 550, 250);

        newStage.setTitle("Add Link");
        newStage.setScene(scene);

        newStage.show();
    }
    public void getDocument() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ma/enset/chatn7/UploadFile.fxml"));
        Parent root = loader.load();

        Stage newStage = new Stage();
        Scene scene = new Scene(root, 550, 250);

        newStage.setTitle("Add Document");
        newStage.setScene(scene);

        newStage.show();
    }
    public void newChat() {
        String name = "History " + System.currentTimeMillis(); // Make each history name unique
        String messages = collectMessages(chatMessages);

        if (messages.isEmpty()) {
            System.out.println("No messages to save.");
            return;
        }

        storeNewHistory(name, messages);
        chatMessages.getChildren().clear();
        ChatService.getInstance().refreshHistory();
    }

    private String collectMessages(VBox chatBox) {
        StringBuilder messages = new StringBuilder();

        for (Node child : chatBox.getChildren()) {
            if (child instanceof HBox) {
                HBox hBox = (HBox) child;
                // Check if it's a user message (RIGHT) or bot message (LEFT)
                boolean isUserMessage = hBox.getAlignment() == Pos.CENTER_RIGHT;

                for (Node labelChild : hBox.getChildren()) {
                    if (labelChild instanceof Label) {
                        // Add prefix to identify message type
                        messages.append(isUserMessage ? "USER: " : "BOT: ")
                                .append(((Label) labelChild).getText())
                                .append("\n");
                    }
                }
            }
        }

        return messages.toString().trim();
    }

    public void storeNewHistory(String name, String messages) {
        String query = "INSERT INTO History(Namee, Content) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, messages);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while saving chat history: " + e.getMessage());
        }
        System.out.println("Saved successfully.");
        }

    public void recoverHistory(String history) {
        String[] messages = history.split("\n");
        chatMessages.getChildren().clear();
        for (String message : messages) {
            HBox messageHBox = new HBox();
            String[] message2 = message.split(":");
            Label messageLabel = new Label(message2[1].trim());
            System.out.println(message2[1]+":::"+message2[0]);
            if(message2[0].equals("USER")){
                messageHBox.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(500);
                messageLabel.setStyle(
                        "-fx-padding: 10px;" +
                                "-fx-alignment: right;" +
                                "-fx-font-size: 12px;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-family: Arial;" +
                                "-fx-background-color: #ff7300;" +
                                "-fx-background-radius: 5px;"
                );
                messageHBox.getChildren().add(messageLabel);
            }else{
                messageHBox.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(500);
                messageLabel.setStyle(
                        "-fx-padding: 10px;" +
                                "-fx-alignment: left;" +  // Changed this to left alignment
                                "-fx-font-size: 12px;" +
                                "-fx-text-fill: black;" + // Changed to black for better visibility
                                "-fx-font-family: Arial;" +
                                "-fx-background-color: blue;" + // Changed background color for response
                                "-fx-background-radius: 5px;"
                );
                messageHBox.getChildren().add(messageLabel);
            }
            chatMessages.getChildren().add(messageHBox);
        }

    }
    public void recordVoice(){

    }
}



