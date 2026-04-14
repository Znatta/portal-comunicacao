package br.com.portoseguro.portalcomunicacao.categoria;

public record CategoriaResponse(
        Long id,
        String nome,
        Boolean ativo) {

        public CategoriaResponse(Categoria categoria) {
        this(categoria.getId(), categoria.getNome(), categoria.getAtivo());
    }
}
