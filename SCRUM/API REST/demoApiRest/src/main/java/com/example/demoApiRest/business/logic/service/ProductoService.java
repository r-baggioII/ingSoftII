package com.example.demoApiRest.business.logic.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demoApiRest.business.domain.entity.Categoria;
import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.domain.entity.Producto;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.percistence.repository.ProductoRepository;

import jakarta.persistence.NoResultException;

@Service
public class ProductoService {

	@Autowired
	private CategoriaService categoriaService; 
	
	@Autowired
	private MarcaService marcaService; 
	
	@Autowired
	private ProductoRepository repository; 
    
    public void validar(String nombre, double precio)throws ErrorServiceException {
        
        try{
        	
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            
            if (precio <= 0) {
                throw new ErrorServiceException("El precio del producto debe ser mayor que cero");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

	@Transactional
    public void crearProducto(String idCategoria, String idMarca, String nombre, double precio) throws ErrorServiceException {

        try {
            
            validar(nombre, precio);

            Categoria categoria = categoriaService.buscarCategoria(idCategoria); 
            Marca marca = marcaService.buscarMarca(idMarca);
            
            try {
            	Producto productoAux = repository.buscarProductoPorNombre(nombre);
            	if (productoAux != null && !productoAux.isEliminado()) {
                 throw new ErrorServiceException("Existe un producto con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Producto producto = new Producto();
            producto.setId(UUID.randomUUID().toString());
            producto.setCategoria(categoria);
            producto.setMarca(marca);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setEliminado(false);

            repository.save(producto);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}

	@Transactional
    public void modificarProducto(String idProducto, String idCategoria, String idMarca, String nombre, double precio) throws ErrorServiceException {

        try {

        	validar(nombre, precio);
        	
        	Categoria categoria = categoriaService.buscarCategoria(idCategoria); 
        	Marca marca = marcaService.buscarMarca(idMarca);
            Producto producto = buscarProducto(idProducto);

            try{
                Producto productoExsitente = repository.buscarProductoPorNombre(nombre);
                if (productoExsitente != null && !productoExsitente.getId().equals(idProducto) && !productoExsitente.isEliminado()){
                  throw new ErrorServiceException("Existe un producto con el nombre indicado");  
                }
            } catch (NoResultException ex) {}

            producto.setCategoria(categoria);
            producto.setMarca(marca);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setEliminado(false);
            
            repository.save(producto);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
	
	public Producto buscarProducto(String id) throws ErrorServiceException {

        try {
            
            if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el producto");
            }

            Optional<Producto> optional = repository.findById(id);
            Producto producto = null;
            if (optional.isPresent()) {
            	producto= optional.get();
    			if (producto.isEliminado()){
                    throw new ErrorServiceException("No se encuentra el producto indicado");
                }
    		}
            
            return producto;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    @Transactional
    public void eliminarProducto(String id) throws ErrorServiceException {

        try {

            Producto producto = buscarProducto(id);
            producto.setEliminado(true);
            
            repository.save(producto);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public List<Producto> listarProducto() throws ErrorServiceException {
        try {
            
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
    
    public List<Producto> listarProductoActivo() throws ErrorServiceException {
        try {
            
            return repository.listarProductoActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
}
