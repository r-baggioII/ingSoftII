import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { useHydratedForm } from '../hooks/useHydratedForm';
const emptyAutor = {
    nombre: '',
    apellido: '',
    biografia: ''
};
export function AutorForm({ initialValue, onSubmit, onCancel, submitLabel = 'Guardar' }) {
    const { form, setForm, submitting, setSubmitting } = useHydratedForm(emptyAutor, initialValue);
    const [errors, setErrors] = useState({});
    const validate = () => {
        const newErrors = {};
        if (!form.nombre.trim())
            newErrors.nombre = 'Nombre obligatorio';
        if (!form.apellido.trim())
            newErrors.apellido = 'Apellido obligatorio';
        if (!form.biografia.trim())
            newErrors.biografia = 'Biografía obligatoria';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!validate())
            return;
        setSubmitting(true);
        try {
            await onSubmit(form);
        }
        finally {
            setSubmitting(false);
        }
    };
    return (_jsxs("form", { onSubmit: handleSubmit, className: "card", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "nombre", children: "Nombre" }), _jsx("input", { id: "nombre", value: form.nombre, onChange: (e) => setForm((prev) => ({ ...prev, nombre: e.target.value })) }), errors.nombre && _jsx("small", { className: "error", children: errors.nombre })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "apellido", children: "Apellido" }), _jsx("input", { id: "apellido", value: form.apellido, onChange: (e) => setForm((prev) => ({ ...prev, apellido: e.target.value })) }), errors.apellido && _jsx("small", { className: "error", children: errors.apellido })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "biografia", children: "Biograf\u00EDa" }), _jsx("textarea", { id: "biografia", rows: 4, value: form.biografia, onChange: (e) => setForm((prev) => ({ ...prev, biografia: e.target.value })) }), errors.biografia && _jsx("small", { className: "error", children: errors.biografia })] }), _jsxs("div", { className: "actions", children: [_jsx("button", { type: "submit", disabled: submitting, children: submitLabel }), onCancel && (_jsx("button", { type: "button", className: "secondary", onClick: onCancel, children: "Cancelar" }))] })] }));
}
