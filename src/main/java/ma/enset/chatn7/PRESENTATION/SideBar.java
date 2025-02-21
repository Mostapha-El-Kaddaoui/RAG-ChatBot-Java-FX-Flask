package ma.enset.chatn7.PRESENTATION;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ma.enset.chatn7.DAO.SingletonConnexionDB;
import ma.enset.chatn7.MODEL.ChatService;
import ma.enset.chatn7.MODEL.HistoryButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SideBar {
    private Connection connection = SingletonConnexionDB.getConnection();
    @FXML
    private VBox historyVBox;
    @FXML
    public void initialize() {
        ChatService.getInstance().setSideBarController(this);
        getHistory();
    }
    public void getHistory() {
        String query = "SELECT * FROM History";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            historyVBox.getChildren().clear();
            while (rs.next()) {
                HistoryButton HButton = new HistoryButton(rs.getString("Namee"),rs.getString("Content"));
                Button messageButton = new Button(HButton.getNamee());
                messageButton.setPrefHeight(34.0);
                messageButton.setPrefWidth(265.0);

                messageButton.getStyleClass().add("neutralize");

                messageButton.setOnAction(event -> {
                    ChatService.getInstance().affectHistory(HButton.getContent());
                    System.out.println("Button clicked: " + HButton.getContent());
                });

                historyVBox.getChildren().add(messageButton);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving chat history: " + e.getMessage());
        }
    }

}
