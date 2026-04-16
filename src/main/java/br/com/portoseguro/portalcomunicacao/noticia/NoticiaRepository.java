package br.com.portoseguro.portalcomunicacao.noticia;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long>, JpaSpecificationExecutor<Noticia> {

    List<Noticia> findTop5ByOrderByDataPublicacaoDesc();
}
