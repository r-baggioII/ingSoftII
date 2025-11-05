package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumentacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Documentacion.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DocumentacionDTO extends BaseDTO<String> {
    
    @NotNull(message = "El tipo de documentación es obligatorio")
    private TipoDocumentacion tipoDocumentacion;
    
    @Size(max = 500, message = "La observación no puede exceder 500 caracteres")
    private String observacion;
    
    @NotBlank(message = "El path del archivo es obligatorio")
    @Size(max = 500, message = "El path del archivo no puede exceder 500 caracteres")
    private String pathArchivo;
    
    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede exceder 255 caracteres")
    private String nombreArchivo;
    
    @Override
    public String getId() {
        return super.id;
    }
    
    @Override
    public void setId(String id) {
        super.id = id;
    }
}
