package br.com.portoseguro.portalcomunicacao.newsletter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsletterService {
    private final NewsletterRepository newsletterRepository;

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
}
