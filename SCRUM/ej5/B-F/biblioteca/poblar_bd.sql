-- Script para poblar la base de datos de biblioteca con datos de ejemplo
-- Ejecutar: mysql -u root -proot demospringsecuritybibliotecadb < poblar_bd.sql

USE demospringsecuritybibliotecadb;

-- Limpiar datos previos (opcional, comentar si querés mantener datos existentes)
-- DELETE FROM Libro;
-- DELETE FROM Usuario;
-- DELETE FROM Autor;
-- DELETE FROM Editorial;

-- ============================================
-- USUARIOS (con contraseñas encriptadas BCrypt)
-- ============================================
-- Password para todos: "password123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO usuario (id, nombre, email, password, rol, eliminado, imagen_id) VALUES
-- Usuario ADMIN
('admin-001', 'Administrador Principal', 'admin@biblioteca.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', false, NULL),

-- Usuarios normales (USER)
('user-001', 'María González', 'maria.gonzalez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', false, NULL),
('user-002', 'Juan Pérez', 'juan.perez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', false, NULL),
('user-003', 'Ana Martínez', 'ana.martinez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', false, NULL);

-- ============================================
-- AUTORES
-- ============================================
INSERT INTO Autor (id, nombre, eliminado) VALUES
('autor-001', 'Gabriel García Márquez', false),
('autor-002', 'Jorge Luis Borges', false),
('autor-003', 'Isabel Allende', false),
('autor-004', 'Julio Cortázar', false),
('autor-005', 'Mario Vargas Llosa', false),
('autor-006', 'Pablo Neruda', false),
('autor-007', 'Octavio Paz', false),
('autor-008', 'Carlos Fuentes', false),
('autor-009', 'Laura Esquivel', false),
('autor-010', 'Eduardo Galeano', false);

-- ============================================
-- EDITORIALES
-- ============================================
INSERT INTO Editorial (id, nombre, eliminado) VALUES
('edit-001', 'Editorial Sudamericana', false),
('edit-002', 'Alfaguara', false),
('edit-003', 'Planeta', false),
('edit-004', 'Penguin Random House', false),
('edit-005', 'Anagrama', false),
('edit-006', 'Tusquets Editores', false),
('edit-007', 'Editorial Losada', false),
('edit-008', 'Fondo de Cultura Económica', false);

-- ============================================
-- LIBROS
-- ============================================
INSERT INTO Libro (id, isbn, titulo, ejemplares, eliminado, autor_id, editorial_id, imagen_id) VALUES
-- Gabriel García Márquez
('libro-001', 9788497592208, 'Cien años de soledad', 15, false, 'autor-001', 'edit-002', NULL),
('libro-002', 9788420471839, 'El amor en los tiempos del cólera', 10, false, 'autor-001', 'edit-002', NULL),
('libro-003', 9788497592444, 'Crónica de una muerte anunciada', 8, false, 'autor-001', 'edit-002', NULL),

-- Jorge Luis Borges
('libro-004', 9788499089515, 'Ficciones', 12, false, 'autor-002', 'edit-004', NULL),
('libro-005', 9788420652245, 'El Aleph', 10, false, 'autor-002', 'edit-002', NULL),

-- Isabel Allende
('libro-006', 9788401352836, 'La casa de los espíritus', 10, false, 'autor-003', 'edit-003', NULL),
('libro-007', 9788497592437, 'Paula', 8, false, 'autor-003', 'edit-002', NULL),

-- Julio Cortázar
('libro-008', 9788420471440, 'Rayuela', 15, false, 'autor-004', 'edit-002', NULL),
('libro-009', 9788420474625, 'Final del juego', 7, false, 'autor-004', 'edit-002', NULL),
('libro-010', 9788420473147, 'Bestiario', 6, false, 'autor-004', 'edit-002', NULL),

-- Mario Vargas Llosa
('libro-011', 9788420471686, 'La ciudad y los perros', 9, false, 'autor-005', 'edit-002', NULL),
('libro-012', 9788420412146, 'Conversación en La Catedral', 7, false, 'autor-005', 'edit-002', NULL),

-- Pablo Neruda
('libro-013', 9788420634289, 'Veinte poemas de amor y una canción desesperada', 20, false, 'autor-006', 'edit-002', NULL),
('libro-014', 9788432248238, 'Canto General', 5, false, 'autor-006', 'edit-001', NULL),

-- Octavio Paz
('libro-015', 9788437620145, 'El laberinto de la soledad', 8, false, 'autor-007', 'edit-008', NULL),

-- Carlos Fuentes
('libro-016', 9788420471419, 'La muerte de Artemio Cruz', 6, false, 'autor-008', 'edit-002', NULL),
('libro-017', 9788420474052, 'Aura', 10, false, 'autor-008', 'edit-002', NULL),

-- Laura Esquivel
('libro-018', 9788497592154, 'Como agua para chocolate', 12, false, 'autor-009', 'edit-002', NULL),

-- Eduardo Galeano
('libro-019', 9788432311840, 'Las venas abiertas de América Latina', 8, false, 'autor-010', 'edit-001', NULL);

-- ============================================
-- Verificación
-- ============================================
SELECT '=== RESUMEN DE DATOS INSERTADOS ===' AS '';
SELECT CONCAT('Usuarios: ', COUNT(*)) AS total FROM Usuario;
SELECT CONCAT('Autores: ', COUNT(*)) AS total FROM Autor;
SELECT CONCAT('Editoriales: ', COUNT(*)) AS total FROM Editorial;
SELECT CONCAT('Libros: ', COUNT(*)) AS total FROM Libro;

SELECT '' AS '';
SELECT '=== USUARIO ADMIN (login con este usuario) ===' AS '';
SELECT nombre, email, 'password123' AS password, rol FROM Usuario WHERE rol = 'ADMIN';

SELECT '' AS '';
SELECT '=== USUARIOS NORMALES ===' AS '';
SELECT nombre, email, 'password123' AS password, rol FROM Usuario WHERE rol = 'USER';
