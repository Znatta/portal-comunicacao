package br.com.portoseguro.portalcomunicacao.categoria;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse criar(CategoriaRequest request) {
        log.info("Iniciando criação de categoria: {}", request.nome());
        Categoria categoria = new Categoria();

        categoria.setNome(request.nome());
        categoria.setAtivo(request.ativo());

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        log.info("Categoria criada com sucesso. ID: {}", categoriaSalva.getId());

        return new CategoriaResponse(categoriaSalva);
    }

    public List<CategoriaResponse> listar(Boolean ativo) {
        log.debug("Listando categorias. Filtro ativo: {}", ativo);
        List<Categoria> categorias = (ativo == null)
                ? categoriaRepository.findAll()
                : categoriaRepository.findDistinctByAtivoTrueAndNoticiasAtivoTrue();

        return categorias.stream()
                .map(categoria -> new CategoriaResponse(categoria))
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id, Boolean ativo) {
        log.debug("Buscando categoria por ID: {} (Filtro ativo: {})", id, ativo);
        return categoriaRepository.findById(id)
                .filter(categoria -> ativo == null || !ativo || categoria.getAtivo())
                .map(categoria -> new CategoriaResponse(categoria))
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada com ID: {}", id);
                    return new EntityNotFoundException("Categoria não encontrada com o ID: " + id);
                });
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        log.info("Iniciando atualização da categoria ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNome(request.nome());
                    categoria.setAtivo(request.ativo());

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);
                    log.info("Categoria ID: {} atualizada com sucesso.", id);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Categoria ID {} não encontrada", id);
                    return new EntityNotFoundException("Categoria não encontrada com o ID: " + id);
                });
    }

    public CategoriaResponse inativar(Long id) {
        log.info("Inativando categoria ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoria -> {

                    categoria.setAtivo(false);

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);
                    log.info("Categoria ID: {} inativada com sucesso.", id);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao inativar: Categoria ID {} não encontrada", id);
                    return new EntityNotFoundException("Categoria não encontrada com o ID: " + id);
                });
    }

    public CategoriaResponse ativar(Long id) {
        log.info("Ativando categoria ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoria -> {

                    categoria.setAtivo(true);

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);
                    log.info("Categoria ID: {} ativada com sucesso.", id);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao ativar: Categoria ID {} não encontrada", id);
                    return new EntityNotFoundException("Categoria não encontrada com o ID: " + id);
                });
    }
}
