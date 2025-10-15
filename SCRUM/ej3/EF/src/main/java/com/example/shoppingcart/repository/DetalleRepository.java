package com.example.shoppingcart.repository;

import com.example.shoppingcart.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, String> {
}