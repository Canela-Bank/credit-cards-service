package com.canela.service.creditcardmgmt.controllers;

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
import java.net.ProtocolException;
import java.net.URL;

@RestController
@RequestMapping(value = "/api/credit-cards")
public class CardPayDebtController {

    @PutMapping(value = "pay-debt/{cardNumber}")
    public ResponseEntity<String> payDebt (@PathVariable(value = "cardNumber") String cardNumber, @RequestBody PayDebtRequest payDebtRequest) {
        try {
            URL getCreditCardUrl = new URL("http://localhost:3002/graphql?query=query%20{%0A%20%20getCreditCardByNumber(number%3A"+cardNumber+")%20{%0A%20%20%20%20number%0A%20%20%20%20cvv%0A%20%20%20%20exp_date%0A%20%20%20%20card_name%0A%20%20%20%20debt%0A%20%20%20%20user_id%0A%20%20%20%20user_document_type%0A%20%20}%0A}");
            HttpURLConnection connCreditCard = (HttpURLConnection) getCreditCardUrl.openConnection();
            connCreditCard.setRequestMethod("GET");

            if(connCreditCard.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connCreditCard.getInputStream()));
                String inputLine;
                StringBuilder responseBuff = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseBuff.append(inputLine);
                }
                in.close();

                JSONObject jsonData = new JSONObject(responseBuff.toString());
                JSONObject jsonGetCreditCard = new JSONObject(jsonData.get("data").toString());
                if(jsonGetCreditCard.get("getCreditCardByNumber").toString().equals("null")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontro la tarjeta");
                }
                String creditCardInfo = jsonGetCreditCard.get("getCreditCardByNumber").toString();
                JSONObject jsonCreditCard = new JSONObject(creditCardInfo);

                double currentDebt = Double.parseDouble(jsonCreditCard.get("debt").toString());

                JSONObject account = this.requestAccount(payDebtRequest.account_id);
                if(account == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The account in question was not found");
                }

                double account_balance = Double.parseDouble(account.get("balance").toString());

                if(account_balance < currentDebt) {
                    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("The account does not have enough funds");
                }

                //Updates account and creditCard
                account.put("balance", account_balance-currentDebt);
                String id = account.get("id").toString();
                double balance = Double.parseDouble(account.get("balance").toString());
                String user_id = account.get("user_id").toString();
                int user_document_type = Integer.parseInt(account.get("user_document_type").toString());

                //Credit card values
                int number = jsonCreditCard.getInt("number");
                int cvv = jsonCreditCard.getInt("cvv");
                String expDate = jsonCreditCard.getString("exp_date");
                String card_name = jsonCreditCard.getString("card_name");

                jsonCreditCard.put("debt", 0.0);
                double debt = jsonCreditCard.getDouble("debt");
                String user_id_cc = jsonCreditCard.getString("user_id");
                String user_document_type_cc = jsonCreditCard.getString("user_document_type");

                String updateUrl = "http://localhost:3002/graphql?query=mutation%20%7B%0A%20%20createCreditCard(number%3A%20%20"+number+"%20%2C%20cvv%3A%20"+cvv+"%2C%20exp_date%3A%20%22"+expDate.replace("T", " ").replace("Z", "")+"%22%2C%20card_name%3A%20%22"+card_name+"%22%2C%20debt%3A%20"+debt+"%2C%20user_id%3A%20%22"+user_id_cc+"%22%2C%20user_document_type%3A%20"+user_document_type_cc+")%20%7B%0A%20%20%20%20number%0A%20%20%20%20cvv%0A%20%20%20%20exp_date%0A%20%20%20%20card_name%0A%20%20%20%20debt%0A%20%20%20%20user_id%0A%20%20%20%20user_document_type%0A%20%20%7D%0A%7D";
                URL update = new URL(updateUrl);
                HttpURLConnection connUpdate = (HttpURLConnection) update.openConnection();
                connUpdate.setRequestMethod("POST");
                if (connCreditCard.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return ResponseEntity.status(HttpStatus.OK).body("Solicitud aprovada");
                }

            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitud rechazada");
            }

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error");
    }

    private JSONObject requestAccount(String accountId) {

        try {
            String url = "http://localhost:3002/graphql?query=query%20%7B%0A%20%20getAccountById(id%3A%20%22"+accountId+"%22)%20%7B%0A%20%20%20%20id%0A%20%20%20%20balance%0A%20%20%20%20user_id%0A%20%20%20%20user_document_type%0A%20%20%7D%0A%7D";
            URL getCreditCardUrl = new URL(url);
            HttpURLConnection connCreditCard = (HttpURLConnection) getCreditCardUrl.openConnection();
            connCreditCard.setRequestMethod("POST");

            if (connCreditCard.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connCreditCard.getInputStream()));
                String inputLine;
                StringBuilder responseBuff = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseBuff.append(inputLine);
                }
                in.close();

                JSONObject jsonData = new JSONObject(responseBuff.toString());
                JSONObject jsonGetAccount = new JSONObject(jsonData.get("data").toString());
                if(jsonGetAccount.get("getAccountById").toString().equals("null")) {
                    return null;
                }
                String accountInfo = jsonGetAccount.get("getAccountById").toString();
                return new JSONObject(accountInfo);
            }
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    static class PayDebtRequest {
        //TODO There is a massive security problem here.
        String account_id;

        public String getAccount_id() {
            return account_id;
        }

        public void setAccount_id(String account_id) {
            this.account_id = account_id;
        }
    }

}
