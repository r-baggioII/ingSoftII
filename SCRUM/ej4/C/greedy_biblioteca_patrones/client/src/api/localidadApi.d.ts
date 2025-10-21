import type { LocalidadDTO } from '../dto/LocalidadDTO';
export declare function fetchLocalidades(): Promise<LocalidadDTO[]>;
export declare function createLocalidad(denominacion: string): Promise<LocalidadDTO>;
