import { apiDelete, apiGet, apiPost, apiPut } from './http';
import type { AutorDTO } from '../dto/AutorDTO';
import { toAutorPayload, type AutorPayload } from './payloads';

export async function fetchAutores(): Promise<AutorDTO[]> {
  return apiGet<AutorDTO[]>('/autores');
}

export async function fetchAutor(id: number): Promise<AutorDTO> {
  return apiGet<AutorDTO>(`/autores/${id}`);
}

export async function createAutor(autor: AutorDTO): Promise<AutorDTO> {
  return apiPost<AutorDTO, AutorPayload>('/autores', toAutorPayload(autor));
}

export async function updateAutor(id: number, autor: AutorDTO): Promise<AutorDTO> {
  return apiPut<AutorDTO, AutorPayload>(`/autores/${id}`, toAutorPayload(autor));
}

export async function removeAutor(id: number): Promise<void> {
  return apiDelete(`/autores/${id}`);
}
