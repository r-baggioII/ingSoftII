package com.ejemplo.biblioteca.web.dto.adapter;

/**
 * Adaptador bidireccional entre DTOs y entidades del dominio.
 *
 * @param <Req> tipo de solicitud entrante
 * @param <Entity> tipo de la entidad de dominio
 * @param <Res> tipo de respuesta a exponer
 */
public interface EntityAdapter<Req, Entity, Res> {

    Entity toEntity(Req request);

    Entity toEntity(Req request, Long id);

    Res toDto(Entity entity);
}
