import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';
export declare function fetchLibros(params: {
    autorId?: number;
    personaId?: number;
    genero?: string;
    criterio?: 'TITULO' | 'GENERO' | 'AUTOR';
    valor?: string;
    page?: number;
    size?: number;
}): Promise<Page<LibroDTO>>;
export declare function fetchLibro(id: number): Promise<LibroDTO>;
export declare function createLibro(libro: LibroDTO): Promise<LibroDTO>;
export declare function updateLibro(id: number, libro: LibroDTO): Promise<LibroDTO>;
export declare function removeLibro(id: number): Promise<void>;
export declare function fetchLibrosPorAutorIterador(autorId: number): Promise<LibroDTO[]>;
