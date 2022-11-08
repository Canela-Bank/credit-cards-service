package com.canela.service.creditcardmgmt.controllers;

import com.canela.service.creditcardmgmt.entities.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


@RestController
@RequestMapping(value = "/api/credit-card")
@Tag(name = "Account", description = "Account REST API")
public class CardPayController {

    @Operation(summary = "Pay with credit card", description = "Connect to the Visa network to pay with credit card", tags = {"Credit card"})
    @PutMapping(value = "pay/{cardNumber}")
    @CrossOrigin("*")
    public ResponseEntity<String> pay (@PathVariable(value = "cardNumber") String cardNumber, @RequestBody PayRequest req){

        try {
            //Connection with GraphQL
            URL getCreditCardUrl = new URL("http://localhost:3001/graphql?query=query%7BgetCreditCardByNumber(number%3A" + cardNumber + ")%7B%0A%20%20%09number%2C%0A%20%20%09cvv%2C%0A%20%20%09exp_date%2C%0A%20%20%09card_name%2C%20%0A%20%20%09advancement_amount%2C%0A%20%20%09debt%2C%0A%20%20%09used_advancement%2C%0A%20%20%09user_id%0A%09%7D%0A%7D");
            HttpURLConnection connCreditCard = (HttpURLConnection) getCreditCardUrl.openConnection();
            connCreditCard.setRequestMethod("GET");

            //If the connection is successful
            if(connCreditCard.getResponseCode() == HttpURLConnection.HTTP_OK){
                //Obtain body information
                BufferedReader in = new BufferedReader(new InputStreamReader(connCreditCard.getInputStream()));
                String inputLine;
                StringBuilder responseBuff = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseBuff.append(inputLine);
                }
                in.close();

                //Parse to JSON the String obtained
                JSONObject jsonData = new JSONObject(responseBuff.toString());
                JSONObject jsonGetCreditCard = new JSONObject(jsonData.get("data").toString());

                if(jsonGetCreditCard.get("getCreditCardByNumber").toString().equals("null")){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la tarjeta de crédito");
                } else {
                    String creditCardInfo = jsonGetCreditCard.get("getCreditCardByNumber").toString();
                    JSONObject jsonCreditCard = new JSONObject(creditCardInfo);

                    double currentDebt = Double.parseDouble(jsonCreditCard.get("debt").toString());

                    //TODO: Check the authorized amount
                    if(currentDebt > 5000000D){
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Transacción denegada");
                    } else {
                        double newDebt = currentDebt + req.getPrice();

                        //Update balance of the account
                        String cvv = jsonCreditCard.get("cvv").toString();
                        String expDate = jsonCreditCard.get("exp_date").toString().replace(" ", "%20").replace(":", "%3A");
                        String cardName = jsonCreditCard.get("card_name").toString().replace(" ", "%20");
                        String userId = jsonCreditCard.get("user_id").toString();
                        String usedAdvancement = jsonCreditCard.get("used_advancement").toString();
                        String advancementAmount = jsonCreditCard.get("advancement_amount").toString();

                        URL updateCreditCard = new URL("http://localhost:3001/graphql?query=mutation%7B%20createCreditCard(number%3A%20"+cardNumber+"%2C%20cvv%3A%20"+cvv+"%2C%20exp_date%3A%20%22"+expDate+"%22%2C%20card_name%3A%20%22"+cardName+"%22%2C%20advancement_amount%3A%20"+advancementAmount+"%2C%20debt%3A%20"+newDebt+"%2C%20used_advancement%3A%20"+usedAdvancement+"%2C%20user_id%3A%20%22"+userId+"%22)%7B%0A%20%20%09number%2C%0A%20%20%09cvv%2C%0A%20%20%09exp_date%2C%0A%20%20%09card_name%2C%0A%20%20%09advancement_amount%2C%0A%20%20%09used_advancement%2C%0A%20%20%09debt%2C%0A%20%20%09user_id%0A%09%7D%0A%7D");
                        HttpURLConnection connUpdate = (HttpURLConnection) updateCreditCard.openConnection();
                        connUpdate.setRequestMethod("POST");

                        if (connUpdate.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Transacción aprovada");
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transacción denegada");
                        }
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitud rechazada");
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
    static class PayRequest {
        Float price;
        String desAcc;

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public String getDesAcc() {
            return desAcc;
        }

        public void setDesAcc(String desAcc) {
            this.desAcc = desAcc;
        }
    }

}