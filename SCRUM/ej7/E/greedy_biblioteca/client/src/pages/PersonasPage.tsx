import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPersona, fetchPersonas, removePersona, updatePersona } from '../api/personaApi';
import { fetchLocalidades } from '../api/localidadApi';
import type { LocalidadDTO } from '../dto/LocalidadDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
import type { Page } from '../api/types';
import { PersonaForm } from '../components/PersonaForm';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';
import { useCrudDialog } from '../hooks/useCrudDialog';

const emptyPersona: PersonaDTO = {
  nombre: '',
  apellido: '',
  dni: 0,
  domicilio: {
    calle: '',
    numero: 0,
    localidad: { id: 0, denominacion: '' }
  }
};

export function PersonasPage() {
  const [localidades, setLocalidades] = useState<LocalidadDTO[]>([]);
  const [page, setPage] = useState<Page<PersonaDTO> | null>(null);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<{ apellido?: string; dni?: string }>({});
  const { showToast } = useToast();
  const navigate = useNavigate();
  const { current, isOpen, isEditing, openForCreate, openForEdit, close, setCurrentValue } =
    useCrudDialog<PersonaDTO>(emptyPersona);

  const loadLocalidades = async () => {
    try {
      const data = await fetchLocalidades();
      setLocalidades(data);
    } catch (error) {
      console.error(error);
      showToast('Error al cargar localidades', 'error');
    }
  };

  const loadPersonas = async (pageNumber = 0) => {
    setLoading(true);
    try {
      const dniFilter = filters.dni ? Number(filters.dni) : undefined;
      const data = await fetchPersonas({
        apellido: filters.apellido?.trim() || undefined,
        dni: Number.isNaN(dniFilter) ? undefined : dniFilter,
        page: pageNumber,
        size: 10
      });
      setPage(data);
    } catch (error) {
      console.error(error);
      showToast('Error al cargar personas', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLocalidades();
  }, []);

  useEffect(() => {
    loadPersonas();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.apellido, filters.dni]);

  const handleCreate = async (persona: PersonaDTO) => {
    try {
      await createPersona(persona);
      showToast('Persona creada');
      setCurrentValue(emptyPersona);
      close();
      await loadPersonas(page?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo crear la persona', 'error');
    }
  };

  const handleUpdate = async (persona: PersonaDTO) => {
    if (!persona.id) return;
    try {
      await updatePersona(persona.id, persona);
      showToast('Persona actualizada');
      setCurrentValue(emptyPersona);
      close();
      await loadPersonas(page?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo actualizar la persona', 'error');
    }
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    if (!window.confirm('¿Seguro que desea eliminar la persona?')) return;
    try {
      await removePersona(id);
      showToast('Persona eliminada');
      await loadPersonas(page?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo eliminar la persona', 'error');
    }
  };

  return (
    <section>
      <div className="actions" style={{ marginBottom: '1rem' }}>
        <button
          onClick={() => {
            if (isOpen && !isEditing) {
              close();
            } else {
              openForCreate();
            }
          }}
        >
          {isOpen && !isEditing ? 'Cerrar formulario' : 'Nueva persona'}
        </button>
        {isEditing && (
          <button
            className="secondary"
            onClick={() => {
              setCurrentValue(emptyPersona);
              close();
            }}
          >
            Cancelar edición
          </button>
        )}
      </div>

      {(isOpen || isEditing) && (
        <PersonaForm
          initialValue={current}
          localidades={localidades}
          onSubmit={isEditing ? handleUpdate : handleCreate}
          onCancel={() => {
            setCurrentValue(emptyPersona);
            close();
          }}
          submitLabel={isEditing ? 'Actualizar' : 'Crear'}
        />
      )}

      <div className="card" style={{ marginTop: '1.5rem' }}>
        <div className="filters">
          <div className="form-row">
            <label htmlFor="f-apellido">Apellido</label>
            <input
              id="f-apellido"
              value={filters.apellido ?? ''}
              onChange={(e) => setFilters((prev) => ({ ...prev, apellido: e.target.value }))}
              placeholder="Buscar por apellido"
            />
          </div>
          <div className="form-row">
            <label htmlFor="f-dni">DNI</label>
            <input
              id="f-dni"
              type="number"
              value={filters.dni ?? ''}
              onChange={(e) => setFilters((prev) => ({ ...prev, dni: e.target.value }))}
              placeholder="Buscar por DNI"
            />
          </div>
          <div className="form-row">
            <label>&nbsp;</label>
            <button onClick={() => loadPersonas()} type="button">
              Aplicar
            </button>
          </div>
          <div className="form-row">
            <label>&nbsp;</label>
            <button
              type="button"
              className="secondary"
              onClick={() => {
                setFilters({});
              }}
            >
              Limpiar
            </button>
          </div>
        </div>

        {loading && <p>Cargando personas...</p>}

        {!loading && page && page.content.length === 0 && (
          <div className="empty-state">No hay personas registradas.</div>
        )}

        {!loading && page && page.content.length > 0 && (
          <>
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Apellido</th>
                  <th>DNI</th>
                  <th>Domicilio</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {page.content.map((persona) => (
                  <tr key={persona.id}>
                    <td>{persona.nombre}</td>
                    <td>{persona.apellido}</td>
                    <td>{persona.dni}</td>
                    <td>
                      {persona.domicilio.calle} {persona.domicilio.numero}{' '}
                      <span className="badge">{persona.domicilio.localidad.denominacion}</span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button
                          className="secondary"
                          onClick={() => {
                            openForEdit(persona);
                          }}
                        >
                          Editar
                        </button>
                        <button
                          className="secondary"
                          onClick={() => navigate(`/personas/${persona.id}`)}
                        >
                          Ver
                        </button>
                        <button className="danger" onClick={() => handleDelete(persona.id)}>
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination
              page={page.number}
              totalPages={page.totalPages}
              onChange={(newPage) => {
                loadPersonas(newPage);
              }}
            />
          </>
        )}
      </div>
    </section>
  );
}
