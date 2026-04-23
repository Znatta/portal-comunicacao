package br.com.portoseguro.portalcomunicacao.categoria;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse criar(CategoriaRequest request) {
        Categoria categoria = new Categoria();

        categoria.setNome(request.nome());
        categoria.setAtivo(request.ativo());

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return new CategoriaResponse(categoriaSalva);
    }

    public List<CategoriaResponse> listar(Boolean ativo) {
        List<Categoria> categorias = (ativo == null)
                ? categoriaRepository.findAll()
                : categoriaRepository.findDistinctByAtivoTrueAndNoticiasAtivoTrue();

        return categorias.stream()
                .map(categoria -> new CategoriaResponse(categoria))
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id, Boolean ativo) {
        return categoriaRepository.findById(id)
                .filter(categoria -> ativo == null || !ativo || categoria.getAtivo())
                .map(categoria -> new CategoriaResponse(categoria))
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNome(request.nome());
                    categoria.setAtivo(request.ativo());

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    }

    public CategoriaResponse inativar(Long id) {
        return categoriaRepository.findById(id)
                .map(categoria -> {

                    categoria.setAtivo(false);

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    }

    public CategoriaResponse ativar(Long id) {
        return categoriaRepository.findById(id)
                .map(categoria -> {

                    categoria.setAtivo(true);

                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);

                    return new CategoriaResponse(categoriaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    }
}
