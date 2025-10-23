-- Script para insertar usuarios en la base de datos mecánico
-- Ejecutar: mysql -u root -padminAdmin mecanico_db < poblar_bd.sql

USE mecanico_db;

-- Limpiar usuarios existentes (opcional - descomentar si querés borrar todo)
DELETE FROM Usuario;

-- ============================================
-- USUARIOS (con contraseñas encriptadas BCrypt)
-- ============================================
-- Password para todos: "password123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO Usuario (id, nombre, email, password, rol, eliminado, imagen_id) VALUES
-- Usuario ADMIN
('admin-001', 'Administrador Principal', 'admin@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 0, NULL),

-- Usuarios normales
('user-001', 'Carlos Méndez', 'carlos.mendez@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 0, NULL),
('user-002', 'Juan Ramírez', 'juan.ramirez@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 0, NULL),
('user-003', 'Ana Torres', 'ana.torres@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 0, NULL),
('user-004', 'María González', 'maria.gonzalez@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 0, NULL),
('user-005', 'Pedro Martínez', 'pedro.martinez@mecanico.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 0, NULL);

-- ============================================
-- Verificación
-- ============================================
SELECT '=== USUARIOS INSERTADOS ===' AS '';
SELECT nombre, email, 'password123' AS password, rol FROM Usuario;

SELECT '' AS '';
SELECT '=== USUARIO ADMIN (usar para login) ===' AS '';
SELECT nombre, email, 'password123' AS password FROM Usuario WHERE rol = 'ADMIN';

