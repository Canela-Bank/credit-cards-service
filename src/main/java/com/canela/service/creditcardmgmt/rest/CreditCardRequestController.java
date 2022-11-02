package com.canela.service.creditcardmgmt.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/credit-card")
public class CreditCardRequestController {

    StringBuilder result = null;
	
	@PostMapping("/requestCreditCard")
	public ResponseEntity<String> creditCardRequest(@RequestBody String client) {
		try {
			// Add URL API
            JSONObject clientObject = new JSONObject(client);
            String type = (String) clientObject.get("type");
            int document = (int) clientObject.get("document");
			URL url = new URL ("http://localhost:3000/api/prov/centralderiesgo/getReports/"+ type +"/"+ document +"");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    try {
                        JSONObject body = new JSONObject(result.toString());
                        JSONObject data = (JSONObject) body.get("data");
                        JSONObject user = (JSONObject) data.get("user");
                        JSONObject finalData1 = data;
                        JSONObject dataReturn = new JSONObject(){{
                            put("document", user.get("document"));
                            put("type", user.get("type"));
                            put("firstname", user.get("firstname"));
                            put("lastname", user.get("lastname"));
                            put("mail", user.get("mail"));
                            put("phone", user.get("phone"));
                            put("request", "card");
                            put("points", (int) finalData1.get("points"));
                            put("status", "wait");
                        }};
                        url = new URL("http://localhost:4000/graphql?query=mutation{createCreditQuery(document:1234567890,type:\"CC\",firstname:\"Jimmy\",lastname:\"Castro\",mail:\"ja.castros@javeriana.edu.co\",phone:\"3222412343\",request:\"card\",points:500,status:\"wait\"){document,firstname,lastname,request,status}}");
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        int responseCode2 = con.getResponseCode();
                        if(responseCode2 == HttpURLConnection.HTTP_OK){
                            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            result = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            JSONObject response = new JSONObject(result.toString());
                            data = (JSONObject) ((JSONObject) response.get("data")).get("createCreditQuery");
                            HttpURLConnection finalCon1 = con;
                            JSONObject finalData = data;
                            return ResponseEntity.status(HttpURLConnection.HTTP_OK).body(new JSONObject(){{
                                put("status", finalCon1.getResponseCode());
                                put("data", finalData);
                            }}.toString());
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpURLConnection.HTTP_INTERNAL_ERROR).body(new JSONObject(){{
                        put("status", HttpURLConnection.HTTP_INTERNAL_ERROR);
                        put("message", e.getMessage());
                    }}.toString());
                }
            }
            HttpURLConnection finalCon = con;
            return ResponseEntity.status(responseCode).body(new JSONObject(){{
                put("status", responseCode);
                put("message", finalCon.getResponseMessage());
            }}.toString());
		} catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpURLConnection.HTTP_INTERNAL_ERROR).body(new JSONObject(){{
                put("status", HttpURLConnection.HTTP_INTERNAL_ERROR);
                put("message", e.getMessage());
            }}.toString());
		}
    }
}
