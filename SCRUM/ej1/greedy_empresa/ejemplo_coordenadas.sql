-- Script para agregar coordenadas de ejemplo a direcciones existentes
-- Ejecutar después de que Hibernate cree las nuevas columnas

-- Ejemplo: Plaza Independencia, Mendoza, Argentina
-- UPDATE direccion SET latitud = '-32.88970575178735', longitud = '-68.84457510855037' 
-- WHERE id = 'ID_DE_TU_DIRECCION_AQUI';

-- Ejemplo: Obelisco, Buenos Aires, Argentina  
-- UPDATE direccion SET latitud = '-34.6037389', longitud = '-58.3815704'
-- WHERE id = 'OTRO_ID_DE_DIRECCION';

-- Ejemplo: Centro de Córdoba, Argentina
-- UPDATE direccion SET latitud = '-31.4200833', longitud = '-64.1887761'
-- WHERE id = 'OTRO_ID_MAS';

-- Verificar que las columnas se agregaron correctamente
-- SELECT id, calle, numero, latitud, longitud FROM direccion;

-- Ver direcciones con coordenadas
-- SELECT id, calle, numero, latitud, longitud 
-- FROM direccion 
-- WHERE latitud IS NOT NULL AND longitud IS NOT NULL;