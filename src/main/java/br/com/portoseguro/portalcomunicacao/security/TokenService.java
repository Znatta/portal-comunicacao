package br.com.portoseguro.portalcomunicacao.security;

import br.com.portoseguro.portalcomunicacao.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario){
        Algorithm algoritmo = Algorithm.HMAC256(secret);

        try {
            return JWT.create()
                    .withIssuer("Portal Comunicacao")
                    .withSubject(usuario.getEmail())
                    .withClaim("role", usuario.getPerfil().name())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Erro ao gerar token JWT", ex);
        }
    }

    public String getSubject(String tokenWJT){
        Algorithm algoritmo = Algorithm.HMAC256(secret);

        try{
            return JWT.require(algoritmo)
                    .withIssuer("Portal Comunicacao")
                    .build()
                    .verify(tokenWJT)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    private Instant gerarDataExpiracao(){
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
