import type { LocalidadDTO } from './LocalidadDTO';

export interface DomicilioDTO {
  id?: number;
  calle: string;
  numero: number;
  localidad: LocalidadDTO;
  localidadId?: number;
}
