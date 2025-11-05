package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends BaseRepository<Imagen, String> {
    
    /**
     * Busca todas las imágenes por tipo de imagen y que no estén eliminadas.
     * 
     * @param tipoImagen el tipo de imagen a buscar
     * @return lista de imágenes del tipo especificado
     */
    List<Imagen> findByTipoImagenAndEliminadoIsFalse(TipoImagen tipoImagen);
    
    /**
     * Busca imágenes por nombre que contenga el texto especificado y que no estén eliminadas.
     * 
     * @param nombre el texto a buscar en el nombre
     * @return lista de imágenes que coincidan con la búsqueda
     */
    List<Imagen> findByNombreContainingAndEliminadoIsFalse(String nombre);
    
    /**
     * Busca una imagen por su nombre exacto que no esté eliminada.
     *
     * @param nombre el nombre de la imagen
     * @return un Optional con la imagen si se encuentra
     */
    Optional<Imagen> findByNombreAndEliminadoFalse(String nombre);
}
