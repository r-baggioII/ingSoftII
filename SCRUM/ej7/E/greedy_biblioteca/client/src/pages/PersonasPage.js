import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPersona, fetchPersonas, removePersona, updatePersona } from '../api/personaApi';
import { fetchLocalidades } from '../api/localidadApi';
import { PersonaForm } from '../components/PersonaForm';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';
import { useCrudDialog } from '../hooks/useCrudDialog';
const emptyPersona = {
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
    const [localidades, setLocalidades] = useState([]);
    const [page, setPage] = useState(null);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({});
    const { showToast } = useToast();
    const navigate = useNavigate();
    const { current, isOpen, isEditing, openForCreate, openForEdit, close, setCurrentValue } = useCrudDialog(emptyPersona);
    const loadLocalidades = async () => {
        try {
            const data = await fetchLocalidades();
            setLocalidades(data);
        }
        catch (error) {
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
        }
        catch (error) {
            console.error(error);
            showToast('Error al cargar personas', 'error');
        }
        finally {
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
    const handleCreate = async (persona) => {
        try {
            await createPersona(persona);
            showToast('Persona creada');
            setCurrentValue(emptyPersona);
            close();
            await loadPersonas(page?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo crear la persona', 'error');
        }
    };
    const handleUpdate = async (persona) => {
        if (!persona.id)
            return;
        try {
            await updatePersona(persona.id, persona);
            showToast('Persona actualizada');
            setCurrentValue(emptyPersona);
            close();
            await loadPersonas(page?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo actualizar la persona', 'error');
        }
    };
    const handleDelete = async (id) => {
        if (!id)
            return;
        if (!window.confirm('¿Seguro que desea eliminar la persona?'))
            return;
        try {
            await removePersona(id);
            showToast('Persona eliminada');
            await loadPersonas(page?.number ?? 0);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo eliminar la persona', 'error');
        }
    };
    return (_jsxs("section", { children: [_jsxs("div", { className: "actions", style: { marginBottom: '1rem' }, children: [_jsx("button", { onClick: () => {
                            if (isOpen && !isEditing) {
                                close();
                            }
                            else {
                                openForCreate();
                            }
                        }, children: isOpen && !isEditing ? 'Cerrar formulario' : 'Nueva persona' }), isEditing && (_jsx("button", { className: "secondary", onClick: () => {
                            setCurrentValue(emptyPersona);
                            close();
                        }, children: "Cancelar edici\u00F3n" }))] }), (isOpen || isEditing) && (_jsx(PersonaForm, { initialValue: current, localidades: localidades, onSubmit: isEditing ? handleUpdate : handleCreate, onCancel: () => {
                    setCurrentValue(emptyPersona);
                    close();
                }, submitLabel: isEditing ? 'Actualizar' : 'Crear' })), _jsxs("div", { className: "card", style: { marginTop: '1.5rem' }, children: [_jsxs("div", { className: "filters", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "f-apellido", children: "Apellido" }), _jsx("input", { id: "f-apellido", value: filters.apellido ?? '', onChange: (e) => setFilters((prev) => ({ ...prev, apellido: e.target.value })), placeholder: "Buscar por apellido" })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "f-dni", children: "DNI" }), _jsx("input", { id: "f-dni", type: "number", value: filters.dni ?? '', onChange: (e) => setFilters((prev) => ({ ...prev, dni: e.target.value })), placeholder: "Buscar por DNI" })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { children: "\u00A0" }), _jsx("button", { onClick: () => loadPersonas(), type: "button", children: "Aplicar" })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { children: "\u00A0" }), _jsx("button", { type: "button", className: "secondary", onClick: () => {
                                            setFilters({});
                                        }, children: "Limpiar" })] })] }), loading && _jsx("p", { children: "Cargando personas..." }), !loading && page && page.content.length === 0 && (_jsx("div", { className: "empty-state", children: "No hay personas registradas." })), !loading && page && page.content.length > 0 && (_jsxs(_Fragment, { children: [_jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "Nombre" }), _jsx("th", { children: "Apellido" }), _jsx("th", { children: "DNI" }), _jsx("th", { children: "Domicilio" }), _jsx("th", { children: "Acciones" })] }) }), _jsx("tbody", { children: page.content.map((persona) => (_jsxs("tr", { children: [_jsx("td", { children: persona.nombre }), _jsx("td", { children: persona.apellido }), _jsx("td", { children: persona.dni }), _jsxs("td", { children: [persona.domicilio.calle, " ", persona.domicilio.numero, ' ', _jsx("span", { className: "badge", children: persona.domicilio.localidad.denominacion })] }), _jsx("td", { children: _jsxs("div", { className: "table-actions", children: [_jsx("button", { className: "secondary", onClick: () => {
                                                                    openForEdit(persona);
                                                                }, children: "Editar" }), _jsx("button", { className: "secondary", onClick: () => navigate(`/personas/${persona.id}`), children: "Ver" }), _jsx("button", { className: "danger", onClick: () => handleDelete(persona.id), children: "Eliminar" })] }) })] }, persona.id))) })] }), _jsx(Pagination, { page: page.number, totalPages: page.totalPages, onChange: (newPage) => {
                                    loadPersonas(newPage);
                                } })] }))] })] }));
}
