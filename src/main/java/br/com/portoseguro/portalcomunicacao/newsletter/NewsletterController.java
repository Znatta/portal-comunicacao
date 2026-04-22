package br.com.portoseguro.portalcomunicacao.newsletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {
    private final NewsletterService newsletterService;

    @Operation(summary = "Assinar Newsletter", tags = {"Portal Público"})
    @ApiResponse(responseCode = "201", description = "Assinado com sucesso")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping()
    public void inscrever(@RequestBody @Valid NewsletterRequest request) {
        newsletterService.inscrever(request);
    }

    @Operation(summary = "Cancelar Inscrição", tags = {"Portal Público"})
    @ApiResponse(responseCode = "200", description = "Cancelado com sucesso")
    @ResponseStatus(code = HttpStatus.OK)
    @PatchMapping("/unsubscribe/{uuid}")
    public void cancelarInscricao(@PathVariable UUID uuid) {
        newsletterService.cancelarInscricao(uuid);
    }

    @Operation(summary = "Enviar Newsletter (manual)", tags = {"Portal Administrativo"})
    @ApiResponse(responseCode = "202", description = "Processamento de envio iniciado")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @GetMapping("/enviar-diario")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void dispararNewsletter() {
        newsletterService.enviarNewsletterDiaria();
    }
}
