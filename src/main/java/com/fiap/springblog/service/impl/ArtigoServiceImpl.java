package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
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

    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data) {
        return this.artigoRepository.findByStatusAndDataGreaterThan(status, data);
    }

    @Override
    public List<Artigo> findByStatusEquals(Integer status) {
        return this.artigoRepository.findByStatusEquals(status);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo) {
        Criteria criteria = new Criteria();

        if (data != null) {
            criteria.and("data").lte(data);
        }


        if (status != null) {
            criteria.and("status").is(status);
        }

        if (titulo != null && !titulo.isEmpty()) {
            criteria.and("titulo").regex(titulo, "i");
        }

        Query query = new Query(criteria);
        return this.mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public Page<Artigo> obterArtigosPaginados(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.artigoRepository.findAll(paginacao);
    }

    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    @Override
    public List<Artigo> findByTexto(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return this.mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<ArtigoStatusCount> contarArtigoPorStatus() {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.group("status").count().as("quantidade"),
                        Aggregation.project("quantidade").and("status").previousOperation()
                );
        AggregationResults<ArtigoStatusCount> result = this.mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);
        return result.getMappedResults();
    }

    @Override
    public List<AutorTotalArtigo> calcularTotalArtigoPorAutorNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                  Artigo.class,
                  Aggregation.match(Criteria.where("data")
                          .gte(dataInicio.atStartOfDay())
                          .lt(dataFim.plusDays(1).atStartOfDay())
                  ),
                  Aggregation.group("autor").count().as("totalArtigos"),
                  Aggregation.project("totalArtigos").and("autor").previousOperation()
                );

        AggregationResults<AutorTotalArtigo> results =
                this.mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);
        return results.getMappedResults();
    }
}
