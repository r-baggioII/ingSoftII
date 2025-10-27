import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
export declare function fetchPersonas(params: {
    apellido?: string;
    dni?: number;
    page?: number;
    size?: number;
}): Promise<Page<PersonaDTO>>;
export declare function fetchPersona(id: number): Promise<PersonaDTO>;
export declare function createPersona(persona: PersonaDTO): Promise<PersonaDTO>;
export declare function updatePersona(id: number, persona: PersonaDTO): Promise<PersonaDTO>;
export declare function removePersona(id: number): Promise<void>;
export declare function fetchPersonaLibros(id: number, params?: {
    page?: number;
    size?: number;
}): Promise<Page<LibroDTO>>;
