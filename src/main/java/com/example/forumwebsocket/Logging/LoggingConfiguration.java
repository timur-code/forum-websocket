package com.example.forumwebsocket.Logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {
    @Bean
    public RequestAndResponseLoggingFilter requestResponseLoggingFilter() {
        return new RequestAndResponseLoggingFilter();
    }
}