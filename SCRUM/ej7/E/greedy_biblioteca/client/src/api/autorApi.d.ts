import type { AutorDTO } from '../dto/AutorDTO';
export declare function fetchAutores(): Promise<AutorDTO[]>;
export declare function fetchAutor(id: number): Promise<AutorDTO>;
export declare function createAutor(autor: AutorDTO): Promise<AutorDTO>;
export declare function updateAutor(id: number, autor: AutorDTO): Promise<AutorDTO>;
export declare function removeAutor(id: number): Promise<void>;
