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
                    while ((line = reader.readLine()) != null)
                        result.append(line);
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
                        url = new URL("http://localhost:3001/graphql?query=mutation%7B%0A%20%20createCreditCard(number%3A%202%2C%20cvv%3A502%2C%20exp_date%3A%222022-07-27%2020%3A08%3A16%22%2C%20card_name%3A%20%22David%20Raamirez%22%2C%20debt%3A%20152000%2C%20user_id%3A%221193093873%22%2C%20user_document_type%3A1)%7B%0A%20%20%20%20number%2C%0A%20%20%20%20cvv%2C%0A%20%20%20%20exp_date%2C%0A%20%20%20%20card_name%2C%0A%20%20%20%20debt%2C%0A%20%20%20%20user_id%2C%0A%20%20%20%20user_document_type%0A%20%20%7D%0A%7D%0A");
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
