package com.canela.service.creditcardmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class CreditCardMgmtApplication {
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CreditCardMgmtApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(CreditCardMgmtApplication.class, args);
	}

}
