package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {

    public List<Artigo> obterTodos();
    public Artigo obterPorCodigo(String codigo);
//    public Artigo criar(Artigo artigo);
//    public ResponseEntity<?> criar(Artigo artigo);

    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);

    public void excluirArtigoEAutor(Artigo artigo);

    public List<Artigo> findByDataGreaterThan(LocalDateTime dateTime);
    public List<Artigo> findByDataAndStatus(LocalDateTime dateTime, Integer status);
    public void atualizar(Artigo artigo);
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo);
    public void atualizarURLArtigo(String id, String novaURL);
    public void deleteById(String id);
    public void deleteArtigoByMongoId(String id);
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data);
    public List<Artigo> findByStatusEquals(Integer status);
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo);
    public Page<Artigo> obterArtigosPaginados(Pageable pageable);
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status);
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);
    public List<Artigo> findByTexto(String texto);
    public List<ArtigoStatusCount> contarArtigoPorStatus();
    public List<AutorTotalArtigo> calcularTotalArtigoPorAutorNoPeriodo(LocalDate dataInicio, LocalDate dataFim);


}
