import { apiDelete, apiGet, apiPost, apiPut } from './http';
import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';

interface LibroPayload {
  titulo: string;
  fecha: string;
  genero: string;
  paginas: number;
  autorId: number;
  personaId: number;
}

function toPayload(libro: LibroDTO): LibroPayload {
  return {
    titulo: libro.titulo,
    fecha: libro.fecha,
    genero: libro.genero,
    paginas: libro.paginas,
    autorId: libro.autorId ?? libro.autor.id!,
    personaId: libro.personaId ?? libro.persona.id!
  };
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

export async function createLibro(libro: LibroDTO): Promise<LibroDTO> {
  return apiPost<LibroDTO, LibroPayload>('/libros', toPayload(libro));
}

export async function updateLibro(id: number, libro: LibroDTO): Promise<LibroDTO> {
  return apiPut<LibroDTO, LibroPayload>(`/libros/${id}`, toPayload(libro));
}

export async function removeLibro(id: number): Promise<void> {
  return apiDelete(`/libros/${id}`);
}
