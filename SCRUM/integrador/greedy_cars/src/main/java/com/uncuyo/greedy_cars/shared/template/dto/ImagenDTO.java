package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad ImagenEntity.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "contenido") // Excluimos contenido del toString
public class ImagenDTO extends BaseDTO<String> {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;
    
    @NotBlank(message = "El tipo MIME es obligatorio")
    @Size(max = 100, message = "El tipo MIME no puede exceder los 100 caracteres")
    private String mime;
    
    @NotNull(message = "El contenido de la imagen es obligatorio")
    private byte[] contenido;
    
    @NotNull(message = "El tipo de imagen es obligatorio")
    private TipoImagen tipoImagen;
    
    @Override
    public String getId() {
        return super.id;
    }
    
    @Override
    public void setId(String id) {
        super.id = id;
    }
}
