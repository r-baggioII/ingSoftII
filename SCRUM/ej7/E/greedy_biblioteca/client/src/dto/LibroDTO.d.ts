import type { AutorDTO } from './AutorDTO';
import type { PersonaDTO } from './PersonaDTO';
export interface LibroDTO {
    id?: number;
    titulo: string;
    fecha: string;
    genero: string;
    paginas: number;
    autor: AutorDTO;
    persona: Pick<PersonaDTO, 'id' | 'nombre' | 'apellido'>;
    autorId?: number;
    personaId?: number;
}
