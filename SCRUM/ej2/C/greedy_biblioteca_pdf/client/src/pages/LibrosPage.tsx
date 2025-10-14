import { useEffect, useMemo, useState } from 'react';
import { createLibro, fetchLibro, fetchLibros, removeLibro, updateLibro } from '../api/libroApi';
import { fetchAutores } from '../api/autorApi';
import { fetchPersonas } from '../api/personaApi';
import type { AutorDTO } from '../dto/AutorDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { PersonaDTO } from '../dto/PersonaDTO';
import type { Page } from '../api/types';
import { LibroForm } from '../components/LibroForm';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';

const emptyLibro: LibroDTO = {
  titulo: '',
  fecha: '',
  genero: '',
  paginas: 0,
  autor: { nombre: '', apellido: '', biografia: '' },
  persona: { id: 0, nombre: '', apellido: '' }
};

export function LibrosPage() {
  const [libros, setLibros] = useState<Page<LibroDTO> | null>(null);
  const [autores, setAutores] = useState<AutorDTO[]>([]);
  const [personas, setPersonas] = useState<PersonaDTO[]>([]);
  const [filters, setFilters] = useState<{ autorId?: string; personaId?: string; genero?: string }>({});
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [current, setCurrent] = useState<LibroDTO>(emptyLibro);
  const { showToast } = useToast();

  const loadLibros = async (page = 0) => {
    setLoading(true);
    try {
      const autorId =
        filters.autorId && filters.autorId !== '' ? Number.parseInt(filters.autorId, 10) : undefined;
      const personaId =
        filters.personaId && filters.personaId !== ''
          ? Number.parseInt(filters.personaId, 10)
          : undefined;
      const data = await fetchLibros({
        autorId: Number.isNaN(autorId) ? undefined : autorId,
        personaId: Number.isNaN(personaId) ? undefined : personaId,
        genero: filters.genero?.trim() || undefined,
        page,
        size: 10
      });
      setLibros(data);
    } catch (error) {
      console.error(error);
      showToast('Error al cargar libros', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadAuxData = async () => {
    try {
      const [autoresData, personasPage] = await Promise.all([
        fetchAutores(),
        fetchPersonas({ page: 0, size: 100 })
      ]);
      setAutores(autoresData);
      setPersonas(personasPage.content);
    } catch (error) {
      console.error(error);
      showToast('Error al cargar catálogos', 'error');
    }
  };

  useEffect(() => {
    loadAuxData();
  }, []);

  useEffect(() => {
    loadLibros();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.autorId, filters.personaId, filters.genero]);

  useEffect(() => {
    if (!editingId) return;
    (async () => {
      try {
        const libro = await fetchLibro(editingId);
        setCurrent({
          ...libro,
          autorId: libro.autor.id,
          personaId: libro.persona.id
        });
        setShowForm(true);
      } catch (error) {
        console.error(error);
        showToast('No se pudo cargar el libro', 'error');
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [editingId]);

  const handleCreate = async (libro: LibroDTO) => {
    try {
      await createLibro(libro);
      showToast('Libro creado');
      setShowForm(false);
      setCurrent(emptyLibro);
      await loadLibros(libros?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo crear el libro', 'error');
    }
  };

  const handleUpdate = async (libro: LibroDTO) => {
    if (!libro.id) return;
    try {
      await updateLibro(libro.id, libro);
      showToast('Libro actualizado');
      setShowForm(false);
      setEditingId(null);
      setCurrent(emptyLibro);
      await loadLibros(libros?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo actualizar el libro', 'error');
    }
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    if (!window.confirm('¿Eliminar libro?')) return;
    try {
      await removeLibro(id);
      showToast('Libro eliminado');
      await loadLibros(libros?.number ?? 0);
    } catch (error) {
      console.error(error);
      showToast('No se pudo eliminar el libro', 'error');
    }
  };

  const isEditing = Boolean(editingId);
  const formValue = useMemo(() => (isEditing ? current : emptyLibro), [current, isEditing]);

  return (
    <section>
      <div className="actions" style={{ marginBottom: '1rem' }}>
        <button
          onClick={() => {
            setShowForm((prev) => !prev);
            setEditingId(null);
            setCurrent(emptyLibro);
          }}
        >
          {showForm && !isEditing ? 'Cerrar formulario' : 'Nuevo libro'}
        </button>
        {isEditing && (
          <button
            className="secondary"
            onClick={() => {
              setEditingId(null);
              setShowForm(false);
              setCurrent(emptyLibro);
            }}
          >
            Cancelar edición
          </button>
        )}
      </div>

      {(showForm || isEditing) && (
        <LibroForm
          initialValue={formValue}
          autores={autores}
          personas={personas}
          onSubmit={isEditing ? handleUpdate : handleCreate}
          onCancel={() => {
            setShowForm(false);
            setEditingId(null);
            setCurrent(emptyLibro);
          }}
          submitLabel={isEditing ? 'Actualizar' : 'Crear'}
        />
      )}

      <div className="card" style={{ marginTop: '1.5rem' }}>
        <div className="filters">
          <div className="form-row">
            <label htmlFor="f-autor">Autor</label>
            <select
              id="f-autor"
              value={filters.autorId ?? ''}
              onChange={(e) => setFilters((prev) => ({ ...prev, autorId: e.target.value }))}
            >
              <option value="">Todos</option>
              {autores.map((autor) => (
                <option key={autor.id} value={autor.id}>
                  {autor.nombre} {autor.apellido}
                </option>
              ))}
            </select>
          </div>
          <div className="form-row">
            <label htmlFor="f-persona">Persona</label>
            <select
              id="f-persona"
              value={filters.personaId ?? ''}
              onChange={(e) => setFilters((prev) => ({ ...prev, personaId: e.target.value }))}
            >
              <option value="">Todas</option>
              {personas.map((persona) => (
                <option key={persona.id} value={persona.id}>
                  {persona.nombre} {persona.apellido}
                </option>
              ))}
            </select>
          </div>
          <div className="form-row">
            <label htmlFor="f-genero">Género</label>
            <input
              id="f-genero"
              value={filters.genero ?? ''}
              onChange={(e) => setFilters((prev) => ({ ...prev, genero: e.target.value }))}
              placeholder="Filtrar por género"
            />
          </div>
          <div className="form-row">
            <label>&nbsp;</label>
            <button type="button" onClick={() => loadLibros()}>
              Aplicar
            </button>
          </div>
        </div>

        {loading && <p>Cargando libros...</p>}
        {!loading && libros && libros.content.length === 0 && (
          <div className="empty-state">No hay libros.</div>
        )}
        {!loading && libros && libros.content.length > 0 && (
          <>
            <table>
              <thead>
                <tr>
                  <th>Título</th>
                  <th>Género</th>
                  <th>Páginas</th>
                  <th>Autor</th>
                  <th>Persona</th>
                  <th>Fecha</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {libros.content.map((libro) => (
                  <tr key={libro.id}>
                    <td>{libro.titulo}</td>
                    <td>{libro.genero}</td>
                    <td>{libro.paginas}</td>
                    <td>
                      {libro.autor.nombre} {libro.autor.apellido}
                    </td>
                    <td>
                      {libro.persona.nombre} {libro.persona.apellido}
                    </td>
                    <td>{libro.fecha}</td>
                    <td>
                      <div className="table-actions">
                        <button className="secondary" onClick={() => setEditingId(libro.id!)}>
                          Editar
                        </button>
                        <button className="danger" onClick={() => handleDelete(libro.id)}>
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination
              page={libros.number}
              totalPages={libros.totalPages}
              onChange={(newPage) => loadLibros(newPage)}
            />
          </>
        )}
      </div>
    </section>
  );
}
