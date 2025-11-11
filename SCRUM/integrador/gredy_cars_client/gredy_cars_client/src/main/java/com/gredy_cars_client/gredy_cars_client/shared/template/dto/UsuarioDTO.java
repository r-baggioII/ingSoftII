package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO extends BaseDTO<String> {
    
    private String id;
    private String nombreUsuario;
    private String clave; // write-only, solo para crear/editar
    private Rol rol;
    private String personaId; // ID de la persona (cliente o empleado) asociada
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
}
