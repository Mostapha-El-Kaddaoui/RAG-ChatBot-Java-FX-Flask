package ma.enset.chatn7.MODEL;
import ma.enset.chatn7.SERVICE.TextProcess;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
public class LLM {

    public void sendFile(String link) throws IOException {
        TextProcess textPro = new TextProcess();
        String url = "http://127.0.0.1:8080/file";
        StringBuilder response = new StringBuilder();

        // Extract text from the PDF
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(PDDocument.load(new File(link)));

        // Clean the extracted text
        List<String> textArr = textPro.textCleaning(text);
        System.out.println(textArr);

        for (int i = 0; i < textArr.size(); i++) {
            String data_to_send = "{\"data\": \"" + textArr.get(i).replace("\"", "\\\"") + "\"}"; // Escape quotes inside text
            try {
                URL obj = new URI(url).toURL();
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                // Set up the connection properties
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");

                // Send the POST data
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data_to_send.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Read the response from the server
                int status = connection.getResponseCode();
                System.out.println("Response Code: " + status);

                if (status == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    response.append("Error: " + status);
                }
                connection.disconnect();
            } catch (IOException | URISyntaxException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }

        }

    }
    public String getResponse(String text){
        String url = "http://127.0.0.1:8080";
        StringBuilder response = new StringBuilder();

        String data_to_send = "{\"data\": \"" + text + "\"}";


        try {
            URL obj = new URI(url).toURL();

            HttpURLConnection connection = (HttpURLConnection)obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(data_to_send);
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                try (
                        BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                System.out.println("Response: " + response.toString());
            }
            else {
                System.out.println("Error: HTTP Response code - " + responseCode);
            }
            connection.disconnect();
        }
        catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    public void sendfileslink(List<String> links){
        String url = "http://127.0.0.1:8080/files";
        StringBuilder response = new StringBuilder();
        String data_to_send = "{\"links\": " + links + "}";
        System.out.println(data_to_send);
        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // Send the POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data_to_send.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            System.out.println("Response Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                response.append("Error: " + status);
            }
            connection.disconnect();

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
    public String askfiles(String query){
        String url = "http://127.0.0.1:8080/askfiles";
        StringBuilder response = new StringBuilder();
        String data_to_send = "{\"query\": \"" + query + "\"}";
        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // Send the POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data_to_send.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            System.out.println("Response Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                response.append("Error: " + status);
            }
            connection.disconnect();

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("response");
    }


    public String sendWebSiteLink(String link){
        String url = "http://127.0.0.1:8080/askWebSite";
        StringBuilder response = new StringBuilder();
        String data_to_send = "{\"link\": \"" + link + "\"}";
        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // Send the POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data_to_send.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            System.out.println("Response Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                response.append("Error: " + status);
            }
            connection.disconnect();

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return response.toString();
    }
}