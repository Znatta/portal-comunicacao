package br.com.portoseguro.portalcomunicacao.usuario;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request){
        String senhaCriptografada = passwordEncoder.encode(request.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(senhaCriptografada);
        usuario.setPerfil(UsuarioPerfil.valueOf(request.perfil()));

        usuarioRepository.save(usuario);

        return new UsuarioResponse(usuario);
    }

    public Page<UsuarioResponse> listar(String nome, String email, Boolean ativo, Pageable pageable) {
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
        return usuarioRepository.findAllByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(UsuarioResumoResponse::new)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> new UsuarioResponse(usuario))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNome(request.nome());
                    usuario.setEmail(request.email());
                    usuario.setAtivo(request.ativo());
                    usuario.setPerfil(UsuarioPerfil.valueOf(request.perfil()));

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public UsuarioResponse inativar(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(false);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(true);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);

                    return new UsuarioResponse(usuarioAtualizado);
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }
}
