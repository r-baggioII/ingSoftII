import type { LocalidadDTO } from '../dto/LocalidadDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
interface PersonaFormProps {
    initialValue: PersonaDTO;
    localidades: LocalidadDTO[];
    onSubmit: (persona: PersonaDTO) => Promise<void> | void;
    onCancel?: () => void;
    submitLabel?: string;
}
export declare function PersonaForm({ initialValue, localidades, onSubmit, onCancel, submitLabel }: PersonaFormProps): import("react/jsx-runtime").JSX.Element;
export {};
