import type { Page } from './types';
import type { LibroDTO } from '../dto/LibroDTO';
export interface LibroMutationInput {
    libro: LibroDTO;
    pdfFile?: File | null;
}
export declare function fetchLibros(params: {
    autorId?: number;
    personaId?: number;
    genero?: string;
    page?: number;
    size?: number;
}): Promise<Page<LibroDTO>>;
export declare function fetchLibro(id: number): Promise<LibroDTO>;
export declare function createLibro(input: LibroMutationInput): Promise<LibroDTO>;
export declare function updateLibro(id: number, input: LibroMutationInput): Promise<LibroDTO>;
export declare function removeLibro(id: number): Promise<void>;
