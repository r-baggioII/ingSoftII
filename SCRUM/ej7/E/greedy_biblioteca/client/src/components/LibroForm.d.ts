import type { AutorDTO } from '../dto/AutorDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
interface LibroFormProps {
    initialValue: Partial<LibroDTO>;
    autores: AutorDTO[];
    personas: PersonaDTO[];
    onSubmit: (libro: LibroDTO) => Promise<void> | void;
    onCancel?: () => void;
    submitLabel?: string;
}
export declare function LibroForm({ initialValue, autores, personas, onSubmit, onCancel, submitLabel }: LibroFormProps): import("react/jsx-runtime").JSX.Element;
export {};
