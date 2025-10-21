import { apiDelete, apiGet, apiPost, apiPut } from './http';
function toPayload(libro) {
    return {
        titulo: libro.titulo,
        fecha: libro.fecha,
        genero: libro.genero,
        paginas: libro.paginas,
        tipo: libro.tipo,
        pesoGramos: libro.tipo === 'FISICO' ? libro.pesoGramos ?? 0 : undefined,
        tamanoMb: libro.tipo === 'DIGITAL' ? libro.tamanoMb ?? 0 : undefined,
        autorId: libro.autorId ?? libro.autor.id,
        personaId: libro.personaId ?? libro.persona.id
    };
}
export async function fetchLibros(params) {
    return apiGet('/libros', params);
}
export async function fetchLibro(id) {
    return apiGet(`/libros/${id}`);
}
export async function createLibro(libro) {
    return apiPost('/libros', toPayload(libro));
}
export async function updateLibro(id, libro) {
    return apiPut(`/libros/${id}`, toPayload(libro));
}
export async function removeLibro(id) {
    return apiDelete(`/libros/${id}`);
}
export async function fetchLibrosPorAutorIterador(autorId) {
    return apiGet(`/libros/autor/${autorId}/iterador`);
}
