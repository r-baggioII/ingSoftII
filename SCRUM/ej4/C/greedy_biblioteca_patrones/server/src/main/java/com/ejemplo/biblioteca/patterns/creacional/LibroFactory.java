package com.ejemplo.biblioteca.patterns.creacional;

import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.TipoLibro;

/**
 * Fábrica concreta responsable de instanciar libros físicos y digitales.
 */
public final class LibroFactory {

    private LibroFactory() {
    }

    public static Libro crearLibro(TipoLibro tipo, LibroBuilder builder) {
        Libro libro = new Libro();
        libro.setId(builder.getId());
        libro.setTitulo(builder.getTitulo());
        libro.setFecha(builder.getFecha());
        libro.setGenero(builder.getGenero());
        libro.setPaginas(builder.getPaginas());
        libro.setAutor(builder.getAutor());
        libro.setPersona(builder.getPersona());
        libro.setTipo(tipo);

        switch (tipo) {
            case FISICO -> configurarFisico(builder, libro);
            case DIGITAL -> configurarDigital(builder, libro);
            default -> throw new IllegalStateException("Tipo no soportado: " + tipo);
        }
        return libro;
    }

    private static void configurarFisico(LibroBuilder builder, Libro libro) {
        Double peso = builder.getPesoGramos();
        if (peso == null || peso <= 0) {
            throw new IllegalArgumentException("Un libro físico requiere un peso en gramos mayor a cero");
        }
        libro.setPesoGramos(peso);
        libro.setTamanoMb(null);
    }

    private static void configurarDigital(LibroBuilder builder, Libro libro) {
        Double tamano = builder.getTamanoMb();
        if (tamano == null || tamano <= 0) {
            throw new IllegalArgumentException("Un libro digital requiere un tamaño en MB mayor a cero");
        }
        libro.setTamanoMb(tamano);
        libro.setPesoGramos(null);
    }
}
