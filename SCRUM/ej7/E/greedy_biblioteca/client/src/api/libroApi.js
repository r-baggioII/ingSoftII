import { apiDelete, apiGet, apiPost, apiPut } from './http';
import { toLibroPayload } from './payloads';
export async function fetchLibros(params) {
    return apiGet('/libros', params);
}
export async function fetchLibro(id) {
    return apiGet(`/libros/${id}`);
}
export async function createLibro(libro) {
    return apiPost('/libros', toLibroPayload(libro));
}
export async function updateLibro(id, libro) {
    return apiPut(`/libros/${id}`, toLibroPayload(libro));
}
export async function removeLibro(id) {
    return apiDelete(`/libros/${id}`);
}
