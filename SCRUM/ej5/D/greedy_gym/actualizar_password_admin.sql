-- Script para actualizar manualmente la contraseña del usuario admin
-- Contraseña: admin123
-- Hash BCrypt: $2a$10$8.H5JHJbvVkCKRpYN5KJXuP1Iv8PZYZTqHfYrqvqX0L1YdUHKhXKm

-- OPCIÓN 1: Actualizar usuario admin existente
UPDATE usuarios 
SET clave = '$2a$10$8.H5JHJbvVkCKRpYN5KJXuP1Iv8PZYZTqHfYrqvqX0L1YdUHKhXKm'
WHERE nombreUsuario = 'admin';

-- OPCIÓN 2: Verificar el usuario admin
SELECT id, nombreUsuario, clave, rol, eliminado 
FROM usuarios 
WHERE nombreUsuario = 'admin';

-- NOTA: El hash BCrypt anterior corresponde a la contraseña "admin123"
-- Si reinicias la aplicación, el StartupAdminSeeder actualizará automáticamente
-- cualquier contraseña en texto plano a BCrypt
