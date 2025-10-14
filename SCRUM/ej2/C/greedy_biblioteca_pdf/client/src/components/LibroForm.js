import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
const emptyLibro = {
    titulo: '',
    fecha: '',
    genero: '',
    paginas: 0,
    autor: { nombre: '', apellido: '', biografia: '' },
    persona: { id: 0, nombre: '', apellido: '' },
    hasPdf: false
};
export function LibroForm({ initialValue, autores, personas, onSubmit, onCancel, submitLabel = 'Guardar' }) {
    const [form, setForm] = useState({ ...emptyLibro, ...initialValue });
    const [errors, setErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [pdfFile, setPdfFile] = useState(null);
    useEffect(() => {
        setForm({ ...emptyLibro, ...initialValue });
        setPdfFile(null);
    }, [initialValue]);
    const validate = () => {
        const newErrors = {};
        if (!form.titulo.trim())
            newErrors.titulo = 'Título obligatorio';
        if (!form.fecha)
            newErrors.fecha = 'Fecha obligatoria';
        if (!form.genero.trim())
            newErrors.genero = 'Género obligatorio';
        if (!form.paginas || form.paginas <= 0)
            newErrors.paginas = 'Páginas inválidas';
        const autorId = form.autorId ?? form.autor?.id;
        if (!autorId)
            newErrors.autor = 'Seleccione autor';
        const personaId = form.personaId ?? form.persona?.id;
        if (!personaId)
            newErrors.persona = 'Seleccione persona';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!validate())
            return;
        setSubmitting(true);
        try {
            await onSubmit({ libro: form, pdfFile: pdfFile ?? undefined });
            setPdfFile(null);
        }
        finally {
            setSubmitting(false);
        }
    };
    const autorId = form.autorId ?? form.autor?.id ?? '';
    const personaId = form.personaId ?? form.persona?.id ?? '';
    return (_jsxs("form", { onSubmit: handleSubmit, className: "card", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "titulo", children: "T\u00EDtulo" }), _jsx("input", { id: "titulo", value: form.titulo, onChange: (e) => setForm((prev) => ({ ...prev, titulo: e.target.value })) }), errors.titulo && _jsx("small", { className: "error", children: errors.titulo })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "fecha", children: "Fecha" }), _jsx("input", { id: "fecha", type: "date", value: form.fecha, onChange: (e) => setForm((prev) => ({ ...prev, fecha: e.target.value })) }), errors.fecha && _jsx("small", { className: "error", children: errors.fecha })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "genero", children: "G\u00E9nero" }), _jsx("input", { id: "genero", value: form.genero, onChange: (e) => setForm((prev) => ({ ...prev, genero: e.target.value })) }), errors.genero && _jsx("small", { className: "error", children: errors.genero })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "paginas", children: "P\u00E1ginas" }), _jsx("input", { id: "paginas", type: "number", value: form.paginas, onChange: (e) => setForm((prev) => ({
                            ...prev,
                            paginas: Number.parseInt(e.target.value, 10) || 0
                        })) }), errors.paginas && _jsx("small", { className: "error", children: errors.paginas })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "autor", children: "Autor" }), _jsxs("select", { id: "autor", value: autorId, onChange: (e) => {
                            const selected = autores.find((a) => a.id === Number(e.target.value));
                            if (!selected)
                                return;
                            setForm((prev) => ({
                                ...prev,
                                autor: selected,
                                autorId: selected.id
                            }));
                        }, children: [_jsx("option", { value: "", children: "Seleccione..." }), autores.map((autor) => (_jsxs("option", { value: autor.id, children: [autor.nombre, " ", autor.apellido] }, autor.id)))] }), errors.autor && _jsx("small", { className: "error", children: errors.autor })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "persona", children: "Persona" }), _jsxs("select", { id: "persona", value: personaId, onChange: (e) => {
                            const selected = personas.find((p) => p.id === Number(e.target.value));
                            if (!selected)
                                return;
                            setForm((prev) => ({
                                ...prev,
                                persona: { id: selected.id, nombre: selected.nombre, apellido: selected.apellido },
                                personaId: selected.id
                            }));
                        }, children: [_jsx("option", { value: "", children: "Seleccione..." }), personas.map((persona) => (_jsxs("option", { value: persona.id, children: [persona.nombre, " ", persona.apellido] }, persona.id)))] }), errors.persona && _jsx("small", { className: "error", children: errors.persona })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "pdf", children: "Archivo PDF" }), _jsx("input", { id: "pdf", type: "file", accept: "application/pdf", onChange: (event) => {
                            const file = event.target.files?.[0] ?? null;
                            setPdfFile(file);
                        } }), form.hasPdf && !pdfFile && (_jsx("small", { children: "Este libro ya tiene un PDF cargado. Adjunte uno nuevo para reemplazarlo." })), pdfFile && _jsx("small", { children: pdfFile.name })] }), _jsxs("div", { className: "actions", children: [_jsx("button", { type: "submit", disabled: submitting, children: submitLabel }), onCancel && (_jsx("button", { type: "button", className: "secondary", onClick: onCancel, children: "Cancelar" }))] })] }));
}
