package com.example.demoApiRest.business.logic.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demoApiRest.business.domain.entity.Categoria;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.percistence.repository.CategoriaRepository;

import jakarta.persistence.NoResultException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repository; 
    
    public void validar(String nombre)throws ErrorServiceException {
        
        try{
            
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

	@Transactional
    public void crearCategoria(String nombre) throws ErrorServiceException {

        try {
            
            validar(nombre);

            try {
            	Categoria categoriaAux = repository.buscarCategoriaPorNombre(nombre);
            	if (categoriaAux != null && !categoriaAux.isEliminado()) {
                 throw new ErrorServiceException("Existe un categoria con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Categoria categoria = new Categoria();
            categoria.setId(UUID.randomUUID().toString());
            categoria.setNombre(nombre);
            categoria.setEliminado(false);

            repository.save(categoria);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}

	@Transactional
    public void modificarCategoria(String idCategoria, String nombre) throws ErrorServiceException {

        try {

        	validar(nombre);
        	
            Categoria categoria = buscarCategoria(idCategoria);

            try{
                Categoria categoriaExsitente = repository.buscarCategoriaPorNombre(nombre);
                if (categoriaExsitente != null && !categoriaExsitente.getId().equals(idCategoria) && !categoriaExsitente.isEliminado()){
                  throw new ErrorServiceException("Existe un categoria con el nombre indicado");  
                }
            } catch (NoResultException ex) {}

            categoria.setNombre(nombre);
            categoria.setEliminado(false);
            
            repository.save(categoria);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
	
	public Categoria buscarCategoria(String id) throws ErrorServiceException {

        try {
            
            if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el categoria");
            }

            Optional<Categoria> optional = repository.findById(id);
            Categoria categoria = null;
            if (optional.isPresent()) {
            	categoria= optional.get();
    			if (categoria.isEliminado()){
                    throw new ErrorServiceException("No se encuentra el categoria indicado");
                }
    		}
            
            return categoria;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    @Transactional
    public void eliminarCategoria(String id) throws ErrorServiceException {

        try {

            Categoria categoria = buscarCategoria(id);
            categoria.setEliminado(true);
            
            repository.save(categoria);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public List<Categoria> listarCategoria() throws ErrorServiceException {
        try {
            
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
    
    public List<Categoria> listarCategoriaActivo() throws ErrorServiceException {
        try {
            
            return repository.listarCategoriaActiva();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
}