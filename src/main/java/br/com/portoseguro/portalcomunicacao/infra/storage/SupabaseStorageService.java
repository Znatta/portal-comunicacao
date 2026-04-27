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
@Slf4j
public class SupabaseStorageService implements Storage {

    private final SupabaseProperties properties;
    private final WebClient supabaseWebClient;

    @Override
    public String salvar(MultipartFile arquivo) {
        String nomeArquivo = UUID.randomUUID() + "-" + arquivo.getOriginalFilename();
        log.info("Iniciando upload de arquivo para o Supabase: {} (Nome gerado: {})", arquivo.getOriginalFilename(), nomeArquivo);

        try {
            supabaseWebClient.post()
                    .uri("/object/{bucket}/{file}", properties.bucket(), nomeArquivo)
                    .header("Content-Type", arquivo.getContentType())
                    .bodyValue(arquivo.getBytes())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Erro na API do Supabase durante o upload do arquivo {}: {}", nomeArquivo, error);
                                    return Mono.error(new RuntimeException("Falha no upload: " + error));
                                })
                    )
                    .toBodilessEntity()
                    .block();

            log.info("Upload concluído com sucesso para o arquivo: {}", nomeArquivo);
            return nomeArquivo;

        } catch (IOException e) {
            log.error("Erro de E/S ao processar os bytes do arquivo: {}", arquivo.getOriginalFilename(), e);
            throw new RuntimeException("Erro ao realizar upload", e);
        }
    }

    @Override
    public void excluir(String nomeArquivo) {
        log.info("Iniciando solicitação de exclusão para o arquivo: {}", nomeArquivo);
        try {
            supabaseWebClient.delete()
                    .uri("/object/{bucket}/{file}", properties.bucket(), nomeArquivo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Erro na API do Supabase durante a exclusão do arquivo {}: {}", nomeArquivo, error);
                                    return Mono.error(new RuntimeException("Falha na deleção: " + error));
                                })
                    )
                    .toBodilessEntity()
                    .block();
            log.info("Arquivo excluído com sucesso do Storage: {}", nomeArquivo);
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir arquivo: {}", nomeArquivo, e);
            throw new RuntimeException("Erro ao realizar delete", e);
        }
    }
}
