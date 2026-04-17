package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.Categoria;
import br.com.portoseguro.portalcomunicacao.categoria.CategoriaRepository;
import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import br.com.portoseguro.portalcomunicacao.infra.storage.Storage;
import br.com.portoseguro.portalcomunicacao.usuario.Usuario;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final Storage storage;
    private final SupabaseProperties properties;

    private String getPublicUrl() {
        return properties.url() + "/storage/v1/object/public/" + properties.bucket() + "/";
    }

    @Transactional
    public NoticiaResponse criar(NoticiaRequest request, MultipartFile arquivo) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + request.categoriaId()));

        Usuario autor = usuarioRepository.findById(request.autorId())
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + request.autorId()));

        String imagemSalva = null;
        if (arquivo != null && !arquivo.isEmpty()) {
            imagemSalva = storage.salvar(arquivo);
        }

        try {
            Noticia noticia = new Noticia();
            noticia.setTitulo(request.titulo());
            noticia.setSubtitulo(request.subtitulo());
            noticia.setConteudo(request.conteudo());
            noticia.setDataPublicacao(request.dataPublicacao());
            noticia.setAtivo(request.ativo());
            noticia.setCategoria(categoria);
            noticia.setAutor(autor);
            noticia.setImagem(imagemSalva);

            Noticia noticiaSalva = noticiaRepository.save(noticia);
            return new NoticiaResponse(noticiaSalva, getPublicUrl());

        } catch (Exception e) {
            if (imagemSalva != null) {
                storage.excluir(imagemSalva);
            }
            throw e;
        }
    }

    public Page<NoticiaResponse> listar(String busca, Long categoriaId, Boolean ativo, Pageable pageable){
        Specification<Noticia> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if (busca != null && !busca.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String padraoBusca = "%" + busca.toLowerCase() + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), padraoBusca),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("subtitulo")), padraoBusca),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("conteudo")), padraoBusca)
                );
            });
        }

        if (categoriaId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("categoria").get("id"), categoriaId)
            );
        }

        if (ativo != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ativo"), ativo));
        }

        Page<Noticia> noticias = noticiaRepository.findAll(spec, pageable);

        return  noticias.map(noticia -> new NoticiaResponse(noticia, getPublicUrl()));
    }

    public NoticiaResponse buscarPorId(Long id, Boolean ativo) {
        return noticiaRepository.findById(id)
                .filter(noticia -> ativo == null || noticia.getAtivo().equals(ativo))
                .map(noticia -> new NoticiaResponse(noticia, getPublicUrl()))
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse atualizar(Long id, NoticiaRequest request, MultipartFile arquivo) {
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));

        String imagemAntiga = noticia.getImagem();
        String novaImagem = null;

        if (arquivo != null && !arquivo.isEmpty()) {
            novaImagem = storage.salvar(arquivo);
        }

        try {
            noticia.setTitulo(request.titulo());
            noticia.setSubtitulo(request.subtitulo());
            noticia.setConteudo(request.conteudo());
            noticia.setDataPublicacao(request.dataPublicacao());
            noticia.setAtivo(request.ativo());

            Categoria categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + request.categoriaId()));
            Usuario autor = usuarioRepository.findById(request.autorId())
                    .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + request.autorId()));

            noticia.setCategoria(categoria);
            noticia.setAutor(autor);

            if (novaImagem != null) {
                noticia.setImagem(novaImagem);
            }

            Noticia noticiaSalva = noticiaRepository.save(noticia);

            if (novaImagem != null && imagemAntiga != null) {
                storage.excluir(imagemAntiga);
            }

            return new NoticiaResponse(noticiaSalva, getPublicUrl());

        } catch (Exception e) {
            if (novaImagem != null) {
                storage.excluir(novaImagem);
            }
            throw e;
        }
    }

    @Transactional
    public NoticiaResponse inativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setAtivo(false);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    return new NoticiaResponse(noticiaAtualizada, getPublicUrl());
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse ativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setAtivo(true);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    return new NoticiaResponse(noticiaAtualizada, getPublicUrl());
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }
}
