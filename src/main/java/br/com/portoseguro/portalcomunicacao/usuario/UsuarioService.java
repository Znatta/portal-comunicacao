package br.com.portoseguro.portalcomunicacao.usuario;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo(),
                usuario.getPerfil().name());
    }

    public Page<UsuarioResponse> listar(String nome, String email, Boolean ativo, Pageable pageable) {
        // 1. Cria uma entidade "molde" com os filtros recebidos
        Usuario filtro = new Usuario();
        filtro.setNome(nome);
        filtro.setEmail(email);
        filtro.setAtivo(ativo);

        // 2. Configura o "Matcher" para ignorar case (maiusculas/minusculas)
        // e buscar partes da palavra (LIKE %valor%)
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        // 3. Monta o exemplo e passa para o repositório
        Example<Usuario> example = Example.of(filtro, matcher);
        Page<Usuario> usuarios = usuarioRepository.findAll(example, pageable);

        // 4. Mapeia para o DTO de resposta
        return usuarios.map(usuario -> new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo(),
                usuario.getPerfil().name()
        ));
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> new UsuarioResponse(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getAtivo(),
                        usuario.getPerfil().name()
                ))
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

                    return new UsuarioResponse(
                            usuarioAtualizado.getId(),
                            usuarioAtualizado.getNome(),
                            usuarioAtualizado.getEmail(),
                            usuarioAtualizado.getAtivo(),
                            usuarioAtualizado.getPerfil().name()
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public UsuarioResponse inativar(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(false);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);

                    return new UsuarioResponse(
                            usuarioAtualizado.getId(),
                            usuarioAtualizado.getNome(),
                            usuarioAtualizado.getEmail(),
                            usuarioAtualizado.getAtivo(),
                            usuarioAtualizado.getPerfil().name()
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuario.setAtivo(true);

                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);

                    return new UsuarioResponse(
                            usuarioAtualizado.getId(),
                            usuarioAtualizado.getNome(),
                            usuarioAtualizado.getEmail(),
                            usuarioAtualizado.getAtivo(),
                            usuarioAtualizado.getPerfil().name()
                    );
                })
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }
}
