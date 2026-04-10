package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.CategoriaRepository;
import br.com.portoseguro.portalcomunicacao.categoria.CategoriaResponse;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public NoticiaResponse criar(NoticiaRequest request) {
        Noticia noticia = new Noticia();

        noticia.setTitulo(request.titulo());
        noticia.setSubtitulo(request.subtitulo());
        noticia.setConteudo(request.conteudo());
        noticia.setDataPublicacao(request.dataPublicacao());
        noticia.setAtivo(request.ativo());
        noticia.setCategoria(categoriaRepository.getReferenceById(request.categoriaId()));
        noticia.setAutor(usuarioRepository.getReferenceById(request.autorId()));

        Noticia noticiaSalva = noticiaRepository.save(noticia);

        CategoriaResponse categoria = new CategoriaResponse(
                noticia.getCategoria().getId(),
                noticia.getCategoria().getNome(),
                noticia.getCategoria().getAtivo());

        UsuarioResponse usuario = new UsuarioResponse(
                noticia.getAutor().getId(),
                noticia.getAutor().getNome(),
                noticia.getAutor().getEmail(),
                noticia.getAutor().getAtivo(),
                noticia.getAutor().getPerfil().name());

        return new NoticiaResponse(
                noticiaSalva.getId(),
                noticiaSalva.getTitulo(),
                noticiaSalva.getSubtitulo(),
                noticiaSalva.getConteudo(),
                noticiaSalva.getDataPublicacao(),
                noticiaSalva.getAtivo(),
                categoria,
                usuario
        );
    }

    public Page<NoticiaResponse> listar(Boolean ativo, Pageable pageable) {
        Page<Noticia> noticias = (ativo == null)
                ? noticiaRepository.findAll(pageable)
                : noticiaRepository.findByAtivo(ativo, pageable);

        return noticias.map(noticia -> new NoticiaResponse(
                        noticia.getId(),
                        noticia.getTitulo(),
                        noticia.getSubtitulo(),
                        noticia.getConteudo(),
                        noticia.getDataPublicacao(),
                        noticia.getAtivo(),
                        new CategoriaResponse(
                                noticia.getCategoria().getId(),
                                noticia.getCategoria().getNome(),
                                noticia.getCategoria().getAtivo()),
                        new UsuarioResponse(
                                noticia.getAutor().getId(),
                                noticia.getAutor().getNome(),
                                noticia.getAutor().getEmail(),
                                noticia.getAutor().getAtivo(),
                                noticia.getAutor().getPerfil().name()
                        )
                ));
    }

    public NoticiaResponse buscarPorId(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> new NoticiaResponse(
                        noticia.getId(),
                        noticia.getTitulo(),
                        noticia.getSubtitulo(),
                        noticia.getConteudo(),
                        noticia.getDataPublicacao(),
                        noticia.getAtivo(),
                        new CategoriaResponse(
                                noticia.getCategoria().getId(),
                                noticia.getCategoria().getNome(),
                                noticia.getCategoria().getAtivo()),
                        new UsuarioResponse(
                                noticia.getAutor().getId(),
                                noticia.getAutor().getNome(),
                                noticia.getAutor().getEmail(),
                                noticia.getAutor().getAtivo(),
                                noticia.getAutor().getPerfil().name()
                        )
                ))
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse atualizar(Long id, NoticiaRequest request) {
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setTitulo(request.titulo());
                    noticia.setSubtitulo(request.subtitulo());
                    noticia.setConteudo(request.conteudo());
                    noticia.setDataPublicacao(request.dataPublicacao());
                    noticia.setAtivo(request.ativo());
                    noticia.setCategoria(categoriaRepository.getReferenceById(request.categoriaId()));

                    Noticia noticiaSalva = noticiaRepository.save(noticia);

                    CategoriaResponse categoria = new CategoriaResponse(
                            noticia.getCategoria().getId(),
                            noticia.getCategoria().getNome(),
                            noticia.getCategoria().getAtivo());

                    UsuarioResponse usuario = new UsuarioResponse(
                            noticia.getAutor().getId(),
                            noticia.getAutor().getNome(),
                            noticia.getAutor().getEmail(),
                            noticia.getAutor().getAtivo(),
                            noticia.getAutor().getPerfil().name());

                    return new NoticiaResponse(
                            noticiaSalva.getId(),
                            noticiaSalva.getTitulo(),
                            noticiaSalva.getSubtitulo(),
                            noticiaSalva.getConteudo(),
                            noticiaSalva.getDataPublicacao(),
                            noticiaSalva.getAtivo(),
                            categoria,
                            usuario
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse inativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {

                    noticia.setAtivo(false);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    CategoriaResponse categoria = new CategoriaResponse(
                            noticia.getCategoria().getId(),
                            noticia.getCategoria().getNome(),
                            noticia.getCategoria().getAtivo());

                    UsuarioResponse usuario = new UsuarioResponse(
                            noticia.getAutor().getId(),
                            noticia.getAutor().getNome(),
                            noticia.getAutor().getEmail(),
                            noticia.getAutor().getAtivo(),
                            noticia.getAutor().getPerfil().name());

                    return new NoticiaResponse(
                            noticiaAtualizada.getId(),
                            noticiaAtualizada.getTitulo(),
                            noticiaAtualizada.getSubtitulo(),
                            noticiaAtualizada.getConteudo(),
                            noticiaAtualizada.getDataPublicacao(),
                            noticiaAtualizada.getAtivo(),
                            categoria,
                            usuario
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse ativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {

                    noticia.setAtivo(true);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    CategoriaResponse categoria = new CategoriaResponse(
                            noticia.getCategoria().getId(),
                            noticia.getCategoria().getNome(),
                            noticia.getCategoria().getAtivo());

                    UsuarioResponse usuario = new UsuarioResponse(
                            noticia.getAutor().getId(),
                            noticia.getAutor().getNome(),
                            noticia.getAutor().getEmail(),
                            noticia.getAutor().getAtivo(),
                            noticia.getAutor().getPerfil().name());

                    return new NoticiaResponse(
                            noticiaAtualizada.getId(),
                            noticiaAtualizada.getTitulo(),
                            noticiaAtualizada.getSubtitulo(),
                            noticiaAtualizada.getConteudo(),
                            noticiaAtualizada.getDataPublicacao(),
                            noticiaAtualizada.getAtivo(),
                            categoria,
                            usuario
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }
}
