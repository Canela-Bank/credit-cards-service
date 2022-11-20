package com.canela.service.creditcardmgmt.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class CreditCard {

    @Id
    private String number;
    private Integer cvv;
    private Date expDate;
    private String cardName;
    private Float advancementAmount;
    private Float usedAdvancement;
    private Float debt;
    private String userId;

    public CreditCard(String number, Integer cvv, Date expDate, String cardName, Float advancementAmount, Float usedAdvancement, Float debt, String userId) {
        this.number = number;
        this.cvv = cvv;
        this.expDate = expDate;
        this.cardName = cardName;
        this.advancementAmount = advancementAmount;
        this.usedAdvancement = usedAdvancement;
        this.debt = debt;
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }



    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Float getAdvancementAmount() {
        return advancementAmount;
    }

    public void setAdvancementAmount(Float advancementAmount) {
        this.advancementAmount = advancementAmount;
    }

    public Float getUsedAdvancement() {
        return usedAdvancement;
    }

    public void setUsedAdvancement(Float usedAdvancement) {
        this.usedAdvancement = usedAdvancement;
    }

    public Float getDebt() {
        return debt;
    }

    public void setDebt(Float debt) {
        this.debt = debt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
