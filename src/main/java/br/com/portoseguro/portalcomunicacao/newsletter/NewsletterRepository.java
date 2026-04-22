package br.com.portoseguro.portalcomunicacao.newsletter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    Optional<Newsletter> findByEmail(String email);

    Optional<Newsletter> findByTokenUnsubscribe(UUID token);

    List<Newsletter> findAllByAtivoTrue();
}
