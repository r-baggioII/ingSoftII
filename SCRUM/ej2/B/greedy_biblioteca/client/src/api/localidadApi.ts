import { apiGet, apiPost } from './http';
import type { LocalidadDTO } from '../dto/LocalidadDTO';

export async function fetchLocalidades(): Promise<LocalidadDTO[]> {
  return apiGet<LocalidadDTO[]>('/localidades');
}

export async function createLocalidad(denominacion: string): Promise<LocalidadDTO> {
  return apiPost<LocalidadDTO, { denominacion: string }>('/localidades', { denominacion });
}
