package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VehiculoDTO extends BaseDTO<String> {

    private String patente;

    private EstadoVehiculo estadoVehiculo = EstadoVehiculo.DISPONIBLE;

    @JsonProperty("caracteristicaVehiculo")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private CaracteristicaVehiculoMinDTO caracteristicaVehiculo;

    @JsonIgnore
    private String caracteristicaVehiculoId;

    @JsonIgnore
    public CaracteristicaVehiculoMinDTO getCaracteristica() {
        return caracteristicaVehiculo;
    }

    public String getCaracteristicaVehiculoId() {
        if (caracteristicaVehiculo != null && caracteristicaVehiculo.getId() != null) {
            return caracteristicaVehiculo.getId();
        }
        return caracteristicaVehiculoId;
    }

    public void setCaracteristicaVehiculoId(String caracteristicaVehiculoId) {
        if (caracteristicaVehiculoId != null && caracteristicaVehiculoId.trim().isEmpty()) {
            this.caracteristicaVehiculoId = null;
        } else {
            this.caracteristicaVehiculoId = caracteristicaVehiculoId != null ? caracteristicaVehiculoId.trim() : null;
        }
        if (this.caracteristicaVehiculo == null && this.caracteristicaVehiculoId != null) {
            this.caracteristicaVehiculo = new CaracteristicaVehiculoMinDTO();
        }
        if (this.caracteristicaVehiculo != null) {
            this.caracteristicaVehiculo.setId(this.caracteristicaVehiculoId);
        }
    }

    @JsonIgnore
    public void setCaracteristica(CaracteristicaVehiculoMinDTO caracteristica) {
        setCaracteristicaVehiculo(caracteristica);
    }

    public void setCaracteristicaVehiculo(CaracteristicaVehiculoMinDTO caracteristicaVehiculo) {
        this.caracteristicaVehiculo = caracteristicaVehiculo;
        this.caracteristicaVehiculoId = caracteristicaVehiculo != null ? caracteristicaVehiculo.getId() : null;
    }

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
