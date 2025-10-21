import { apiDelete, apiGet, apiPost, apiPut } from './http';
import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';

interface PersonaPayload {
  nombre: string;
  apellido: string;
  dni: number;
  domicilio: {
    calle: string;
    numero: number;
    localidadId: number;
  };
}

function toPayload(persona: PersonaDTO): PersonaPayload {
  return {
    nombre: persona.nombre,
    apellido: persona.apellido,
    dni: persona.dni,
    domicilio: {
      calle: persona.domicilio.calle,
      numero: persona.domicilio.numero,
      localidadId: persona.domicilio.localidadId ?? persona.domicilio.localidad.id
    }
  };
}

export async function fetchPersonas(params: {
  apellido?: string;
  dni?: number;
  page?: number;
  size?: number;
}): Promise<Page<PersonaDTO>> {
  return apiGet<Page<PersonaDTO>>('/personas', params);
}

export async function fetchPersona(id: number): Promise<PersonaDTO> {
  return apiGet<PersonaDTO>(`/personas/${id}`);
}

export async function createPersona(persona: PersonaDTO): Promise<PersonaDTO> {
  return apiPost<PersonaDTO, PersonaPayload>('/personas', toPayload(persona));
}

export async function updatePersona(id: number, persona: PersonaDTO): Promise<PersonaDTO> {
  return apiPut<PersonaDTO, PersonaPayload>(`/personas/${id}`, toPayload(persona));
}

export async function removePersona(id: number): Promise<void> {
  return apiDelete(`/personas/${id}`);
}

export async function fetchPersonaLibros(
  id: number,
  params: { page?: number; size?: number } = {}
): Promise<Page<LibroDTO>> {
  return apiGet<Page<LibroDTO>>(`/personas/${id}/libros`, params);
}
