import { useState } from 'react';
import type { AutorDTO } from '../dto/AutorDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
import { useHydratedForm } from '../hooks/useHydratedForm';

interface LibroFormProps {
  initialValue: Partial<LibroDTO>;
  autores: AutorDTO[];
  personas: PersonaDTO[];
  onSubmit: (libro: LibroDTO) => Promise<void> | void;
  onCancel?: () => void;
  submitLabel?: string;
}

const emptyLibro: LibroDTO = {
  titulo: '',
  fecha: '',
  genero: '',
  paginas: 0,
  autor: { nombre: '', apellido: '', biografia: '' },
  persona: { id: 0, nombre: '', apellido: '' }
};

export function LibroForm({
  initialValue,
  autores,
  personas,
  onSubmit,
  onCancel,
  submitLabel = 'Guardar'
}: LibroFormProps) {
  const { form, setForm, submitting, setSubmitting } = useHydratedForm<LibroDTO>(emptyLibro, initialValue);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!form.titulo.trim()) newErrors.titulo = 'Título obligatorio';
    if (!form.fecha) newErrors.fecha = 'Fecha obligatoria';
    if (!form.genero.trim()) newErrors.genero = 'Género obligatorio';
    if (!form.paginas || form.paginas <= 0) newErrors.paginas = 'Páginas inválidas';
    const autorId = form.autorId ?? form.autor?.id;
    if (!autorId) newErrors.autor = 'Seleccione autor';
    const personaId = form.personaId ?? form.persona?.id;
    if (!personaId) newErrors.persona = 'Seleccione persona';
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

  const autorId = form.autorId ?? form.autor?.id ?? '';
  const personaId = form.personaId ?? form.persona?.id ?? '';

  return (
    <form onSubmit={handleSubmit} className="card">
      <div className="form-row">
        <label htmlFor="titulo">Título</label>
        <input
          id="titulo"
          value={form.titulo}
          onChange={(e) => setForm((prev) => ({ ...prev, titulo: e.target.value }))}
        />
        {errors.titulo && <small className="error">{errors.titulo}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="fecha">Fecha</label>
        <input
          id="fecha"
          type="date"
          value={form.fecha}
          onChange={(e) => setForm((prev) => ({ ...prev, fecha: e.target.value }))}
        />
        {errors.fecha && <small className="error">{errors.fecha}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="genero">Género</label>
        <input
          id="genero"
          value={form.genero}
          onChange={(e) => setForm((prev) => ({ ...prev, genero: e.target.value }))}
        />
        {errors.genero && <small className="error">{errors.genero}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="paginas">Páginas</label>
        <input
          id="paginas"
          type="number"
          value={form.paginas}
          onChange={(e) =>
            setForm((prev) => ({
              ...prev,
              paginas: Number.parseInt(e.target.value, 10) || 0
            }))
          }
        />
        {errors.paginas && <small className="error">{errors.paginas}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="autor">Autor</label>
        <select
          id="autor"
          value={autorId}
          onChange={(e) => {
            const selected = autores.find((a) => a.id === Number(e.target.value));
            if (!selected) return;
            setForm((prev) => ({
              ...prev,
              autor: selected,
              autorId: selected.id
            }));
          }}
        >
          <option value="">Seleccione...</option>
          {autores.map((autor) => (
            <option key={autor.id} value={autor.id}>
              {autor.nombre} {autor.apellido}
            </option>
          ))}
        </select>
        {errors.autor && <small className="error">{errors.autor}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="persona">Persona</label>
        <select
          id="persona"
          value={personaId}
          onChange={(e) => {
            const selected = personas.find((p) => p.id === Number(e.target.value));
            if (!selected) return;
            setForm((prev) => ({
              ...prev,
              persona: { id: selected.id!, nombre: selected.nombre, apellido: selected.apellido },
              personaId: selected.id
            }));
          }}
        >
          <option value="">Seleccione...</option>
          {personas.map((persona) => (
            <option key={persona.id} value={persona.id}>
              {persona.nombre} {persona.apellido}
            </option>
          ))}
        </select>
        {errors.persona && <small className="error">{errors.persona}</small>}
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
