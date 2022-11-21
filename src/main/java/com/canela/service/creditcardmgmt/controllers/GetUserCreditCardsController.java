package com.canela.service.creditcardmgmt.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/credit-cards")
@Tag(name = "Credit Card", description = "Credit Card REST API")
public class GetUserCreditCardsController {
	
	 @Operation(summary = "Get user credit cards", description = "Connect to the database and get the user credit cards", tags = {"Credit card"})
	 @GetMapping(value = "/getCreditCards/{document}/{typeDocument}" )
	    public ResponseEntity<String> getUserCreditCards(@PathVariable String document, @PathVariable String typeDocument) {	 
		 
		 try {
			// String url = "http://localhost:3002/graphql";
			 String operation = "getCreditCardsByUser";
			 String query = "query{getCreditCardsByUser(user_document:\""+document+"\",user_document_type:"+typeDocument+"){\n"
			 		+ "  number\n"
			 		+ "  cvv\n"
			 		+ "  exp_date\n"
			 		+ "  card_name\n"
			 		+ "  debt\n"
			 		+ "  user_id\n"
			 		+ "  user_document_type\n"
			 		+ "}}";
			
				 CloseableHttpClient client = HttpClientBuilder.create().build();
			        HttpGet requestGraphQL = new HttpGet("http://localhost:3002/graphql");
			        URI uri = new URIBuilder(requestGraphQL.getURI())
			                .addParameter("query", query)
			                .build();
			        requestGraphQL.setURI(uri);
			        HttpResponse response =  client.execute(requestGraphQL);
			        InputStream inputResponse = response.getEntity().getContent();
			        String actualResponse = new BufferedReader(
			                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
			                .lines()
			                .collect(Collectors.joining("\n"));

			        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
			        
			        JsonNode creditCards = node.get("data").get(operation);			       
			        
			        if(creditCards.isEmpty()) {
			        	 return ResponseEntity.status(HttpURLConnection.HTTP_NOT_FOUND).body("Lo sentimos, hubo un error.");
			        }
			        else{
			        	 JsonNode UserCreditCards = node.get("data").get(operation);
					        
					     return ResponseEntity.status(HttpURLConnection.HTTP_OK).body(UserCreditCards.toString());
			        }

		} catch (Exception e) {
			throw new RuntimeException(e);
			
		}	
		 	 
		
	 }

}
