-- Script para limpiar las tablas duplicadas
-- Ejecuta este script en MariaDB ANTES de reiniciar la aplicación

USE mecanico_db;

-- Desactivar foreign key checks temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar TODAS las tablas
DROP TABLE IF EXISTS Cliente;
DROP TABLE IF EXISTS HistorialArreglo;
DROP TABLE IF EXISTS Imagen;
DROP TABLE IF EXISTS Mecanico;
DROP TABLE IF EXISTS Persona;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Vehiculo;
DROP TABLE IF EXISTS arreglo_mecanico;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS historial_arreglo;
DROP TABLE IF EXISTS mecanico;
DROP TABLE IF EXISTS persona;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS vehiculo_cliente;

-- Reactivar foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Verificar que no queden tablas
SHOW TABLES;
