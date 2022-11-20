package com.canela.service.creditcardmgmt.controller;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("api/credit-card")
public class GetCreditCardTransactions {

    @GetMapping("/transactions")
    public ResponseEntity<String> getTransactions(){
        try {
            URL url = new URL("http://localhost:4000/graphql?query=query%7BgetAllTransactions%7Bid%2Cplace%2Cammount%7D%7D");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();
                String line;
                while((line = reader.readLine()) != null){
                    response.append(line);
                }
                JSONObject object = new JSONObject(response.toString());
                object = (JSONObject) object.get("data");
                JSONArray array = (JSONArray) object.get("getAllTransactions");
                return ResponseEntity.status(responseCode).body(new JSONObject(){{
                    put("status", HttpURLConnection.HTTP_OK);
                    put("data", array);
                }}.toString());
            } else
                return ResponseEntity.status(responseCode).body(connection.getResponseMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
