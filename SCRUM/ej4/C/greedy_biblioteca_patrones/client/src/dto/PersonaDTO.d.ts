import type { DomicilioDTO } from './DomicilioDTO';
export interface PersonaDTO {
    id?: number;
    nombre: string;
    apellido: string;
    dni: number;
    domicilio: DomicilioDTO;
}
