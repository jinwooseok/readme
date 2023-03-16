package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class getSummary {
    String summaryMessage;
    String fulltext ="";
    Thread thread2 = new Thread(new Runnable() {
        public void run() {
            try {
                String message = getReqMessage(fulltext);
                URL url = new URL("https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "q02nxvaanw");
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", "t1D7xkTV7BA7oE1uRIdEclPIGDFnDHanh4o6qsFu");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(message);
                wr.flush();
                wr.close();

                int responseCode = connection.getResponseCode();
                System.out.println(responseCode);
                if (responseCode == 200) {
                    System.out.println(connection.getResponseMessage());

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));
                    String decodedString;
                    while ((decodedString = in.readLine()) != null) {
                        summaryMessage = decodedString;
                    }
                    in.close();

                } else {  // 에러 발생
                    summaryMessage = connection.getResponseMessage();
                }
            } catch (Exception e) {
                System.out.println("------" + e);
            }

            System.out.println(">>>>>>>>>>" + summaryMessage);

        }
    });

    public static String getReqMessage(String objectStorageURL) {

            String requestBody = "";

            try {

            long timestamp = new Date().getTime();

            JSONObject option = new JSONObject();
            option.put("language","ko");
            JSONArray optionList = new JSONArray();
            optionList.put(option);
            JSONObject document = new JSONObject();
            document.put("content", objectStorageURL);
            document.put("title", "요약");
            JSONArray documentList = new JSONArray();
            documentList.put(option);
            JSONObject request = new JSONObject();
            request.put("document",document);
            request.put("option",option);

            requestBody = request.toString();
            System.out.println(">>>>>>>>>>" + requestBody);

            } catch (Exception e){
            System.out.println("## Exception : " + e);
            }

            return requestBody;

        }
}