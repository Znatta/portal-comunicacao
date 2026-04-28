package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.Categoria;
import br.com.portoseguro.portalcomunicacao.categoria.CategoriaRepository;
import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import br.com.portoseguro.portalcomunicacao.infra.storage.Storage;
import br.com.portoseguro.portalcomunicacao.usuario.Usuario;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
        log.info("Iniciando criação de notícia: {}", request.titulo());

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Falha ao criar notícia: Categoria {} não encontrada", request.categoriaId());
                    return new EntityNotFoundException("Categoria não encontrada com ID: " + request.categoriaId());
                });

        Usuario autor = usuarioRepository.findById(request.autorId())
                .orElseThrow(() -> {
                    log.warn("Falha ao criar notícia: Autor {} não encontrado", request.autorId());
                    return new EntityNotFoundException("Autor não encontrado com ID: " + request.autorId());
                });

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
            log.info("Notícia criada com sucesso. ID: {}", noticiaSalva.getId());
            return new NoticiaResponse(noticiaSalva, getPublicUrl());

        } catch (Exception e) {
            log.error("Erro ao salvar notícia no banco de dados. Iniciando rollback de imagem.", e);
            if (imagemSalva != null) {
                storage.excluir(imagemSalva);
            }
            throw e;
        }
    }

    public Page<NoticiaResponse> listar(String busca, Long categoriaId, Long autorId, Boolean ativo, Pageable pageable){
        log.debug("Listando notícias. Filtros - Busca: {}, Categoria: {}, Autor: {}, Ativo: {}", busca, categoriaId, autorId, ativo);
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

        if (autorId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("autor").get("id"), autorId)
            );
        }

        if (ativo != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                var noticiaAtiva = criteriaBuilder.equal(root.get("ativo"), ativo);
                
                if (Boolean.TRUE.equals(ativo)) {
                    return criteriaBuilder.and(
                            noticiaAtiva,
                            criteriaBuilder.equal(root.get("categoria").get("ativo"), true)
                    );
                }
                
                return noticiaAtiva;
            });
        }

        Page<Noticia> noticias = noticiaRepository.findAll(spec, pageable);
        
        return noticias.map(noticia -> new NoticiaResponse(noticia, getPublicUrl()));
    }

    public NoticiaResponse buscarPorId(Long id, Boolean ativo) {
        log.debug("Buscando notícia por ID: {} (Ativo: {})", id, ativo);
        return noticiaRepository.findById(id)
                .filter(noticia -> {
                    if (ativo == null) return true;
                    
                    boolean noticiaStatusOk = noticia.getAtivo().equals(ativo);
                    
                    if (Boolean.TRUE.equals(ativo)) {
                        return noticiaStatusOk && noticia.getCategoria().getAtivo();
                    }
                    
                    return noticiaStatusOk;
                })
                .map(noticia -> new NoticiaResponse(noticia, getPublicUrl()))
                .orElseThrow(() -> {
                    log.warn("Notícia não encontrada com ID: {}", id);
                    return new EntityNotFoundException("Notícia não encontrada com o ID: " + id);
                });
    }

    @Transactional
    public NoticiaResponse atualizar(Long id, NoticiaRequest request, MultipartFile arquivo) {
        log.info("Iniciando atualização da notícia ID: {}", id);
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Notícia ID {} não encontrada", id);
                    return new EntityNotFoundException("Notícia não encontrada com o ID: " + id);
                });

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
                    .orElseThrow(() -> {
                        log.warn("Falha na atualização da notícia {}: Categoria {} não encontrada", id, request.categoriaId());
                        return new EntityNotFoundException("Categoria não encontrada com ID: " + request.categoriaId());
                    });
            Usuario autor = usuarioRepository.findById(request.autorId())
                    .orElseThrow(() -> {
                        log.warn("Falha na atualização da notícia {}: Autor {} não encontrado", id, request.autorId());
                        return new EntityNotFoundException("Autor não encontrado com ID: " + request.autorId());
                    });

            noticia.setCategoria(categoria);
            noticia.setAutor(autor);

            if (novaImagem != null) {
                noticia.setImagem(novaImagem);
            }

            Noticia noticiaSalva = noticiaRepository.save(noticia);

            if (novaImagem != null && imagemAntiga != null) {
                log.info("Substituindo imagem antiga: {}. Nova imagem: {}", imagemAntiga, novaImagem);
                storage.excluir(imagemAntiga);
            }

            log.info("Notícia ID: {} atualizada com sucesso.", id);
            return new NoticiaResponse(noticiaSalva, getPublicUrl());

        } catch (Exception e) {
            log.error("Erro ao atualizar notícia ID: {}. Efetuando rollback da nova imagem.", id, e);
            if (novaImagem != null) {
                storage.excluir(novaImagem);
            }
            throw e;
        }
    }

    @Transactional
    public NoticiaResponse inativar(Long id) {
        log.info("Inativando notícia ID: {}", id);
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setAtivo(false);
                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);
                    log.info("Notícia ID: {} inativada com sucesso.", id);
                    return new NoticiaResponse(noticiaAtualizada, getPublicUrl());
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao inativar: Notícia ID {} não encontrada", id);
                    return new EntityNotFoundException("Notícia não encontrada com o ID: " + id);
                });
    }

    @Transactional
    public NoticiaResponse ativar(Long id) {
        log.info("Ativando notícia ID: {}", id);
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setAtivo(true);
                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);
                    log.info("Notícia ID: {} ativada com sucesso.", id);
                    return new NoticiaResponse(noticiaAtualizada, getPublicUrl());
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao ativar: Notícia ID {} não encontrada", id);
                    return new EntityNotFoundException("Notícia não encontrada com o ID: " + id);
                });
    }
}
