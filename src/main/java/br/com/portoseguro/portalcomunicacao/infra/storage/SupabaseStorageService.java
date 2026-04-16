package br.com.portoseguro.portalcomunicacao.infra.storage;

import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService implements Storage {

    private final SupabaseProperties properties;
    private final WebClient supabaseWebClient;

    @Override
    public String salvar(MultipartFile arquivo) {
        String nomeArquivo = UUID.randomUUID() + "-" + arquivo.getOriginalFilename();

        try {
            supabaseWebClient.post()
                    .uri("/object/{bucket}/{file}", properties.bucket(), nomeArquivo)
                    .header("Content-Type", arquivo.getContentType())
                    .bodyValue(arquivo.getBytes())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    return Mono.error(new RuntimeException("Falha no upload: " + error));
                                })
                    )
                    .toBodilessEntity()
                    .block();

            return nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao realizar upload", e);
        }
    }

    @Override
    public void excluir(String nomeArquivo) {
        try {
            supabaseWebClient.delete()
                    .uri("/object/{bucket}/{file}", properties.bucket(), nomeArquivo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(error -> { 
                                    return Mono.error(new RuntimeException("Falha na deleção: " + error));
                                })
                    )
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar delete", e);
        }
    }
}
