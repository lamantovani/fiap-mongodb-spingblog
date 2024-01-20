package com.fiap.springblog.controller;


import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
         return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de concorrência: O artigo foi autualizado por outro usuário. " +
                 "Por favor, tente novamente!");
    }

    @GetMapping
    public List<Artigo> obterTodos() {
        return this.artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return this.artigoService.obterPorCodigo(codigo);
    }

    @PostMapping
    public Artigo criar(@RequestBody Artigo artigo) {
        return this.artigoService.criar(artigo);
    }

    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByDataGreaterThan(data);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(
            @RequestParam("data") LocalDateTime data,
            @RequestParam("status") Integer status) {
        return this.artigoService.findByDataAndStatus(data, status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo artigo) {
        this.artigoService.atualizar(artigo);
    }

    @PutMapping("/{id}")
    public void atualizarURL(
            @PathVariable String id,
            @RequestBody String url) {
        this.artigoService.atualizarURLArtigo(id, url);
    }

    @DeleteMapping("/{id}")
    public void deleteArtigo(@PathVariable String id) {
        this.artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoByMongoId(@RequestParam("Id") String id) {
        this.artigoService.deleteArtigoByMongoId(id);
    }

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(@RequestParam("status") Integer status,
                                                       @RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByStatusAndDataGreaterThan(status, data);
    }

    @GetMapping("/status")
    public List<Artigo> findByStatusEquals(@RequestParam("status") Integer status) {
        return this.artigoService.findByStatusEquals(status);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(@RequestParam("de") LocalDateTime de,
                                               @RequestParam("ate") LocalDateTime ate) {
        return this.artigoService.obterArtigoPorDataHora(de, ate);

    }

    @GetMapping("/artigo-complexo")
    public List<Artigo> encontrarArtigosComplexos(@RequestParam("status") Integer status,
                                                  @RequestParam("data") LocalDateTime data,
                                                  @RequestParam("titulo") String titulo) {
        return this.artigoService.encontrarArtigosComplexos(status, data, titulo);
    }

    @GetMapping("/pagina-artigos")
    public ResponseEntity<Page<Artigo>> obterArtigosPaginados(Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.obterArtigosPaginados(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(@RequestParam("status") Integer status) {
        return this.artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(@RequestParam("status") Integer status) {
        return  this.artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    @GetMapping("/busca-texto")
    public List<Artigo> findByTexto(@RequestParam("searchTerm") String termo) {
        return  this.artigoService.findByTexto(termo);
    }

    @GetMapping("/contar-porstatus")
    public List<ArtigoStatusCount> contarArtigoPorStatus() {
        return this.artigoService.contarArtigoPorStatus();
    }

    @GetMapping("/total-artigo-autor-periodo")
    public List<AutorTotalArtigo> calcularTotalArtigoPorAutorNoPeriodo(@RequestParam("dataInicio") LocalDate dataInicio,
                                                                       @RequestParam("dataFim") LocalDate dataFim) {
        return this.artigoService.calcularTotalArtigoPorAutorNoPeriodo(dataInicio, dataFim);
    }

}
