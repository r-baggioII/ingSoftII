import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { fetchPersona, fetchPersonaLibros } from '../api/personaApi';
import { Pagination } from '../components/Pagination';
import { useToast } from '../components/ToastProvider';
export function PersonaDetailPage() {
    const { id } = useParams();
    const personaId = Number(id);
    const [persona, setPersona] = useState(null);
    const [libros, setLibros] = useState(null);
    const [loading, setLoading] = useState(true);
    const { showToast } = useToast();
    const navigate = useNavigate();
    const loadPersona = async () => {
        if (!personaId)
            return;
        setLoading(true);
        try {
            const data = await fetchPersona(personaId);
            setPersona(data);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudo cargar la persona', 'error');
        }
        finally {
            setLoading(false);
        }
    };
    const loadLibros = async (page = 0) => {
        if (!personaId)
            return;
        try {
            const data = await fetchPersonaLibros(personaId, { page, size: 10 });
            setLibros(data);
        }
        catch (error) {
            console.error(error);
            showToast('No se pudieron cargar los libros', 'error');
        }
    };
    useEffect(() => {
        if (!Number.isFinite(personaId))
            return;
        loadPersona();
        loadLibros();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [personaId]);
    if (!personaId) {
        return _jsx("p", { children: "ID inv\u00E1lido." });
    }
    return (_jsxs("section", { children: [_jsx("div", { className: "actions", style: { marginBottom: '1rem' }, children: _jsx("button", { className: "secondary", onClick: () => navigate(-1), children: "Volver" }) }), loading && _jsx("p", { children: "Cargando persona..." }), persona && (_jsxs("div", { className: "card", style: { marginBottom: '1.5rem' }, children: [_jsxs("h2", { children: [persona.nombre, " ", persona.apellido] }), _jsxs("p", { children: ["DNI: ", persona.dni] }), _jsxs("p", { children: ["Domicilio: ", persona.domicilio.calle, " ", persona.domicilio.numero, ",", ' ', persona.domicilio.localidad.denominacion] })] })), _jsxs("div", { className: "card", children: [_jsx("h3", { children: "Libros asociados" }), !libros && _jsx("p", { children: "Cargando libros..." }), libros && libros.content.length === 0 && (_jsx("div", { className: "empty-state", children: "La persona no tiene libros asociados." })), libros && libros.content.length > 0 && (_jsxs(_Fragment, { children: [_jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "T\u00EDtulo" }), _jsx("th", { children: "G\u00E9nero" }), _jsx("th", { children: "P\u00E1ginas" }), _jsx("th", { children: "Autor" }), _jsx("th", { children: "Fecha" })] }) }), _jsx("tbody", { children: libros.content.map((libro) => (_jsxs("tr", { children: [_jsx("td", { children: libro.titulo }), _jsx("td", { children: libro.genero }), _jsx("td", { children: libro.paginas }), _jsxs("td", { children: [libro.autor.nombre, " ", libro.autor.apellido] }), _jsx("td", { children: libro.fecha })] }, libro.id))) })] }), _jsx(Pagination, { page: libros.number, totalPages: libros.totalPages, onChange: (newPage) => loadLibros(newPage) })] }))] })] }));
}
