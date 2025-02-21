package ma.enset.chatn7.MODEL;

import ma.enset.chatn7.PRESENTATION.PChat;
import ma.enset.chatn7.PRESENTATION.SideBar;

public class ChatService {
    private static ChatService instance;
    private SideBar sideBarController;
    private PChat pchatController;

    private ChatService() {}

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public void setSideBarController(SideBar controller) {
        this.sideBarController = controller;
    }
    public  void setPchatController(PChat controller){
        this.pchatController = controller;
    }

    public void refreshHistory() {
        if (sideBarController != null) {
            sideBarController.getHistory();
        }
    }
    public void affectHistory(String history){
        if (pchatController != null){
            pchatController.recoverHistory(history);
        }
    }
}