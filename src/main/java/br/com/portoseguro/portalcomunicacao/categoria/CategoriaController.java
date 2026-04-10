package br.com.portoseguro.portalcomunicacao.categoria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Criar categoria", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)")
    })
    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.criar(request);
    }

    @Operation(summary = "Listar categorias ativas", tags = {"Portal Público"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar(true);
    }

    @Operation(summary = "Listar todas as categorias", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)")
    })
    @GetMapping("/todas")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public List<CategoriaResponse> listarTodos() {
        return categoriaService.listar(null);
    }

    @Operation(summary = "Buscar categoria por ID", tags = {"Portal Público"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/{id}")
    public CategoriaResponse buscaPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    @Operation(summary = "Atualizar categoria", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.atualizar(id, request);
    }

    @Operation(summary = "Inativar categoria", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria inativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public CategoriaResponse inativar(@PathVariable Long id) {
        return categoriaService.inativar(id);
    }

    @Operation(summary = "Ativar categoria", tags = {"Portal Administrativo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria ativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (Admin/Producer)"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER')")
    public CategoriaResponse ativar(@PathVariable Long id) {
        return categoriaService.ativar(id);
    }
}
