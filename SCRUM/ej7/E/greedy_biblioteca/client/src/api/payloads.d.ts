import type { AutorDTO } from '../dto/AutorDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
export interface PersonaPayload {
    nombre: string;
    apellido: string;
    dni: number;
    domicilio: {
        calle: string;
        numero: number;
        localidadId: number;
    };
}
export interface AutorPayload {
    nombre: string;
    apellido: string;
    biografia: string;
}
export interface LibroPayload {
    titulo: string;
    fecha: string;
    genero: string;
    paginas: number;
    autorId: number;
    personaId: number;
}
export declare function toPersonaPayload(persona: PersonaDTO): PersonaPayload;
export declare function toAutorPayload(autor: AutorDTO): AutorPayload;
export declare function toLibroPayload(libro: LibroDTO): LibroPayload;
