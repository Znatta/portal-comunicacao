package br.com.portoseguro.portalcomunicacao.categoria;

import br.com.portoseguro.portalcomunicacao.infra.audit.AuditoriaBase;
import br.com.portoseguro.portalcomunicacao.noticia.Noticia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorias")
public class Categoria extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "categoria")
    private List<Noticia> noticias;

}
