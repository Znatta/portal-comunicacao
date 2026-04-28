package br.com.portoseguro.portalcomunicacao.security;

import br.com.portoseguro.portalcomunicacao.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@Slf4j
public class AutenticacaoController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Login de usuário", tags = {"Portal Administrativo"})
    @PostMapping
    public LoginResponse login (@Valid @RequestBody LoginRequest request){
        log.info("Tentativa de login para o usuário: {}", request.email());
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        Usuario usuario = (Usuario) auth.getPrincipal();
        String tokenJWT = tokenService.gerarToken(usuario);
        return new LoginResponse(tokenJWT, usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}
