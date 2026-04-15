package br.com.portoseguro.portalcomunicacao.infra.storage;

import org.springframework.web.multipart.MultipartFile;

public interface Storage {

    String salvar(MultipartFile arquivo);
    void excluir(String caminho);
}
