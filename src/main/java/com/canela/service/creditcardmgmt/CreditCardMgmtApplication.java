package com.canela.service.creditcardmgmt;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class })
public class CreditCardMgmtApplication {
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CreditCardMgmtApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(CreditCardMgmtApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${application.name}") String appName,
								 @Value("${application.description}") String description) {
		return new OpenAPI().components(new Components()).info(new Info().title(appName).description(description));
	}

}
