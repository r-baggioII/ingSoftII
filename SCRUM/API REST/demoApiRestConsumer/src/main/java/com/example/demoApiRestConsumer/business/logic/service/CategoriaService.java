package com.example.demoApiRestConsumer.business.logic.service;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demoApiRestConsumer.business.domain.dto.CategoriaDTO;
import com.example.demoApiRestConsumer.business.logic.error.ErrorServiceException;
import com.example.demoApiRestConsumer.business.persistence.rest.CategoriaDAORest;


@Service
public class CategoriaService {

	@Autowired
	private CategoriaDAORest dao; 
    
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

    public void crearCategoria(String nombre) throws ErrorServiceException {

        try {
            
            validar(nombre);

            CategoriaDTO categoria = new CategoriaDTO();
            categoria.setId("");
            categoria.setNombre(nombre);
            categoria.setEliminado(false);

            dao.crear(categoria);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}

    public void modificarCategoria(String idCategoria, String nombre) throws ErrorServiceException {

        try {

        	validar(nombre);
        	
        	CategoriaDTO categoria = new CategoriaDTO();
            categoria.setId(idCategoria);
            categoria.setNombre(nombre);
            categoria.setEliminado(false);
            
            dao.actualizar(categoria);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
	
	public CategoriaDTO buscarCategoria(String id) throws ErrorServiceException {

        try {
            
            if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el categoria");
            }

            CategoriaDTO categoria = dao.buscar(id);
            
            return categoria;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public void eliminarCategoria(String id) throws ErrorServiceException {

        try {

        	if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el categoria");
            }
        	
            dao.eliminar(id);
           
        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public List<CategoriaDTO> listarCategoriaActivo() throws ErrorServiceException {
        try {
            
            return dao.listarCategoriaActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
    
}
