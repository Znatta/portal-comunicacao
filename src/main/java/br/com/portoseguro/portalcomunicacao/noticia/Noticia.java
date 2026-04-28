package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.Categoria;
import br.com.portoseguro.portalcomunicacao.infra.audit.AuditoriaBase;
import br.com.portoseguro.portalcomunicacao.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "noticias")
public class Noticia extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String titulo;

    @Column(length = 255, nullable = false)
    private String subtitulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Usuario autor;

    @Column
    private String imagem;
}
