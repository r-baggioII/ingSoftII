package com.example.demoApiRestConsumer.business.persistence.rest;



import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demoApiRestConsumer.business.domain.dto.CategoriaDTO;
import com.example.demoApiRestConsumer.business.logic.error.ErrorServiceException;



@Service
public class CategoriaDAORest {

	@Autowired
    private RestTemplate restTemplate;
	
	public void crear(CategoriaDTO categoria)throws ErrorServiceException {
		
      try {
		 
		 String uri = "http://localhost:8080/api/v1/categoria";
		 restTemplate.postForEntity(uri, categoria, CategoriaDTO.class);
	     
      } catch (Exception ex){
          ex.printStackTrace();
          throw new ErrorServiceException("Error de Sistemas");
      }   
	}
	
	public void actualizar(CategoriaDTO categoria) throws ErrorServiceException {
		
	   try {
		    
		 String uri = "http://localhost:8080/api/v1/categoria/"+ categoria.getId();
		 restTemplate.put(uri, categoria);
		 
       } catch (Exception ex){
           ex.printStackTrace();
           throw new ErrorServiceException("Error de Sistemas");
       } 
	}
	
	public void eliminar(String id)  throws ErrorServiceException {
		
	   try {	
		   
		String uri = "http://localhost:8080/api/v1/categoria/" + id;
		restTemplate.delete(uri);
	   
	   } catch (Exception ex){
           ex.printStackTrace();
           throw new ErrorServiceException("Error de Sistemas");
       } 	
	}
	
	public CategoriaDTO buscar(String id)  throws ErrorServiceException {
		
	   try {
		   
		String uri = "http://localhost:8080/api/v1/categoria/" + id;
		
		ResponseEntity<CategoriaDTO> response = restTemplate.getForEntity(uri,CategoriaDTO.class);
        CategoriaDTO categoria = response.getBody();
        
        return  categoria;
        
	   } catch (Exception ex){
           ex.printStackTrace();
           throw new ErrorServiceException("Error de Sistemas");
       }  
	}
	
	public List<CategoriaDTO> listarCategoriaActivo() throws ErrorServiceException {
		
	  try {	
		  
		String uri="http://localhost:8080/api/v1/categoria/listarCategoria";
        
		ResponseEntity<CategoriaDTO[]> response = restTemplate.getForEntity(uri,CategoriaDTO[].class);
        CategoriaDTO[] categorias = response.getBody();
        List<CategoriaDTO> m = Arrays.asList(categorias);
        
        return  m;
        
	  } catch (Exception ex){
          ex.printStackTrace();
          throw new ErrorServiceException("Error de Sistemas");
      }  
    }
	
}