package br.com.portoseguro.portalcomunicacao.noticia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    Page<Noticia> findByAtivo(Boolean ativo, Pageable pageable);
}
