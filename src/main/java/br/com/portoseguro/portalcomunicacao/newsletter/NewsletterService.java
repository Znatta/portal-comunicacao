package br.com.portoseguro.portalcomunicacao.newsletter;

import br.com.portoseguro.portalcomunicacao.config.BrevoProperties;
import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import br.com.portoseguro.portalcomunicacao.noticia.Noticia;
import br.com.portoseguro.portalcomunicacao.noticia.NoticiaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {
    private final NewsletterRepository newsletterRepository;
    private final NoticiaRepository noticiaRepository;
    private final WebClient brevoWebClient;
    private final TemplateEngine templateEngine;
    private final BrevoProperties brevoProperties;
    private final SupabaseProperties supabaseProperties;

    private String getPublicUrl() {
        return supabaseProperties.url() + "/storage/v1/object/public/" + supabaseProperties.bucket() + "/";
    }

    public void inscrever(NewsletterRequest request){
        log.info("Processando inscrição para o e-mail: {}", request.email());
        Optional<Newsletter> existente = newsletterRepository.findByEmail(request.email());

        if (existente.isPresent()){
            Newsletter n = existente.get();
            if (!n.getAtivo()){
                log.info("Reativando inscrição inativa para o e-mail: {}", request.email());
                n.setAtivo(true);
                newsletterRepository.save(n);
            } else {
                log.debug("E-mail {} já possui uma inscrição ativa.", request.email());
            }
            return;
        }

        Newsletter inscrito = new Newsletter();
        inscrito.setEmail(request.email());
        inscrito.setDataInscricao(LocalDateTime.now());
        inscrito.setAtivo(true);
        inscrito.setTokenUnsubscribe(UUID.randomUUID());

        newsletterRepository.save(inscrito);
        log.info("Novo inscrito salvo com sucesso: {}", request.email());
    }

    public void cancelarInscricao(UUID uuid){
        log.info("Iniciando processo de cancelamento para o token: {}", uuid);
        Newsletter inscrito = newsletterRepository.findByTokenUnsubscribe(uuid)
                .orElseThrow(() -> {
                    log.warn("Falha no unsubscribe: Token {} não encontrado.", uuid);
                    return new EntityNotFoundException("Inscrito não encontrado com o UUID: " + uuid);
                });

        inscrito.setAtivo(false);
        newsletterRepository.save(inscrito);
        log.info("Inscrição cancelada com sucesso para o e-mail: {}", inscrito.getEmail());
    }

    @Async
    public void enviarNewsletterDiaria() {
        log.info("Iniciando processo assíncrono de envio da newsletter diária.");
        
        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDateTime inicio = ontem.atStartOfDay();
        LocalDateTime fim = ontem.atTime(LocalTime.MAX);

        List<Noticia> noticias = noticiaRepository.findAllByAtivoTrueAndDataPublicacaoBetweenOrderByDataPublicacaoDesc(inicio, fim);

        if (noticias.isEmpty()) {
            log.warn("Newsletter cancelada: Nenhuma notícia publicada em {}", ontem);
            return;
        }

        List<Newsletter> inscritos = newsletterRepository.findAllByAtivoTrue();
        
        if (inscritos.isEmpty()) {
            log.warn("Newsletter cancelada: Nenhum inscrito ativo na base de dados.");
            return;
        }

        String dataFormatada = ontem.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        log.info("Lote de envio preparado: {} notícias para {} inscritos.", noticias.size(), inscritos.size());

        int sucessos = 0;
        int falhas = 0;

        for (Newsletter inscrito : inscritos) {
            try {
                enviarParaInscrito(inscrito, noticias, dataFormatada);
                sucessos++;
            } catch (Exception e) {
                falhas++;
                log.error("Erro ao enviar newsletter para {}: {}", inscrito.getEmail(), e.getMessage());
            }
        }
        
        log.info("Processamento da newsletter finalizado. Sucessos: {}, Falhas: {}", sucessos, falhas);
    }

    private void enviarParaInscrito(Newsletter inscrito, List<Noticia> noticias, String data) {
        log.debug("Preparando template de e-mail para: {}", inscrito.getEmail());
        
        Context context = new Context();
        context.setVariable("noticias", noticias);
        context.setVariable("baseUrl", getPublicUrl());
        context.setVariable("token", inscrito.getTokenUnsubscribe());
        context.setVariable("urlUnsubscribe", "https://portaldeleitura.vercel.app/newsletter/unsubscribe");

        String htmlContent = templateEngine.process("newsletter", context);

        BrevoEmailRequest emailRequest = new BrevoEmailRequest(
            new BrevoSender(brevoProperties.fromName(), brevoProperties.fromEmail()),
            List.of(new BrevoRecipient(inscrito.getEmail())),
            "Insights Porto - " + data,
            htmlContent
        );

        brevoWebClient.post()
            .bodyValue(emailRequest)
            .retrieve()
                .toBodilessEntity()
                .doOnSuccess(v -> log.debug("E-mail enviado com sucesso para a fila do Brevo: {}", inscrito.getEmail()))
                .doOnError(e -> log.error("Falha na chamada à API do Brevo para {}: {}", inscrito.getEmail(), e.getMessage()))
                .subscribe();
    }

    // DTOs Internos para a API do Brevo
    private record BrevoEmailRequest(BrevoSender sender, List<BrevoRecipient> to, String subject, String htmlContent) {}
    private record BrevoSender(String name, String email) {}
    private record BrevoRecipient(String email) {}
}
