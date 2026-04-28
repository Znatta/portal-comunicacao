package br.com.portoseguro.portalcomunicacao.usuario;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request){
        log.info("Iniciando criação do usuário: {} ({})", request.nome(), request.email());
        String senhaCriptografada = passwordEncoder.encode(request.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(senhaCriptografada);
        usuario.setPerfil(UsuarioPerfil.valueOf(request.perfil()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());

        return new UsuarioResponse(usuarioSalvo);
    }

    public Page<UsuarioResponse> listar(String nome, String email, Boolean ativo, Pageable pageable) {
        log.debug("Listando usuários com filtros. Nome: {}, Email: {}, Ativo: {}", nome, email, ativo);
        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        if (nome != null && !nome.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%")
            );
        }

        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
            );
        }

        if (ativo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("ativo"), ativo)
            );
        }

        Page<Usuario> usuarios = usuarioRepository.findAll(spec, pageable);

        return usuarios.map(usuario -> new UsuarioResponse(usuario));
    }

    public List<UsuarioResumoResponse> listarAutores() {
        log.debug("Buscando lista resumida de autores ativos.");
        return usuarioRepository.findAllByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(UsuarioResumoResponse::new)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        log.debug("Buscando usuário por ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> new UsuarioResponse(usuario))
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com ID: {}", id);
                    return new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
                });
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request) {
        log.info("Iniciando atualização do usuário ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNome(request.nome());
                    usuario.setEmail(request.email());
                    usuario.setAtivo(request.ativo());
                    usuario.setPerfil(UsuarioPerfil.valueOf(request.perfil()));

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);
                    log.info("Usuário ID: {} atualizado com sucesso.", id);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Usuário ID {} não encontrado.", id);
                    return new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
                });
    }

    @Transactional
    public UsuarioResponse inativar(Long id) {
        log.info("Inativando usuário ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(false);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);
                    log.info("Usuário ID: {} inativado com sucesso.", id);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao inativar: Usuário ID {} não encontrado.", id);
                    return new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
                });
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        log.info("Ativando usuário ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(true);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);
                    log.info("Usuário ID: {} ativado com sucesso.", id);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> {
                    log.warn("Falha ao ativar: Usuário ID {} não encontrado.", id);
                    return new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
                });
    }
}
