package com.virtualtradingengine.virtual_trading_engine.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Configuration
public class JacksonConfig {

    @Bean
    public Module bigDecimalModule() {
        SimpleModule module = new SimpleModule();

        module.addSerializer(BigDecimal.class, new com.fasterxml.jackson.databind.JsonSerializer<>() {
            @Override
            public void serialize(BigDecimal value,
                                  com.fasterxml.jackson.core.JsonGenerator gen,
                                  com.fasterxml.jackson.databind.SerializerProvider serializers)
                    throws java.io.IOException {

                BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
                gen.writeNumber(scaled);
            }
        });

        return module;
    }
}