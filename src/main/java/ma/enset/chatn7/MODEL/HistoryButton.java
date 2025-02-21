package ma.enset.chatn7.MODEL;

public class HistoryButton {
    private String Namee;
    private String Content;

    public HistoryButton(String namee, String content) {
        Namee = namee;
        Content = content;
    }

    public String getNamee() {
        return Namee;
    }

    public void setNamee(String namee) {
        Namee = namee;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
