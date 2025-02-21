package ma.enset.chatn7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ma.enset.chatn7.PRESENTATION.PChat;
import java.io.IOException;

public class chat extends Application {
    private static PChat pChatController;
    public static void setPChatController(PChat controller) {
        pChatController = controller;
    }

    public static PChat getPChatController() {
        return pChatController;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(chat.class.getResource("chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1235, 512);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}