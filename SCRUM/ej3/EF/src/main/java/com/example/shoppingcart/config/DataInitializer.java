package com.example.shoppingcart.config;

import com.example.shoppingcart.*;
import com.example.shoppingcart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(ProveedorRepository proveedorRepository,
                                     ArticuloRepository articuloRepository,
                                     UsuarioRepository usuarioRepository,
                                     CarritoRepository carritoRepository,
                                     DetalleRepository detalleRepository) {
        return args -> {
            // Insertar proveedores
            Proveedor prov1 = new Proveedor();
            prov1.setId("prov1");
            prov1.setNombre("Proveedor A");
            prov1.setDireccion("Av. Corrientes 1234, Buenos Aires, Argentina");
            prov1.setLatitud(-34.6037);
            prov1.setLongitud(-58.3816);
            prov1.setEliminado(false);

            Proveedor prov2 = new Proveedor();
            prov2.setId("prov2");
            prov2.setNombre("Proveedor B");
            prov2.setDireccion("Av. Colón 567, Córdoba, Argentina");
            prov2.setLatitud(-31.4201);
            prov2.setLongitud(-64.1888);
            prov2.setEliminado(false);

            proveedorRepository.save(prov1);
            proveedorRepository.save(prov2);

            // Insertar artículos
            Articulo art1 = new Articulo();
            art1.setId("art1");
            art1.setNombre("Producto 1");
            art1.setPrecio(100.0);
            art1.setEliminado(false);
            art1.setProveedor(prov1);

            Articulo art2 = new Articulo();
            art2.setId("art2");
            art2.setNombre("Producto 2");
            art2.setPrecio(200.0);
            art2.setEliminado(false);
            art2.setProveedor(prov2);

            Articulo art3 = new Articulo();
            art3.setId("art3");
            art3.setNombre("Producto 3");
            art3.setPrecio(300.0);
            art3.setEliminado(false);
            art3.setProveedor(prov1);

            articuloRepository.save(art1);
            articuloRepository.save(art2);
            articuloRepository.save(art3);

            // Insertar usuario
            Usuario user1 = new Usuario();
            user1.setId("user1");
            user1.setNombre("Usuario Test");
            user1.setClave("pass");
            user1.setEliminado(false);

            usuarioRepository.save(user1);

            // Insertar carrito
            Carrito carrito = new Carrito();
            carrito.setId("carrito-123");
            carrito.setTotal(0.0);
            carrito.setEliminado(false);
            carrito.setUsuario(user1);

            carritoRepository.save(carrito);

            // Insertar detalles
            Detalle det1 = new Detalle();
            det1.setId("det1");
            det1.setEliminado(false);
            det1.setCarrito(carrito);
            det1.setArticulo(art1);

            Detalle det2 = new Detalle();
            det2.setId("det2");
            det2.setEliminado(false);
            det2.setCarrito(carrito);
            det2.setArticulo(art2);

            detalleRepository.save(det1);
            detalleRepository.save(det2);

            System.out.println("Data initialized successfully");
        };
    }
}