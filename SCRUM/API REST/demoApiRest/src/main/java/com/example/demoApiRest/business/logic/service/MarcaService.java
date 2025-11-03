package com.example.demoApiRest.business.logic.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.percistence.repository.MarcaRepository;

import jakarta.persistence.NoResultException;

@Service
public class MarcaService {

	@Autowired
	private MarcaRepository repository; 
    
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
    public void crearMarca(String nombre) throws ErrorServiceException {

        try {
            
            validar(nombre);

            try {
            	Marca marcaAux = repository.buscarMarcaPorNombre(nombre);
            	if (marcaAux != null && !marcaAux.isEliminado()) {
                 throw new ErrorServiceException("Existe un marca con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Marca marca = new Marca();
            marca.setId(UUID.randomUUID().toString());
            marca.setNombre(nombre);
            marca.setEliminado(false);

            repository.save(marca);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}

	@Transactional
    public void modificarMarca(String idMarca, String nombre) throws ErrorServiceException {

        try {

        	validar(nombre);
        	
            Marca marca = buscarMarca(idMarca);

            try{
                Marca marcaExsitente = repository.buscarMarcaPorNombre(nombre);
                if (marcaExsitente != null && !marcaExsitente.getId().equals(idMarca) && !marcaExsitente.isEliminado()){
                  throw new ErrorServiceException("Existe un marca con el nombre indicado");  
                }
            } catch (NoResultException ex) {}

            marca.setNombre(nombre);
            marca.setEliminado(false);
            
            repository.save(marca);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
	
	public Marca buscarMarca(String id) throws ErrorServiceException {

        try {
            
            if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el marca");
            }

            Optional<Marca> optional = repository.findById(id);
            Marca marca = null;
            if (optional.isPresent()) {
            	marca= optional.get();
    			if (marca.isEliminado()){
                    throw new ErrorServiceException("No se encuentra el marca indicado");
                }
    		}
            
            return marca;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    @Transactional
    public void eliminarMarca(String id) throws ErrorServiceException {

        try {

            Marca marca = buscarMarca(id);
            marca.setEliminado(true);
            
            repository.save(marca);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public List<Marca> listarMarca() throws ErrorServiceException {
        try {
            
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
    
    public List<Marca> listarMarcaActivo() throws ErrorServiceException {
        try {
            
            return repository.listarMarcaActiva();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
}