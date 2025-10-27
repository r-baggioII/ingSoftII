import { useEffect, useState } from 'react';
import { createAutor, fetchAutor, fetchAutores, removeAutor, updateAutor } from '../api/autorApi';
import type { AutorDTO } from '../dto/AutorDTO';
import { AutorForm } from '../components/AutorForm';
import { useToast } from '../components/ToastProvider';
import { useCrudDialog } from '../hooks/useCrudDialog';

const emptyAutor: AutorDTO = {
  nombre: '',
  apellido: '',
  biografia: ''
};

export function AutoresPage() {
  const [autores, setAutores] = useState<AutorDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState<number | null>(null);
  const { showToast } = useToast();
  const { current, isOpen, isEditing, openForCreate, openForEdit, close, setCurrentValue } =
    useCrudDialog<AutorDTO>(emptyAutor);

  const loadAutores = async () => {
    setLoading(true);
    try {
      const data = await fetchAutores();
      setAutores(data);
    } catch (error) {
      console.error(error);
      showToast('Error al cargar autores', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAutores();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!editingId) return;
    (async () => {
      try {
        const autor = await fetchAutor(editingId);
        setCurrentValue(autor);
      } catch (error) {
        console.error(error);
        showToast('No se pudo cargar el autor', 'error');
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [editingId]);

  const handleCreate = async (autor: AutorDTO) => {
    try {
      await createAutor(autor);
      showToast('Autor creado');
      setEditingId(null);
      setCurrentValue(emptyAutor);
      close();
      await loadAutores();
    } catch (error) {
      console.error(error);
      showToast('No se pudo crear el autor', 'error');
    }
  };

  const handleUpdate = async (autor: AutorDTO) => {
    if (!autor.id) return;
    try {
      await updateAutor(autor.id, autor);
      showToast('Autor actualizado');
      setEditingId(null);
      setCurrentValue(emptyAutor);
      close();
      await loadAutores();
    } catch (error) {
      console.error(error);
      showToast('No se pudo actualizar el autor', 'error');
    }
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    if (!window.confirm('¿Eliminar autor?')) return;
    try {
      await removeAutor(id);
      showToast('Autor eliminado');
      await loadAutores();
    } catch (error) {
      console.error(error);
      showToast('No se pudo eliminar el autor', 'error');
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
              setEditingId(null);
              setCurrentValue(emptyAutor);
              openForCreate();
            }
          }}
        >
          {isOpen && !isEditing ? 'Cerrar formulario' : 'Nuevo autor'}
        </button>
        {isEditing && (
          <button
            className="secondary"
            onClick={() => {
              setEditingId(null);
              setCurrentValue(emptyAutor);
              close();
            }}
          >
            Cancelar edición
          </button>
        )}
      </div>

      {(isOpen || isEditing) && (
        <AutorForm
          initialValue={current}
          onSubmit={isEditing ? handleUpdate : handleCreate}
          onCancel={() => {
            setEditingId(null);
            setCurrentValue(emptyAutor);
            close();
          }}
          submitLabel={isEditing ? 'Actualizar' : 'Crear'}
        />
      )}

      <div className="card" style={{ marginTop: '1.5rem' }}>
        {loading && <p>Cargando autores...</p>}
        {!loading && autores.length === 0 && <div className="empty-state">No hay autores.</div>}
        {!loading && autores.length > 0 && (
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Biografía</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {autores.map((autor) => (
                <tr key={autor.id}>
                  <td>{autor.nombre}</td>
                  <td>{autor.apellido}</td>
                  <td>{autor.biografia}</td>
                  <td>
                    <div className="table-actions">
                      <button
                        className="secondary"
                        onClick={() => {
                          setEditingId(autor.id!);
                          openForEdit(autor);
                        }}
                      >
                        Editar
                      </button>
                      <button className="danger" onClick={() => handleDelete(autor.id)}>
                        Eliminar
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}
