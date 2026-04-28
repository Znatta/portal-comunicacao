package br.com.portoseguro.portalcomunicacao.usuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar resumo de autores", description = "Retorna apenas ID e Nome de usuários ativos para composição de filtros.", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping("/autores")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUCER')")
    public List<UsuarioResumoResponse> listarAutores() {
        log.info("Requisição para listar resumo de autores (ativos)");
        return usuarioService.listarAutores();
    }

    @Operation(summary = "Criar usuário", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)")
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        log.info("Requisição para criar novo usuário: {} ({})", request.nome(), request.email());
        return usuarioService.criar(request);
    }

    @Operation(summary = "Listar usuários", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<UsuarioResponse> listar(
            @Parameter(description = "Filtro parcial por nome do usuário (ignore case)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Filtro parcial por e-mail do usuário (ignore case)")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filtro exato por status (true = ativo, false = inativo)")
            @RequestParam(required = false) Boolean ativo,

            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "nome")
            Pageable pageable) {
        log.info("Requisição para listar usuários. Filtros - Nome: {}, Email: {}, Ativo: {}", nome, email, ativo);
        return usuarioService.listar(nome, email, ativo, pageable);
    }

    @Operation(summary = "Buscar usuário por ID", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UsuarioResponse buscaPorId(@PathVariable Long id) {
        log.info("Requisição para buscar usuário ID: {}", id);
        return usuarioService.buscarPorId(id);
    }

    @Operation(summary = "Atualizar usuário", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioAtualizacaoRequest request) {
        log.info("Requisição para atualizar usuário ID: {}", id);
        return usuarioService.atualizar(id, request);
    }

    @Operation(summary = "Inativar usuário", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UsuarioResponse inativar(@PathVariable Long id) {
        log.info("Requisição para inativar usuário ID: {}", id);
        return usuarioService.inativar(id);
    }

    @Operation(summary = "Ativar usuário", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UsuarioResponse ativar(@PathVariable Long id) {
        log.info("Requisição para ativar usuário ID: {}", id);
        return usuarioService.ativar(id);
    }
}
