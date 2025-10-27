import type { AutorDTO } from '../dto/AutorDTO';
interface AutorFormProps {
    initialValue: Partial<AutorDTO>;
    onSubmit: (autor: AutorDTO) => Promise<void> | void;
    onCancel?: () => void;
    submitLabel?: string;
}
export declare function AutorForm({ initialValue, onSubmit, onCancel, submitLabel }: AutorFormProps): import("react/jsx-runtime").JSX.Element;
export {};
