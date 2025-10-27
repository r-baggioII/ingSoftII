import { apiDelete, apiGet, apiPost, apiPut } from './http';
import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';
import { toLibroPayload, type LibroPayload } from './payloads';

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

export async function createLibro(libro: LibroDTO): Promise<LibroDTO> {
  return apiPost<LibroDTO, LibroPayload>('/libros', toLibroPayload(libro));
}

export async function updateLibro(id: number, libro: LibroDTO): Promise<LibroDTO> {
  return apiPut<LibroDTO, LibroPayload>(`/libros/${id}`, toLibroPayload(libro));
}

export async function removeLibro(id: number): Promise<void> {
  return apiDelete(`/libros/${id}`);
}
