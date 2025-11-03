package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.BaseDTO;
import com.uncuyo.greedy_cars.shared.template.entity.BaseEntity;

import java.util.List;

/**
 * Interface base para todos los mappers del sistema usando MapStruct.
 * Define los métodos comunes de conversión entre entidades y DTOs.
 * 
 * @param <E> Tipo de la entidad (debe extender de BaseEntity)
 * @param <D> Tipo del DTO (debe extender de BaseDTO)
 * @param <ID> Tipo del identificador
 */
public interface BaseMapper<E extends BaseEntity<ID>, D extends BaseDTO<ID>, ID> {
    
    /**
     * Convierte una entidad a su DTO correspondiente
     * 
     * @param entity La entidad a convertir
     * @return El DTO resultante
     */
    D toDTO(E entity);
    
    /**
     * Convierte un DTO a su entidad correspondiente
     * 
     * @param dto El DTO a convertir
     * @return La entidad resultante
     */
    E toEntity(D dto);
    
    /**
     * Convierte una lista de entidades a una lista de DTOs
     * 
     * @param entities Lista de entidades a convertir
     * @return Lista de DTOs resultante
     */
    List<D> toDTOList(List<E> entities);
    
    /**
     * Convierte una lista de DTOs a una lista de entidades
     * 
     * @param dtos Lista de DTOs a convertir
     * @return Lista de entidades resultante
     */
    List<E> toEntityList(List<D> dtos);
}
