package com.example.shoppingcart.repository;

import com.example.shoppingcart.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, String> {
}