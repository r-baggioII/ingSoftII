import { apiDelete, apiGet, apiPost, apiPut } from './http';
function toPayload(autor) {
    return {
        nombre: autor.nombre,
        apellido: autor.apellido,
        biografia: autor.biografia
    };
}
export async function fetchAutores() {
    return apiGet('/autores');
}
export async function fetchAutor(id) {
    return apiGet(`/autores/${id}`);
}
export async function createAutor(autor) {
    return apiPost('/autores', toPayload(autor));
}
export async function updateAutor(id, autor) {
    return apiPut(`/autores/${id}`, toPayload(autor));
}
export async function removeAutor(id) {
    return apiDelete(`/autores/${id}`);
}
