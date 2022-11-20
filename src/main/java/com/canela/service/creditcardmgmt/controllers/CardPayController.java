package com.canela.service.creditcardmgmt.controllers;

import com.canela.service.creditcardmgmt.entities.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


@RestController
@RequestMapping(value = "/api/credit-card")
@Tag(name = "Account", description = "Account REST API")
public class CardPayController {

    @Operation(summary = "Pay with credit card", description = "Connect to the Visa network to pay with credit card", tags = {"Credit card"})
    @PutMapping(value = "pay/{accountId}")
    @CrossOrigin("*")
    public ResponseEntity<String> pay ( @RequestBody PayRequest req){

        String response = null;
        try {
            URL url = new URL("http://localhost:9010/api/GraphQL");
            CreditCard cred = new CreditCard("332132323", 520, new Date(), "Amaris Aroyyo", (float) 5.2, (float) 1.1, 200000F, "123");
            if (cred.getDebt() > 5000000F){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Transaccion denegada ");
            }
            else {
                cred.setDebt(cred.getDebt()+ req.getPrice());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Transaccion aprovada ");
            }
        } catch (MalformedURLException e) {
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