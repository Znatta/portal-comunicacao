package br.com.portoseguro.portalcomunicacao.security;

import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Carregando detalhes do usuário por e-mail: {}", username);
        return usuarioRepository.findByEmail(username).orElseThrow(() -> {
            log.warn("Usuário não encontrado para o e-mail: {}", username);
            return new UsernameNotFoundException("Usuário não encontrado");
        });
    }
}
