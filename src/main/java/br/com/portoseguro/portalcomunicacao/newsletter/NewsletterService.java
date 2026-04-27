package br.com.portoseguro.portalcomunicacao.newsletter;

import br.com.portoseguro.portalcomunicacao.config.BrevoProperties;
import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import br.com.portoseguro.portalcomunicacao.noticia.Noticia;
import br.com.portoseguro.portalcomunicacao.noticia.NoticiaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
        Optional<Newsletter> existente = newsletterRepository.findByEmail(request.email());

        if (existente.isPresent()){
            Newsletter n = existente.get();
            if (!n.getAtivo()){
                n.setAtivo(true);
            }
            newsletterRepository.save(n);
            return;
        }

        Newsletter inscrito = new Newsletter();
        inscrito.setEmail(request.email());
        inscrito.setDataInscricao(LocalDateTime.now());
        inscrito.setAtivo(true);
        inscrito.setTokenUnsubscribe(UUID.randomUUID());

        newsletterRepository.save(inscrito);
    }

    public void cancelarInscricao(UUID uuid){
        Newsletter inscrito = newsletterRepository.findByTokenUnsubscribe(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Inscrito não encontrado com o UUID: " + uuid));

        inscrito.setAtivo(false);

        newsletterRepository.save(inscrito);
    }

    @Async
    public void enviarNewsletterDiaria() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDateTime inicio = ontem.atStartOfDay();
        LocalDateTime fim = ontem.atTime(LocalTime.MAX);

        List<Noticia> noticias = noticiaRepository.findAllByAtivoTrueAndDataPublicacaoBetweenOrderByDataPublicacaoDesc(inicio, fim);

        if (noticias.isEmpty()) {
            return;
        }

        List<Newsletter> inscritos = newsletterRepository.findAllByAtivoTrue();
        
        if (inscritos.isEmpty()) {
            return;
        }

        String dataFormatada = ontem.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        for (Newsletter inscrito : inscritos) {
            try {
                enviarParaInscrito(inscrito, noticias, dataFormatada);
            } catch (Exception e) {
                // criar logs depois...
            }
        }
    }

    private void enviarParaInscrito(Newsletter inscrito, List<Noticia> noticias, String data) {
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
                .subscribe();
    }

    // DTOs Internos para a API do Brevo
    private record BrevoEmailRequest(BrevoSender sender, List<BrevoRecipient> to, String subject, String htmlContent) {}
    private record BrevoSender(String name, String email) {}
    private record BrevoRecipient(String email) {}
}
