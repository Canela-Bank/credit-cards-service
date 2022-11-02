package com.canela.service.creditcardmgmt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class CreditCardBadRequestAdvice {
	
	@ResponseBody
	@ExceptionHandler(CreditCardBadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String clienteBadRequestHandler(CreditCardBadRequestException ex) {
		return "Missing data";
	}
}
