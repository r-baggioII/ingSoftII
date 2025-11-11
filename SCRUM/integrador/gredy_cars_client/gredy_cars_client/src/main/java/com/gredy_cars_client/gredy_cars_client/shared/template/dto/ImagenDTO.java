package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Imagen utilizado por el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImagenDTO extends BaseDTO<Long> {

    @Size(max = 500, message = "El nombre del archivo no puede exceder los 500 caracteres")
    private String nombreArchivo;

    @Size(max = 100, message = "El tipo de contenido no puede exceder los 100 caracteres")
    private String contentType;

    private Long tamano;

    @Size(max = 1000, message = "La URL no puede exceder los 1000 caracteres")
    private String url;

    @Size(max = 500, message = "El path no puede exceder los 500 caracteres")
    private String path;

    @Size(max = 50, message = "El tipo de imagen no puede exceder los 50 caracteres")
    private String tipoImagen;

    @Override
    public Long getId() {
        return super.id;
    }

    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
