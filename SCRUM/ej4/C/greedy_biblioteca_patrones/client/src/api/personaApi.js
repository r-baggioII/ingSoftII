import { apiDelete, apiGet, apiPost, apiPut } from './http';
function toPayload(persona) {
    return {
        nombre: persona.nombre,
        apellido: persona.apellido,
        dni: persona.dni,
        domicilio: {
            calle: persona.domicilio.calle,
            numero: persona.domicilio.numero,
            localidadId: persona.domicilio.localidadId ?? persona.domicilio.localidad.id
        }
    };
}
export async function fetchPersonas(params) {
    return apiGet('/personas', params);
}
export async function fetchPersona(id) {
    return apiGet(`/personas/${id}`);
}
export async function createPersona(persona) {
    return apiPost('/personas', toPayload(persona));
}
export async function updatePersona(id, persona) {
    return apiPut(`/personas/${id}`, toPayload(persona));
}
export async function removePersona(id) {
    return apiDelete(`/personas/${id}`);
}
export async function fetchPersonaLibros(id, params = {}) {
    return apiGet(`/personas/${id}/libros`, params);
}
