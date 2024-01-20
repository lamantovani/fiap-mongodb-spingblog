package com.fiap.springblog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Artigo {

    @Id
    private String  codigo;
    private String titulo;
    private LocalDateTime data;

    @TextIndexed
    private String texto;

    private String url;
    private Integer status;

    @DBRef
    private Autor autor;
}