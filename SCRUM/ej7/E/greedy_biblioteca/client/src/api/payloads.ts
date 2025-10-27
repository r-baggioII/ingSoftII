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

export function toPersonaPayload(persona: PersonaDTO): PersonaPayload {
  const localidad = persona.domicilio.localidad;
  const localidadId = persona.domicilio.localidadId ?? localidad?.id;
  if (!localidadId) {
    throw new Error('Localidad no definida en la persona');
  }

  return {
    nombre: persona.nombre,
    apellido: persona.apellido,
    dni: persona.dni,
    domicilio: {
      calle: persona.domicilio.calle,
      numero: persona.domicilio.numero,
      localidadId
    }
  };
}

export function toAutorPayload(autor: AutorDTO): AutorPayload {
  return {
    nombre: autor.nombre,
    apellido: autor.apellido,
    biografia: autor.biografia
  };
}

export function toLibroPayload(libro: LibroDTO): LibroPayload {
  const autorId = libro.autorId ?? libro.autor.id;
  const personaId = libro.personaId ?? libro.persona.id;
  if (!autorId || !personaId) {
    throw new Error('Libro sin identificadores de autor o persona');
  }

  return {
    titulo: libro.titulo,
    fecha: libro.fecha,
    genero: libro.genero,
    paginas: libro.paginas,
    autorId,
    personaId
  };
}
