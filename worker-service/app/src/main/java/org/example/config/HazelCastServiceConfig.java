package org.example.config;

import org.example.service.HazelCastService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelCastServiceConfig {
    @Bean
    public HazelCastService hazelCastService() {
        return new HazelCastService();
    };
}
