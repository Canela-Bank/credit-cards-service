package com.canela.service.creditcardmgmt.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canela.service.creditcardmgmt.entity.Client;
import com.canela.service.creditcardmgmt.exceptions.CreditCardBadRequestException;
import com.canela.service.creditcardmgmt.exceptions.NoContentException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RestController
@RequestMapping("api/credit-card/request")
public class CreditCardRequestController {
	
	StringBuilder result = null;
	
	@PostMapping("/")
	public String creditCardRequest(@RequestBody Client client) {
	
	//Data verification 
		if(client.getDocument() == null || client.getType() == null) {			
			throw new CreditCardBadRequestException();			
		}
		
		try { 
			//Convert Object Client to String
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String clientString = ow.writeValueAsString(client);
			
			// Add URL API 
			URL url = new URL ("http://localhost:8081/api/centralderiesgo/getReports");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			// Create BodyRequest params 
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            
            // Write server body request
            OutputStream os = con.getOutputStream();
            os.write(clientString.getBytes("UTF-8"));
            os.close();         
            con.connect();
              
            // ResponseCode verification
               int responseCode = con.getResponseCode(); 
               if(responseCode != HttpURLConnection.HTTP_OK) {
            	   throw new NoContentException();	       	   
               }
               
               else {
            	   
            	   try {
            		  // Read server response
                       InputStream in = new BufferedInputStream(con.getInputStream());
                       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                       result = new StringBuilder();
                       String line;
                       while ((line = reader.readLine()) != null) {
                           result.append(line);
                          
                       }            
                       System.out.println("result: " + result.toString()) ;
                       
                       return "Request Sent...";
   
                   } catch (Exception e) {
                       e.printStackTrace();
                   }       	      	                                 
               }
               		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new CreditCardBadRequestException();	
 	
	}
	
}
