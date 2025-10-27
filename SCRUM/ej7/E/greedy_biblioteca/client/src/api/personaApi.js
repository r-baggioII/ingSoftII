import { apiDelete, apiGet, apiPost, apiPut } from './http';
import { toPersonaPayload } from './payloads';
export async function fetchPersonas(params) {
    return apiGet('/personas', params);
}
export async function fetchPersona(id) {
    return apiGet(`/personas/${id}`);
}
export async function createPersona(persona) {
    return apiPost('/personas', toPersonaPayload(persona));
}
export async function updatePersona(id, persona) {
    return apiPut(`/personas/${id}`, toPersonaPayload(persona));
}
export async function removePersona(id) {
    return apiDelete(`/personas/${id}`);
}
export async function fetchPersonaLibros(id, params = {}) {
    return apiGet(`/personas/${id}/libros`, params);
}
