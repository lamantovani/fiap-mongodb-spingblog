package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;

import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {

    public List<Artigo> obterTodos();
    public Artigo obterPorCodigo(String codigo);
    public Artigo criar(Artigo artigo);
    public List<Artigo> findByDataGreaterThan(LocalDateTime dateTime);
    public List<Artigo> findByDataAndStatus(LocalDateTime dateTime, Integer status);
    public void atualizar(Artigo artigo);
    public void atualizarURLArtigo(String id, String novaURL);
    public void deleteById(String id);
    public void deleteArtigoByMongoId(String id);


}
