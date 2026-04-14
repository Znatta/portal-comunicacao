package br.com.portoseguro.portalcomunicacao.noticia;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/noticias")
@RequiredArgsConstructor
public class NoticiaController {

    private final NoticiaService noticiaService;

    @Operation(summary = "Criar notícia", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notícia criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)")
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public NoticiaResponse criar(@Valid @RequestBody NoticiaRequest request) {
        return noticiaService.criar(request);
    }

    @Operation(summary = "Listar notícias ativas", tags = {"Portal Público"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    @GetMapping
    public Page<NoticiaResponse> listar(
            @Parameter(description = "Filtro de texto (ignore case)")
            @RequestParam(required = false) String busca,

            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "titulo")
            Pageable pageable) {
        return noticiaService.listar(busca, true, pageable);
    }

    @Operation(summary = "Listar todas as notícias", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)")
    })
    @GetMapping("/todas")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public Page<NoticiaResponse> listarTodos(
            @Parameter(description = "Filtro de texto (ignore case)")
            @RequestParam(required = false) String busca,

            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "titulo")
            Pageable pageable) {
        return noticiaService.listar(busca, null, pageable);
    }

    @Operation(summary = "Buscar notícia por ID (somente ativas)", tags = {"Portal Público"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notícia não encontrada com o ID fornecido")
    })
    @GetMapping("/{id}")
    public NoticiaResponse buscaPorId(@PathVariable Long id) {
        return noticiaService.buscarPorId(id, true);
    }

    @Operation(summary = "Buscar notícia por ID", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notícia não encontrada com o ID fornecido")
    })
    @GetMapping("/admin/{id}")
    public NoticiaResponse buscaPorIdAdmin(@PathVariable Long id) {
        return noticiaService.buscarPorId(id, null);
    }

    @Operation(summary = "Atualizar notícia", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notícia atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Notícia não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public NoticiaResponse atualizar(@PathVariable Long id, @Valid @RequestBody NoticiaRequest request) {
        return noticiaService.atualizar(id, request);
    }

    @Operation(summary = "Inativar notícia", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notícia inativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Notícia não encontrada")
    })
    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public NoticiaResponse inativar(@PathVariable Long id) {
        return noticiaService.inativar(id);
    }

    @Operation(summary = "Ativar notícia", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notícia ativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Notícia não encontrada")
    })
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public NoticiaResponse ativar(@PathVariable Long id) {
        return noticiaService.ativar(id);
    }
}
