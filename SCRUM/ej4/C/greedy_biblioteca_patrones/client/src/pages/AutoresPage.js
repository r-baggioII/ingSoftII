import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useMemo, useState } from 'react';
import { createAutor, fetchAutor, fetchAutores, removeAutor, updateAutor } from '../api/autorApi';
import { AutorForm } from '../components/AutorForm';
import { useToast } from '../components/ToastProvider';
const emptyAutor = {
    nombre: '',
    apellido: '',
    biografia: ''
};
export function AutoresPage() {
    const [autores, setAutores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [current, setCurrent] = useState(emptyAutor);
    const { showToast } = useToast();
    const loadAutores = async () => {
        setLoading(true);
        try {
            const data = await fetchAutores();
            setAutores(data);
        }
        catch (error) {
            console.error(error);
            showToast('Error al cargar autores', 'error');
        }
        finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        loadAutores();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);
    useEffect(() => {
        if (!editingId)
            return;
        (async () => {
            try {
                const autor = await fetchAutor(editingId);
                setCurrent(autor);
                setShowForm(true);
            }
            catch (error) {
                console.error(error);
                showToast('No se pudo cargar el autor', 'error');
            }
        })();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [editingId]);
    const handleCreate = async (autor) => {
        try {
            await createAutor(autor);
            showToast('Autor creado');
            setShowForm(false);
            setCurrent(emptyAutor);
            await loadAutores();
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo crear el autor', 'error');
        }
    };
    const handleUpdate = async (autor) => {
        if (!autor.id)
            return;
        try {
            await updateAutor(autor.id, autor);
            showToast('Autor actualizado');
            setEditingId(null);
            setShowForm(false);
            setCurrent(emptyAutor);
            await loadAutores();
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo actualizar el autor', 'error');
        }
    };
    const handleDelete = async (id) => {
        if (!id)
            return;
        if (!window.confirm('¿Eliminar autor?'))
            return;
        try {
            await removeAutor(id);
            showToast('Autor eliminado');
            await loadAutores();
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo eliminar el autor', 'error');
        }
    };
    const isEditing = Boolean(editingId);
    const formValue = useMemo(() => (isEditing ? current : emptyAutor), [current, isEditing]);
    return (_jsxs("section", { children: [_jsxs("div", { className: "actions", style: { marginBottom: '1rem' }, children: [_jsx("button", { onClick: () => {
                            setShowForm((prev) => !prev);
                            setEditingId(null);
                            setCurrent(emptyAutor);
                        }, children: showForm && !isEditing ? 'Cerrar formulario' : 'Nuevo autor' }), isEditing && (_jsx("button", { className: "secondary", onClick: () => {
                            setEditingId(null);
                            setCurrent(emptyAutor);
                            setShowForm(false);
                        }, children: "Cancelar edici\u00F3n" }))] }), (showForm || isEditing) && (_jsx(AutorForm, { initialValue: formValue, onSubmit: isEditing ? handleUpdate : handleCreate, onCancel: () => {
                    setShowForm(false);
                    setEditingId(null);
                    setCurrent(emptyAutor);
                }, submitLabel: isEditing ? 'Actualizar' : 'Crear' })), _jsxs("div", { className: "card", style: { marginTop: '1.5rem' }, children: [loading && _jsx("p", { children: "Cargando autores..." }), !loading && autores.length === 0 && _jsx("div", { className: "empty-state", children: "No hay autores." }), !loading && autores.length > 0 && (_jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "Nombre" }), _jsx("th", { children: "Apellido" }), _jsx("th", { children: "Biograf\u00EDa" }), _jsx("th", { children: "Acciones" })] }) }), _jsx("tbody", { children: autores.map((autor) => (_jsxs("tr", { children: [_jsx("td", { children: autor.nombre }), _jsx("td", { children: autor.apellido }), _jsx("td", { children: autor.biografia }), _jsx("td", { children: _jsxs("div", { className: "table-actions", children: [_jsx("button", { className: "secondary", onClick: () => setEditingId(autor.id), children: "Editar" }), _jsx("button", { className: "danger", onClick: () => handleDelete(autor.id), children: "Eliminar" })] }) })] }, autor.id))) })] }))] })] }));
}
