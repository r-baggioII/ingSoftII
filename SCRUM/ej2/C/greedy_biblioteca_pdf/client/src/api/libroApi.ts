import { apiDelete, apiGet, apiPost, apiPut } from './http';
import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';

export interface LibroMutationInput {
  libro: LibroDTO;
  pdfFile?: File | null;
}

function toPayload(libro: LibroDTO) {
  const autorId = libro.autorId ?? libro.autor?.id;
  const personaId = libro.personaId ?? libro.persona?.id;
  if (!autorId) throw new Error('El autor es obligatorio');
  if (!personaId) throw new Error('La persona es obligatoria');
  return {
    titulo: libro.titulo,
    fecha: libro.fecha,
    genero: libro.genero,
    paginas: libro.paginas,
    autorId,
    personaId
  };
}

function buildFormData({ libro, pdfFile }: LibroMutationInput): FormData {
  const formData = new FormData();
  const payload = toPayload(libro);
  formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
  if (pdfFile) {
    formData.append('pdf', pdfFile);
  }
  return formData;
}

export async function fetchLibros(params: {
  autorId?: number;
  personaId?: number;
  genero?: string;
  page?: number;
  size?: number;
}): Promise<Page<LibroDTO>> {
  return apiGet<Page<LibroDTO>>('/libros', params);
}

export async function fetchLibro(id: number): Promise<LibroDTO> {
  return apiGet<LibroDTO>(`/libros/${id}`);
}

export async function createLibro(input: LibroMutationInput): Promise<LibroDTO> {
  const formData = buildFormData(input);
  return apiPost<LibroDTO, FormData>('/libros', formData);
}

export async function updateLibro(id: number, input: LibroMutationInput): Promise<LibroDTO> {
  const formData = buildFormData(input);
  return apiPut<LibroDTO, FormData>(`/libros/${id}`, formData);
}

export async function removeLibro(id: number): Promise<void> {
  return apiDelete(`/libros/${id}`);
}
