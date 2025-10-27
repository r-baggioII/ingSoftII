import { useState } from 'react';
import type { AutorDTO } from '../dto/AutorDTO';
import { useHydratedForm } from '../hooks/useHydratedForm';

interface AutorFormProps {
  initialValue: Partial<AutorDTO>;
  onSubmit: (autor: AutorDTO) => Promise<void> | void;
  onCancel?: () => void;
  submitLabel?: string;
}

const emptyAutor: AutorDTO = {
  nombre: '',
  apellido: '',
  biografia: ''
};

export function AutorForm({
  initialValue,
  onSubmit,
  onCancel,
  submitLabel = 'Guardar'
}: AutorFormProps) {
  const { form, setForm, submitting, setSubmitting } = useHydratedForm<AutorDTO>(emptyAutor, initialValue);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!form.nombre.trim()) newErrors.nombre = 'Nombre obligatorio';
    if (!form.apellido.trim()) newErrors.apellido = 'Apellido obligatorio';
    if (!form.biografia.trim()) newErrors.biografia = 'Biografía obligatoria';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!validate()) return;
    setSubmitting(true);
    try {
      await onSubmit(form);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card">
      <div className="form-row">
        <label htmlFor="nombre">Nombre</label>
        <input
          id="nombre"
          value={form.nombre}
          onChange={(e) => setForm((prev) => ({ ...prev, nombre: e.target.value }))}
        />
        {errors.nombre && <small className="error">{errors.nombre}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="apellido">Apellido</label>
        <input
          id="apellido"
          value={form.apellido}
          onChange={(e) => setForm((prev) => ({ ...prev, apellido: e.target.value }))}
        />
        {errors.apellido && <small className="error">{errors.apellido}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="biografia">Biografía</label>
        <textarea
          id="biografia"
          rows={4}
          value={form.biografia}
          onChange={(e) => setForm((prev) => ({ ...prev, biografia: e.target.value }))}
        />
        {errors.biografia && <small className="error">{errors.biografia}</small>}
      </div>

      <div className="actions">
        <button type="submit" disabled={submitting}>
          {submitLabel}
        </button>
        {onCancel && (
          <button type="button" className="secondary" onClick={onCancel}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}
