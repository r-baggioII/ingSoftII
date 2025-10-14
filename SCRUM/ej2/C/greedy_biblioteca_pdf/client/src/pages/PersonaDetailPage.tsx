import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { fetchPersona, fetchPersonaLibros } from '../api/personaApi';
import type { PersonaDTO } from '../dto/PersonaDTO';
import type { LibroDTO } from '../dto/LibroDTO';
import type { Page } from '../api/types';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';

export function PersonaDetailPage() {
  const { id } = useParams<{ id: string }>();
  const personaId = Number(id);
  const [persona, setPersona] = useState<PersonaDTO | null>(null);
  const [libros, setLibros] = useState<Page<LibroDTO> | null>(null);
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();
  const navigate = useNavigate();

  const loadPersona = async () => {
    if (!personaId) return;
    setLoading(true);
    try {
      const data = await fetchPersona(personaId);
      setPersona(data);
    } catch (error) {
      console.error(error);
      showToast('No se pudo cargar la persona', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadLibros = async (page = 0) => {
    if (!personaId) return;
    try {
      const data = await fetchPersonaLibros(personaId, { page, size: 10 });
      setLibros(data);
    } catch (error) {
      console.error(error);
      showToast('No se pudieron cargar los libros', 'error');
    }
  };

  useEffect(() => {
    if (!Number.isFinite(personaId)) return;
    loadPersona();
    loadLibros();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [personaId]);

  if (!personaId) {
    return <p>ID inválido.</p>;
  }

  return (
    <section>
      <div className="actions" style={{ marginBottom: '1rem' }}>
        <button className="secondary" onClick={() => navigate(-1)}>
          Volver
        </button>
      </div>

      {loading && <p>Cargando persona...</p>}

      {persona && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h2>
            {persona.nombre} {persona.apellido}
          </h2>
          <p>DNI: {persona.dni}</p>
          <p>
            Domicilio: {persona.domicilio.calle} {persona.domicilio.numero},{' '}
            {persona.domicilio.localidad.denominacion}
          </p>
        </div>
      )}

      <div className="card">
        <h3>Libros asociados</h3>
        {!libros && <p>Cargando libros...</p>}
        {libros && libros.content.length === 0 && (
          <div className="empty-state">La persona no tiene libros asociados.</div>
        )}
        {libros && libros.content.length > 0 && (
          <>
            <table>
              <thead>
                <tr>
                  <th>Título</th>
                  <th>Género</th>
                  <th>Páginas</th>
                  <th>Autor</th>
                  <th>Fecha</th>
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
                    <td>{libro.fecha}</td>
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
