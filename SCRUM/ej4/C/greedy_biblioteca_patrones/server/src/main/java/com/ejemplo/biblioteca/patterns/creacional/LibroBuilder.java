package com.ejemplo.biblioteca.patterns.creacional;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.domain.TipoLibro;

import java.time.LocalDate;

/**
 * Builder que encapsula la creación paulatina de un {@link Libro}.
 */
public class LibroBuilder {

    private Long id;
    private String titulo;
    private LocalDate fecha;
    private String genero;
    private Integer paginas;
    private Autor autor;
    private Persona persona;
    private TipoLibro tipo;
    private Double pesoGramos;
    private Double tamanoMb;

    public static LibroBuilder nuevo() {
        return new LibroBuilder();
    }

    public LibroBuilder conId(Long id) {
        this.id = id;
        return this;
    }

    public LibroBuilder conTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public LibroBuilder conFecha(LocalDate fecha) {
        this.fecha = fecha;
        return this;
    }

    public LibroBuilder conGenero(String genero) {
        this.genero = genero;
        return this;
    }

    public LibroBuilder conPaginas(Integer paginas) {
        this.paginas = paginas;
        return this;
    }

    public LibroBuilder conAutor(Autor autor) {
        this.autor = autor;
        return this;
    }

    public LibroBuilder conPersona(Persona persona) {
        this.persona = persona;
        return this;
    }

    public LibroBuilder conTipo(TipoLibro tipo) {
        this.tipo = tipo;
        return this;
    }

    public LibroBuilder conPesoGramos(Double pesoGramos) {
        this.pesoGramos = pesoGramos;
        return this;
    }

    public LibroBuilder conTamanoMb(Double tamanoMb) {
        this.tamanoMb = tamanoMb;
        return this;
    }

    public Libro build() {
        if (tipo == null) {
            throw new IllegalStateException("Debe definirse un tipo de libro antes de construirlo");
        }
        return LibroFactory.crearLibro(tipo, this);
    }

    Long getId() {
        return id;
    }

    String getTitulo() {
        return titulo;
    }

    LocalDate getFecha() {
        return fecha;
    }

    String getGenero() {
        return genero;
    }

    Integer getPaginas() {
        return paginas;
    }

    Autor getAutor() {
        return autor;
    }

    Persona getPersona() {
        return persona;
    }

    TipoLibro getTipo() {
        return tipo;
    }

    Double getPesoGramos() {
        return pesoGramos;
    }

    Double getTamanoMb() {
        return tamanoMb;
    }
}
