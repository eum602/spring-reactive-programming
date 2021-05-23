package com.eum602.webclientapp.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl(WebClientProperties.BASE_URL)
                .clientConnector(
                        new ReactorClientHttpConnector(
                        HttpClient
                                .create() //builds a netty object
                                .wiretap( //and attaches a configuration for logging
                                        "reactor.netty.client.httpClient",
                                        LogLevel.DEBUG,
                                        AdvancedByteBufFormat.TEXTUAL) //change us the output of the log
                ))
                .build();
    }
}
