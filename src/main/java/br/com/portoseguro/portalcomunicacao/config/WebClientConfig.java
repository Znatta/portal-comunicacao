package br.com.portoseguro.portalcomunicacao.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final SupabaseProperties supabaseProperties;

    @Bean
    public WebClient supabaseWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl(supabaseProperties.url() + "/storage/v1") // Path base do Storage
            .defaultHeader("apiKey", supabaseProperties.key())
            .defaultHeader("Authorization", "Bearer " + supabaseProperties.key())
            .build();
    }
}
