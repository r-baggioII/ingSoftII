import { apiDelete, apiGet, apiPost, apiPut } from './http';
function toPayload(libro) {
    const autorId = libro.autorId ?? libro.autor?.id;
    const personaId = libro.personaId ?? libro.persona?.id;
    if (!autorId)
        throw new Error('El autor es obligatorio');
    if (!personaId)
        throw new Error('La persona es obligatoria');
    return {
        titulo: libro.titulo,
        fecha: libro.fecha,
        genero: libro.genero,
        paginas: libro.paginas,
        autorId,
        personaId
    };
}
function buildFormData({ libro, pdfFile }) {
    const formData = new FormData();
    const payload = toPayload(libro);
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
    if (pdfFile) {
        formData.append('pdf', pdfFile);
    }
    return formData;
}
export async function fetchLibros(params) {
    return apiGet('/libros', params);
}
export async function fetchLibro(id) {
    return apiGet(`/libros/${id}`);
}
export async function createLibro(input) {
    const formData = buildFormData(input);
    return apiPost('/libros', formData);
}
export async function updateLibro(id, input) {
    const formData = buildFormData(input);
    return apiPut(`/libros/${id}`, formData);
}
export async function removeLibro(id) {
    return apiDelete(`/libros/${id}`);
}
