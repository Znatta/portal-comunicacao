package br.com.portoseguro.portalcomunicacao.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByAtivo(Boolean ativo);

    List<Categoria> findDistinctByAtivoTrueAndNoticiasAtivoTrue();
}
