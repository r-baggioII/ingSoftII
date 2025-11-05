package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.AlquilerDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Documentacion;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AlquilerMapper {

    public AlquilerDTO toDTO(Alquiler entity) {
        if (entity == null) return null;
        AlquilerDTO dto = new AlquilerDTO();
        dto.setId(entity.getId());
        dto.setEliminado(Boolean.TRUE.equals(entity.getEliminado()));
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        dto.setIdCliente(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setIdVehiculo(entity.getVehiculo() != null ? entity.getVehiculo().getId() : null);
        if (entity.getDocumentaciones() != null) {
            dto.setDocumentacionIds(entity.getDocumentaciones().stream()
                    .filter(Objects::nonNull)
                    .map(Documentacion::getId)
                    .collect(Collectors.toList()));
        } else {
            dto.setDocumentacionIds(new ArrayList<>());
        }
        return dto;
    }

    public Alquiler toEntity(AlquilerDTO dto, Cliente cliente, Vehiculo vehiculo, List<Documentacion> documentaciones) {
        if (dto == null) return null;
        Alquiler entity = new Alquiler();
        entity.setId(dto.getId());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setCliente(cliente);
        entity.setVehiculo(vehiculo);
        entity.setDocumentaciones(documentaciones != null ? documentaciones : new ArrayList<>());
        return entity;
    }
}
