import { apiDelete, apiGet, apiPost, apiPut } from './http';
import { toAutorPayload } from './payloads';
export async function fetchAutores() {
    return apiGet('/autores');
}
export async function fetchAutor(id) {
    return apiGet(`/autores/${id}`);
}
export async function createAutor(autor) {
    return apiPost('/autores', toAutorPayload(autor));
}
export async function updateAutor(id, autor) {
    return apiPut(`/autores/${id}`, toAutorPayload(autor));
}
export async function removeAutor(id) {
    return apiDelete(`/autores/${id}`);
}
