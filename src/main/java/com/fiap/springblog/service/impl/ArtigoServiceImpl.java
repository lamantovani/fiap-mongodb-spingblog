package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    private ArtigoRepository artigoRepository;

    @Autowired
    private AutorRepository autorRepository;

    public ArtigoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Artigo> obterTodos() {
        return this.artigoRepository.findAll();
    }

    @Override
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository
                .findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Artigo não existe!"));
    }

    @Override
    public Artigo criar(Artigo artigo) {

        if (artigo.getAutor() != null && artigo.getAutor().getCodigo() != null) {
            Autor autor = this.autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado"));
            artigo.setAutor(autor);
        } else {
            artigo.setAutor(null);
        }

        return this.artigoRepository.save(artigo);
    }

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime dateTime) {
        Query query = new Query(Criteria.where("data").gt(dateTime));
        return this.mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime dateTime, Integer status) {
        Query query = new Query(Criteria.where("data")
                .is(dateTime)
                .and("status").is(status));
        return this.mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public void atualizar(Artigo artigo) {

        if (artigo.getAutor() == null || artigo.getAutor().getCodigo() == null) {
            new IllegalArgumentException("Autor não encontrado");
        }

        var artigoDb = this.artigoRepository
                .findById(artigo.getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Artigo não encontrado"));

        if (artigo.getAutor() != null) {
            artigoDb.setAutor(artigo.getAutor());
        }

        if (artigo.getTitulo() != null) {
            artigoDb.setTitulo(artigo.getTitulo());
        }

        if (artigo.getTexto() != null) {
            artigoDb.setTexto(artigo.getTexto());
        }

        if (artigo.getStatus() != null) {
            artigoDb.setStatus(artigo.getStatus());
        }

        if (artigo.getData() != null) {
            artigoDb.setData(artigo.getData());
        }

        if (artigo.getUrl() != null) {
            artigoDb.setUrl(artigo.getUrl());
        }

        this.artigoRepository.save(artigoDb);
    }

    @Override
    public void atualizarURLArtigo(String id, String novaURL) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("url", novaURL);
        this.mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    public void deleteById(String id) {
        this.artigoRepository.deleteById(id);
    }

    @Override
    public void deleteArtigoByMongoId(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        this.mongoTemplate.remove(query, Artigo.class);
    }
}
