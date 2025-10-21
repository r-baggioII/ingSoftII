package com.ejemplo.biblioteca.patterns.creacional;

/**
 * Contrato genérico para objetos que soportan clonación controlada.
 *
 * @param <T> tipo que se clonará
 */
public interface Prototype<T> {

    /**
     * @return una nueva instancia con los datos relevantes copiados.
     */
    T clonar();
}
