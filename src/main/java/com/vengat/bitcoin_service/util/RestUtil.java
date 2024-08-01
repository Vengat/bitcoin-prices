package com.vengat.bitcoin_service.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtil {

    public static String sendGetRequest(String urlString, int maxRetries, int retryDelay) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            HttpURLConnection con = null;
            try {
                URL url = new URL(urlString);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    return content.toString();
                }
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw e;
                }
                Thread.sleep(retryDelay);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        throw new Exception("Failed to send GET request after " + maxRetries + " attempts");
    }

    public static String sendGetRequest(String urlString) throws Exception {
        return sendGetRequest(urlString, 3, 5);
    }
}