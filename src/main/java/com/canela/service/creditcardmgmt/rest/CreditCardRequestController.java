package com.canela.service.creditcardmgmt.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
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

    private JSONObject generateCardInfo(){
        JSONObject object = new JSONObject();
        byte[] bytes = new byte[6];
        new Random().nextBytes(bytes);
        object.put("number", new BigInteger(bytes).abs());
        object.put("cvv", new Random().nextInt(100, 1000));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        object.put("exp_date", LocalDate.now().plusYears(4).format(formatter));
        return object;
    }
	
	@PostMapping("/requestCreditCard")
	public ResponseEntity<String> creditCardRequest(@RequestBody String client) {
		try {
			// Add URL API
            JSONObject clientObject = new JSONObject(client);
            String type = clientObject.getString("type");
            int document = clientObject.getInt("document");
            String name = clientObject.getString("name");
            double debt = clientObject.getDouble("debt");
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
                        JSONObject generated = generateCardInfo();
                        int nType = 0;
                        if(type.equals("CC"))
                            nType = 1;
                        else
                            nType = 2;
                        url = new URL("http://localhost:3002/graphql?query=mutation%7BcreateCreditCard(" +
                                "number%3A" + (generated.getInt("number")) + "%2C" +
                                "cvv%3A" + (generated.getInt("cvv")) + "%2C" +
                                "exp_date%3A%22" + generated.getString("exp_date") + "%22%2C" +
                                "card_name%3A%22" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "%22%2C" +
                                "debt%3A" + (debt) + "%2C" +
                                "user_id%3A%22" + (document) + "%22%2C" +
                                "user_document_type%3A" + nType + ")" +
                                "%7Bnumber%2Ccvv%2Cexp_date%2Ccard_name%2Cdebt%2Cuser_id%2Cuser_document_type%7D%7D");
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        System.out.println(con.getURL());
                        int responseCode2 = con.getResponseCode();
                        if(responseCode2 == HttpURLConnection.HTTP_OK){
                            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            result = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            JSONObject response = new JSONObject(result.toString());
                            data = response.getJSONObject("data").getJSONObject("createCreditCard");
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
                put("status", finalCon.getResponseCode());
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
