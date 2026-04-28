package br.com.portoseguro.portalcomunicacao.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebClientConfig {
    private final SupabaseProperties supabaseProperties;
    private final BrevoProperties brevoProperties;

    @Bean
    public WebClient supabaseWebClient(WebClient.Builder builder) {
        log.info("Inicializando Supabase WebClient...");
        return builder
            .baseUrl(supabaseProperties.url() + "/storage/v1") // Path base do Storage
            .defaultHeader("apiKey", supabaseProperties.key())
            .defaultHeader("Authorization", "Bearer " + supabaseProperties.key())
            .build();
    }

    @Bean
    public WebClient brevoWebClient(WebClient.Builder builder) {
        log.info("Inicializando Brevo WebClient...");
        return builder
            .baseUrl("https://api.brevo.com/v3/smtp/email")
            .defaultHeader("api-key", brevoProperties.apiKey())
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
