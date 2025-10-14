import type { LibroMutationInput } from '../api/libroApi';
import type { AutorDTO } from '../dto/AutorDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
interface LibroFormProps {
    initialValue: LibroDTO;
    autores: AutorDTO[];
    personas: PersonaDTO[];
    onSubmit: (input: LibroMutationInput) => Promise<void> | void;
    onCancel?: () => void;
    submitLabel?: string;
}
export declare function LibroForm({ initialValue, autores, personas, onSubmit, onCancel, submitLabel }: LibroFormProps): import("react/jsx-runtime").JSX.Element;
export {};
