package br.com.portoseguro.portalcomunicacao.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroPadrao> tratarEntityNotFound(EntityNotFoundException ex) {
        // Rastreabilidade: Grava o log de erro no servidor para o time de backend monitorar
        log.error("Falha de busca: Recurso não encontrado no banco de dados. Detalhes técnicos: {}", ex.getMessage());

        // Monta o payload JSON limpo e seguro para o front-end
        ErroPadrao erro = new ErroPadrao(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                "O ID fornecido na requisição não existe no sistema."
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        log.warn("Falha de validação: Dados enviados na requisição estão incorretos.");
        
        var erros = ex.getFieldErrors().stream()
                .map(DadosErroValidacao::new)
                .toList();

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadrao> tratarErro500(Exception ex) {
        log.error("Erro interno inesperado: {}", ex.getMessage(), ex);

        ErroPadrao erro = new ErroPadrao(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    // Record auxiliar para detalhar erros de validação por campo
    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    // Record utilizado para padronizar a resposta de erro em formato JSON
    public record ErroPadrao(
            int status,
            String erro,
            String mensagem,
            LocalDateTime timestamp
    ) {
        // Construtor auxiliar para preencher o timestamp automaticamente
        public ErroPadrao(int status, String erro, String mensagem) {
            this(status, erro, mensagem, LocalDateTime.now());
        }
    }
}
