package com.sanatoryApp.api_gate_way.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * Configures the Feign log level.
     * FULL = logs headers, body, request and response metadata.
     * Useful for debugging during development.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Customised handling of HTTP errors in Feign calls
     * By default, Feign throws generic exceptions, which gives us more control
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();

}
}
