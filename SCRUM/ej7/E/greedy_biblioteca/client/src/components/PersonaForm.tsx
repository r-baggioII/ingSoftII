import { useState } from 'react';
import type { LocalidadDTO } from '../dto/LocalidadDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
import { useHydratedForm } from '../hooks/useHydratedForm';

interface PersonaFormProps {
  initialValue: Partial<PersonaDTO>;
  localidades: LocalidadDTO[];
  onSubmit: (persona: PersonaDTO) => Promise<void> | void;
  onCancel?: () => void;
  submitLabel?: string;
}

const emptyPersona: PersonaDTO = {
  nombre: '',
  apellido: '',
  dni: 0,
  domicilio: {
    calle: '',
    numero: 0,
    localidad: { id: 0, denominacion: '' },
    localidadId: undefined
  }
};

export function PersonaForm({
  initialValue,
  localidades,
  onSubmit,
  onCancel,
  submitLabel = 'Guardar'
}: PersonaFormProps) {
  const { form, setForm, submitting, setSubmitting } = useHydratedForm<PersonaDTO>(emptyPersona, initialValue);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!form.nombre.trim()) newErrors.nombre = 'Nombre obligatorio';
    if (!form.apellido.trim()) newErrors.apellido = 'Apellido obligatorio';
    if (!form.dni || form.dni <= 0) newErrors.dni = 'DNI inválido';
    if (!form.domicilio.calle.trim()) newErrors.calle = 'Calle obligatoria';
    if (!form.domicilio.numero || form.domicilio.numero <= 0) newErrors.numero = 'Número inválido';
    const localidadId = form.domicilio.localidadId ?? form.domicilio.localidad?.id;
    if (!localidadId) newErrors.localidad = 'Seleccione localidad';
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

  const localidadId = form.domicilio.localidadId ?? form.domicilio.localidad?.id ?? '';

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
        <label htmlFor="dni">DNI</label>
        <input
          id="dni"
          type="number"
          value={form.dni}
          onChange={(e) =>
            setForm((prev) => ({ ...prev, dni: Number.parseInt(e.target.value, 10) || 0 }))
          }
        />
        {errors.dni && <small className="error">{errors.dni}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="calle">Calle</label>
        <input
          id="calle"
          value={form.domicilio.calle}
          onChange={(e) =>
            setForm((prev) => ({
              ...prev,
              domicilio: { ...prev.domicilio, calle: e.target.value }
            }))
          }
        />
        {errors.calle && <small className="error">{errors.calle}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="numero">Número</label>
        <input
          id="numero"
          type="number"
          value={form.domicilio.numero}
          onChange={(e) =>
            setForm((prev) => ({
              ...prev,
              domicilio: {
                ...prev.domicilio,
                numero: Number.parseInt(e.target.value, 10) || 0
              }
            }))
          }
        />
        {errors.numero && <small className="error">{errors.numero}</small>}
      </div>

      <div className="form-row">
        <label htmlFor="localidad">Localidad</label>
        <select
          id="localidad"
          value={localidadId}
          onChange={(e) => {
            const selected = localidades.find((l) => l.id === Number(e.target.value));
            if (!selected) return;
            setForm((prev) => ({
              ...prev,
              domicilio: {
                ...prev.domicilio,
                localidad: selected,
                localidadId: selected.id
              }
            }));
          }}
        >
          <option value="">Seleccione...</option>
          {localidades.map((localidad) => (
            <option key={localidad.id} value={localidad.id}>
              {localidad.denominacion}
            </option>
          ))}
        </select>
        {errors.localidad && <small className="error">{errors.localidad}</small>}
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
