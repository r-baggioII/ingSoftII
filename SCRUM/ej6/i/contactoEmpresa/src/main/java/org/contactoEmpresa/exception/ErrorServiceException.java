package org.contactoEmpresa.exception;

public class ErrorServiceException extends RuntimeException {

    /**
     * Constructor estándar que solo toma un mensaje.
     * @param message El mensaje de error que describe el problema.
     */
    public ErrorServiceException(String message) {
        super(message);
    }

    /**
     * Constructor que toma un mensaje y la excepción original (causa).
     * Esto es muy útil para el debug, ya que preserva el stack trace original.
     * @param message El mensaje de error que describe el problema.
     * @param cause La excepción original que provocó este error.
     */
    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}