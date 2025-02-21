package ma.enset.chatn7.PRESENTATION;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ma.enset.chatn7.MODEL.LLM;
import ma.enset.chatn7.MODEL.WebSites;

public class AddWebSite {
    @FXML
    private TextField inputLink;
    private WebSites wb = new WebSites();
    LLM llm = new LLM();
    public void sendLink(){
        String link = inputLink.getText().trim();
        wb.addLink(link);
        llm.sendWebSiteLink(link);
    }
}
