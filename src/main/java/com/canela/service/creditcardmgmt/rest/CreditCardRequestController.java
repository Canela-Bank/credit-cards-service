package com.canela.service.creditcardmgmt.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canela.service.creditcardmgmt.exceptions.CreditCardRequestResponseException;

import io.netty.handler.codec.json.JsonObjectDecoder;



@RestController
@RequestMapping("api/creditcardmgmt")
public class CreditCardRequestController {
	
	@GetMapping("requestCreditCard")
	public String creditCardRequest(@RequestBody String client) {
		

		  StringBuilder result = null;
	    
		try {
			
		  
			
			URL url = new URL ("http://localhost:8081/api/centralderiesgo/getReports");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");

            OutputStream os = con.getOutputStream();
            os.write(client.getBytes("UTF-8"));
            os.close();         
            con.connect();
                                    
               int responseCode = con.getResponseCode();
              
               if(responseCode != 200) {
            	   throw new RuntimeException("Error" + responseCode);     	   
               }
               else {
            	   
            	   try {
            		   
                       InputStream in = new BufferedInputStream(con.getInputStream());
                       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                       result = new StringBuilder();
                       String line;
                       while ((line = reader.readLine()) != null) {
                           result.append(line);
                          
                       }
                       
                       
                       System.out.println("result: " + result.toString()) ;
                       
                   
                       try {
                    	   JSONObject jObj = new JSONObject(result.toString());
                    	    return "Request Sent...";
                    	   
     
                       } catch (JSONException e) {
                           System.out.println("Error parsing data " + e.toString()); 
                       }  
                       
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
            	   
            	   	                                 
               }
               		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return "Request Sent...";      
		
	}
	

	
	
	
}
