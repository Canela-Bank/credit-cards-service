package com.canela.service.creditcardmgmt.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.GraphQLException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/credit-cards")
class DeleteCreditCardController {

    @Value("${integrators.data.ip}")
    private String dataIp;

    @Value("${integrators.data.port}")
    private String dataPort;
    @DeleteMapping("/delete/{card_number}")
    public ResponseEntity deleteCreditCard(@PathVariable int card_number) {
        String url = "http://"+dataIp+":"+dataPort+"/graphql";
        try {
            String creditCardQuery = String.format("query {" +
                    "getCreditCardByNumber(number: %s) {" +
                    "       number" +
                    "       debt" +
                    "   }" +
                    "}", card_number);
            ObjectNode creditCard = queryGraphQLService(url, "", creditCardQuery);
            JsonNode data = creditCard.get("data");
            if(data == null) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("No se pudo eliminar la tarjeta de credito");
            }
            JsonNode debt = data.get("getCreditCardByNumber").get("debt");
            if(debt == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontro la tarjeta de credito que se quiere eliminar");
            }
            double true_debt = debt.asDouble();

            if(true_debt > 0) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("La tarjeta de credito no puede ser eliminada pues tiene deuda");
            }

            String deleteCreditCardQuery = String.format("mutation {" +
                    "   deleteCreditCard(number: %s) {" +
                    "       message" +
                    "   }" +
                    "}", data.get("getCreditCardByNumber").get("number").asText());

            ObjectNode deleteCreditCard = mutateGraphQLService(url, "", deleteCreditCardQuery);
            data = deleteCreditCard.get("data");
            if(data == null)
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Hubo un error al eliminar la tarjeta de credito");

            return ResponseEntity.status(HttpStatus.OK).body("La tarjeta fue eliminada con exito");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("No se pudo eliminar la tarjeta: Ocurrio un error inesperado");
        }
    }

    static ObjectNode queryGraphQLService(String url, String operation, String query) throws URISyntaxException,
            IOException, GraphQLException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("query", query)
                .build();
        request.setURI(uri);
        HttpResponse response =  client.execute(request);
        InputStream inputResponse = response.getEntity().getContent();
        String actualResponse = new BufferedReader(
                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
        return node;
    }

    static ObjectNode mutateGraphQLService(String url, String operation, String query) throws URISyntaxException,
            IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("query", query)
                .build();
        request.setURI(uri);
        HttpResponse response =  client.execute(request);
        InputStream inputResponse = response.getEntity().getContent();
        String actualResponse = new BufferedReader(
                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
        return node;
    }


}
