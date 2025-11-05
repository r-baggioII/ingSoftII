package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
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
@ToString(callSuper = true, exclude = "contenido") // Excluir contenido del toString para no imprimir bytes
@Entity
@Table(name = "imagen")
public class Imagen extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank(message = "El nombre de la imagen es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotBlank(message = "El tipo MIME es obligatorio")
    @Size(max = 100, message = "El tipo MIME no puede exceder 100 caracteres")
    @Column(name = "mime", nullable = false, length = 100)
    private String mime;

    @NotNull(message = "El contenido de la imagen es obligatorio")
    @Lob
    @Column(name = "contenido", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @NotNull(message = "El tipo de imagen es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_imagen", nullable = false, length = 20)
    private TipoImagen tipoImagen;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Crea una nueva imagen
     * @param nombre nombre del archivo de imagen
     * @param contenido contenido binario de la imagen
     * @param tipoImagen tipo de imagen (PERSONA o VEHICULO)
     * @return la imagen creada
     */
    public Imagen crearImagen(String nombre, byte[] contenido, TipoImagen tipoImagen) {
        validar(nombre, contenido, tipoImagen);
        this.nombre = nombre;
        this.contenido = contenido;
        this.tipoImagen = tipoImagen;
        this.eliminado = false;
        // Inferir MIME type del nombre si tiene extensión
        if (nombre != null && nombre.contains(".")) {
            String extension = nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
            this.mime = inferirMimeType(extension);
        } else {
            this.mime = "application/octet-stream";
        }
        return this;
    }

    /**
     * Valida los datos de la imagen
     * @param nombre nombre del archivo
     * @param contenido contenido binario
     * @param tipoImagen tipo de imagen
     */
    public void validar(String nombre, byte[] contenido, TipoImagen tipoImagen) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la imagen es obligatorio");
        }
        if (nombre.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder 255 caracteres");
        }
        if (contenido == null || contenido.length == 0) {
            throw new IllegalArgumentException("El contenido de la imagen es obligatorio");
        }
        if (tipoImagen == null) {
            throw new IllegalArgumentException("El tipo de imagen es obligatorio");
        }
    }

    /**
     * Modifica la imagen existente
     * @param nombre nuevo nombre
     * @param contenido nuevo contenido
     * @param tipoImagen nuevo tipo
     * @return la imagen modificada
     */
    public Imagen modificarImagen(String nombre, byte[] contenido, TipoImagen tipoImagen) {
        validar(nombre, contenido, tipoImagen);
        this.nombre = nombre;
        this.contenido = contenido;
        this.tipoImagen = tipoImagen;
        // Actualizar MIME type
        if (nombre != null && nombre.contains(".")) {
            String extension = nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
            this.mime = inferirMimeType(extension);
        }
        return this;
    }

    /**
     * Infiere el tipo MIME a partir de la extensión del archivo
     */
    private String inferirMimeType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }
}
