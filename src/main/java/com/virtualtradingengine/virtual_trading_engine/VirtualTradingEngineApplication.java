package com.virtualtradingengine.virtual_trading_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@OpenAPIDefinition(
		info = @Info(
				title = "Virtual Trading Engine API",
				version = "v1"
		)
)
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class VirtualTradingEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualTradingEngineApplication.class, args);
	}

}
