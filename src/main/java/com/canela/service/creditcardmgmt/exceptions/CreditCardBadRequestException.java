package com.canela.service.creditcardmgmt.exceptions;

@SuppressWarnings("serial")
public class CreditCardBadRequestException extends RuntimeException {
	
	public CreditCardBadRequestException() {
		
		super("BAD REQUEST");
	}
}
