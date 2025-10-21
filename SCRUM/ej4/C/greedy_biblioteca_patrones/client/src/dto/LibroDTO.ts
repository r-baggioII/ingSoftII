import type { AutorDTO } from './AutorDTO';
import type { PersonaDTO } from './PersonaDTO';

export type TipoLibro = 'FISICO' | 'DIGITAL';

export interface LibroDTO {
  id?: number;
  titulo: string;
  fecha: string;
  genero: string;
  paginas: number;
  tipo: TipoLibro;
  pesoGramos?: number | null;
  tamanoMb?: number | null;
  autor: AutorDTO;
  persona: Pick<PersonaDTO, 'id' | 'nombre' | 'apellido'>;
  autorId?: number;
  personaId?: number;
}
