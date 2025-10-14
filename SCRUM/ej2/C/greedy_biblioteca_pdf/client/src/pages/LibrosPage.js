import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { useEffect, useMemo, useState } from 'react';
import { createLibro, fetchLibro, fetchLibros, removeLibro, updateLibro } from '../api/libroApi';
import { fetchAutores } from '../api/autorApi';
import { fetchPersonas } from '../api/personaApi';
import { LibroForm } from '../components/LibroForm';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';
const emptyLibro = {
    titulo: '',
    fecha: '',
    genero: '',
    paginas: 0,
    autor: { nombre: '', apellido: '', biografia: '' },
    persona: { id: 0, nombre: '', apellido: '' },
    hasPdf: false
};
export function LibrosPage() {
    const [libros, setLibros] = useState(null);
    const [autores, setAutores] = useState([]);
    const [personas, setPersonas] = useState([]);
    const [filters, setFilters] = useState({});
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [current, setCurrent] = useState(emptyLibro);
    const { showToast } = useToast();
    const loadLibros = async (page = 0) => {
        setLoading(true);
        try {
            const autorId = filters.autorId && filters.autorId !== '' ? Number.parseInt(filters.autorId, 10) : undefined;
            const personaId = filters.personaId && filters.personaId !== ''
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
        }
        catch (error) {
            console.error(error);
            showToast('Error al cargar libros', 'error');
        }
        finally {
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
        }
        catch (error) {
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
        if (!editingId)
            return;
        (async () => {
            try {
                const libro = await fetchLibro(editingId);
                setCurrent({
                    ...libro,
                    autorId: libro.autorId ?? libro.autor?.id,
                    personaId: libro.personaId ?? libro.persona?.id
                });
                setShowForm(true);
            }
            catch (error) {
                console.error(error);
                showToast('No se pudo cargar el libro', 'error');
            }
        })();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [editingId]);
    const handleCreate = async (input) => {
        try {
            await createLibro(input);
            showToast('Libro creado');
            setShowForm(false);
            setCurrent(emptyLibro);
            await loadLibros(libros?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo crear el libro', 'error');
        }
    };
    const handleUpdate = async (input) => {
        const libroId = input.libro.id;
        if (!libroId)
            return;
        try {
            await updateLibro(libroId, input);
            showToast('Libro actualizado');
            setShowForm(false);
            setEditingId(null);
            setCurrent(emptyLibro);
            await loadLibros(libros?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo actualizar el libro', 'error');
        }
    };
    const handleDelete = async (id) => {
        if (!id)
            return;
        if (!window.confirm('¿Eliminar libro?'))
            return;
        try {
            await removeLibro(id);
            showToast('Libro eliminado');
            await loadLibros(libros?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo eliminar el libro', 'error');
        }
    };
    const isEditing = Boolean(editingId);
    const formValue = useMemo(() => (isEditing ? current : emptyLibro), [current, isEditing]);
    return (_jsxs("section", { children: [_jsxs("div", { className: "actions", style: { marginBottom: '1rem' }, children: [_jsx("button", { onClick: () => {
                            setShowForm((prev) => !prev);
                            setEditingId(null);
                            setCurrent(emptyLibro);
                        }, children: showForm && !isEditing ? 'Cerrar formulario' : 'Nuevo libro' }), isEditing && (_jsx("button", { className: "secondary", onClick: () => {
                            setEditingId(null);
                            setShowForm(false);
                            setCurrent(emptyLibro);
                        }, children: "Cancelar edici\u00F3n" }))] }), (showForm || isEditing) && (_jsx(LibroForm, { initialValue: formValue, autores: autores, personas: personas, onSubmit: isEditing ? handleUpdate : handleCreate, onCancel: () => {
                    setShowForm(false);
                    setEditingId(null);
                    setCurrent(emptyLibro);
                }, submitLabel: isEditing ? 'Actualizar' : 'Crear' })), _jsxs("div", { className: "card", style: { marginTop: '1.5rem' }, children: [_jsxs("div", { className: "filters", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "f-autor", children: "Autor" }), _jsxs("select", { id: "f-autor", value: filters.autorId ?? '', onChange: (e) => setFilters((prev) => ({ ...prev, autorId: e.target.value })), children: [_jsx("option", { value: "", children: "Todos" }), autores.map((autor) => (_jsxs("option", { value: autor.id, children: [autor.nombre, " ", autor.apellido] }, autor.id)))] })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "f-persona", children: "Persona" }), _jsxs("select", { id: "f-persona", value: filters.personaId ?? '', onChange: (e) => setFilters((prev) => ({ ...prev, personaId: e.target.value })), children: [_jsx("option", { value: "", children: "Todas" }), personas.map((persona) => (_jsxs("option", { value: persona.id, children: [persona.nombre, " ", persona.apellido] }, persona.id)))] })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "f-genero", children: "G\u00E9nero" }), _jsx("input", { id: "f-genero", value: filters.genero ?? '', onChange: (e) => setFilters((prev) => ({ ...prev, genero: e.target.value })), placeholder: "Filtrar por g\u00E9nero" })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { children: "\u00A0" }), _jsx("button", { type: "button", onClick: () => loadLibros(), children: "Aplicar" })] })] }), loading && _jsx("p", { children: "Cargando libros..." }), !loading && libros && libros.content.length === 0 && (_jsx("div", { className: "empty-state", children: "No hay libros." })), !loading && libros && libros.content.length > 0 && (_jsxs(_Fragment, { children: [_jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "T\u00EDtulo" }), _jsx("th", { children: "G\u00E9nero" }), _jsx("th", { children: "P\u00E1ginas" }), _jsx("th", { children: "Autor" }), _jsx("th", { children: "Persona" }), _jsx("th", { children: "Fecha" }), _jsx("th", { children: "PDF" }), _jsx("th", { children: "Acciones" })] }) }), _jsx("tbody", { children: libros.content.map((libro) => (_jsxs("tr", { children: [_jsx("td", { children: libro.titulo }), _jsx("td", { children: libro.genero }), _jsx("td", { children: libro.paginas }), _jsx("td", { children: libro.autor ? (_jsxs(_Fragment, { children: [libro.autor.nombre, " ", libro.autor.apellido] })) : ('-') }), _jsx("td", { children: libro.persona ? (_jsxs(_Fragment, { children: [libro.persona.nombre, " ", libro.persona.apellido] })) : ('-') }), _jsx("td", { children: libro.fecha }), _jsx("td", { children: libro.hasPdf && libro.id ? (_jsx("a", { href: `http://localhost:8080/api/libros/${libro.id}/pdf`, target: "_blank", rel: "noopener noreferrer", children: "Ver PDF" })) : (_jsx("span", { style: { opacity: 0.6 }, children: "Sin PDF" })) }), _jsx("td", { children: _jsxs("div", { className: "table-actions", children: [_jsx("button", { className: "secondary", onClick: () => setEditingId(libro.id), children: "Editar" }), _jsx("button", { className: "danger", onClick: () => handleDelete(libro.id), children: "Eliminar" })] }) })] }, libro.id))) })] }), _jsx(Pagination, { page: libros.number, totalPages: libros.totalPages, onChange: (newPage) => loadLibros(newPage) })] }))] })] }));
}
