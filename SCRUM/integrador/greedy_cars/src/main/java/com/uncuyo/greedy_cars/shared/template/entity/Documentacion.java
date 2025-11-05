package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumentacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "documentacion")
public class Documentacion extends BaseEntity<String> {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "El tipo de documentación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documentacion", nullable = false, length = 50)
    private TipoDocumentacion tipoDocumentacion;

    @Size(max = 500, message = "La observación no puede exceder 500 caracteres")
    @Column(name = "observacion", length = 500)
    private String observacion;

    @NotBlank(message = "El path del archivo es obligatorio")
    @Size(max = 500, message = "El path del archivo no puede exceder 500 caracteres")
    @Column(name = "path_archivo", nullable = false, length = 500)
    private String pathArchivo;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede exceder 255 caracteres")
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Crea una nueva documentación
     * @param tipoDocumentacion tipo de documentación
     * @param pathArchivo ruta del archivo
     * @param nombre nombre del archivo
     * @param observacion observaciones adicionales
     */
    public void crearDocumentacion(TipoDocumentacion tipoDocumentacion, String pathArchivo, String nombre, String observacion) {
        validar(tipoDocumentacion, pathArchivo, nombre, observacion);
        this.tipoDocumentacion = tipoDocumentacion;
        this.pathArchivo = pathArchivo;
        this.nombreArchivo = nombre;
        this.observacion = observacion;
        this.eliminado = false;
    }

    /**
     * Valida los datos de la documentación
     * @param tipoDocumentacion tipo de documentación
     * @param pathArchivo ruta del archivo
     * @param nombre nombre del archivo
     * @param observacion observaciones adicionales
     * @throws IllegalArgumentException si algún dato requerido es inválido
     */
    public void validar(TipoDocumentacion tipoDocumentacion, String pathArchivo, String nombre, String observacion) {
        if (tipoDocumentacion == null) {
            throw new IllegalArgumentException("El tipo de documentación es obligatorio");
        }
        if (pathArchivo == null || pathArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El path del archivo es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio");
        }
        if (pathArchivo.length() > 500) {
            throw new IllegalArgumentException("El path del archivo no puede exceder 500 caracteres");
        }
        if (nombre.length() > 255) {
            throw new IllegalArgumentException("El nombre del archivo no puede exceder 255 caracteres");
        }
        if (observacion != null && observacion.length() > 500) {
            throw new IllegalArgumentException("La observación no puede exceder 500 caracteres");
        }
    }

    /**
     * Modifica la documentación existente
     * @param tipoDocumentacion tipo de documentación
     * @param pathArchivo ruta del archivo
     * @param nombre nombre del archivo
     * @param observacion observaciones adicionales
     */
    public void modificarDocumentacion(TipoDocumentacion tipoDocumentacion, String pathArchivo, String nombre, String observacion) {
        validar(tipoDocumentacion, pathArchivo, nombre, observacion);
        this.tipoDocumentacion = tipoDocumentacion;
        this.pathArchivo = pathArchivo;
        this.nombreArchivo = nombre;
        this.observacion = observacion;
    }

    /**
     * Marca la documentación como eliminada (soft delete)
     * @param id identificador de la documentación
     */
    public void eliminarDocumentacion(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id es obligatorio para eliminar");
        }
        this.eliminado = true;
    }
}
