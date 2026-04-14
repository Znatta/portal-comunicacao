package br.com.portoseguro.portalcomunicacao.noticia;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record NoticiaRequest(
        @NotBlank String titulo,
        @NotBlank String subtitulo,
        @NotBlank String conteudo,
        @NotNull @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataPublicacao,
        @NotNull Boolean ativo,
        @NotNull Long categoriaId,
        @NotNull Long autorId) {
}
