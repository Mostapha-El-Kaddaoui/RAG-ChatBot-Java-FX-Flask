module ma.enset.chatn7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires org.apache.pdfbox;
    requires org.apache.httpcomponents.httpclient;
    requires langchain4j.elasticsearch;
    requires langchain4j.core;
    requires elasticsearch.rest.client;
    requires elasticsearch;
    requires org.apache.httpcomponents.httpcore;
    requires langchain4j.embeddings.all.minilm.l6.v2;
    requires org.apache.httpcomponents.httpasyncclient;
    requires testcontainers;


    opens ma.enset.chatn7 to javafx.fxml;
    exports ma.enset.chatn7;
    exports ma.enset.chatn7.DAO;
    opens ma.enset.chatn7.DAO to javafx.fxml;
    exports ma.enset.chatn7.SERVICE;
    opens ma.enset.chatn7.SERVICE to javafx.fxml;
    exports ma.enset.chatn7.MODEL;
    opens ma.enset.chatn7.MODEL to javafx.fxml;
    exports ma.enset.chatn7.PRESENTATION;
    opens ma.enset.chatn7.PRESENTATION to javafx.fxml;
}