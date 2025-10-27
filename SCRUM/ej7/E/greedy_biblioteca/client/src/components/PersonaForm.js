import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { useHydratedForm } from '../hooks/useHydratedForm';
const emptyPersona = {
    nombre: '',
    apellido: '',
    dni: 0,
    domicilio: {
        calle: '',
        numero: 0,
        localidad: { id: 0, denominacion: '' },
        localidadId: undefined
    }
};
export function PersonaForm({ initialValue, localidades, onSubmit, onCancel, submitLabel = 'Guardar' }) {
    const { form, setForm, submitting, setSubmitting } = useHydratedForm(emptyPersona, initialValue);
    const [errors, setErrors] = useState({});
    const validate = () => {
        const newErrors = {};
        if (!form.nombre.trim())
            newErrors.nombre = 'Nombre obligatorio';
        if (!form.apellido.trim())
            newErrors.apellido = 'Apellido obligatorio';
        if (!form.dni || form.dni <= 0)
            newErrors.dni = 'DNI inválido';
        if (!form.domicilio.calle.trim())
            newErrors.calle = 'Calle obligatoria';
        if (!form.domicilio.numero || form.domicilio.numero <= 0)
            newErrors.numero = 'Número inválido';
        const localidadId = form.domicilio.localidadId ?? form.domicilio.localidad?.id;
        if (!localidadId)
            newErrors.localidad = 'Seleccione localidad';
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
    const localidadId = form.domicilio.localidadId ?? form.domicilio.localidad?.id ?? '';
    return (_jsxs("form", { onSubmit: handleSubmit, className: "card", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "nombre", children: "Nombre" }), _jsx("input", { id: "nombre", value: form.nombre, onChange: (e) => setForm((prev) => ({ ...prev, nombre: e.target.value })) }), errors.nombre && _jsx("small", { className: "error", children: errors.nombre })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "apellido", children: "Apellido" }), _jsx("input", { id: "apellido", value: form.apellido, onChange: (e) => setForm((prev) => ({ ...prev, apellido: e.target.value })) }), errors.apellido && _jsx("small", { className: "error", children: errors.apellido })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "dni", children: "DNI" }), _jsx("input", { id: "dni", type: "number", value: form.dni, onChange: (e) => setForm((prev) => ({ ...prev, dni: Number.parseInt(e.target.value, 10) || 0 })) }), errors.dni && _jsx("small", { className: "error", children: errors.dni })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "calle", children: "Calle" }), _jsx("input", { id: "calle", value: form.domicilio.calle, onChange: (e) => setForm((prev) => ({
                            ...prev,
                            domicilio: { ...prev.domicilio, calle: e.target.value }
                        })) }), errors.calle && _jsx("small", { className: "error", children: errors.calle })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "numero", children: "N\u00FAmero" }), _jsx("input", { id: "numero", type: "number", value: form.domicilio.numero, onChange: (e) => setForm((prev) => ({
                            ...prev,
                            domicilio: {
                                ...prev.domicilio,
                                numero: Number.parseInt(e.target.value, 10) || 0
                            }
                        })) }), errors.numero && _jsx("small", { className: "error", children: errors.numero })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "localidad", children: "Localidad" }), _jsxs("select", { id: "localidad", value: localidadId, onChange: (e) => {
                            const selected = localidades.find((l) => l.id === Number(e.target.value));
                            if (!selected)
                                return;
                            setForm((prev) => ({
                                ...prev,
                                domicilio: {
                                    ...prev.domicilio,
                                    localidad: selected,
                                    localidadId: selected.id
                                }
                            }));
                        }, children: [_jsx("option", { value: "", children: "Seleccione..." }), localidades.map((localidad) => (_jsx("option", { value: localidad.id, children: localidad.denominacion }, localidad.id)))] }), errors.localidad && _jsx("small", { className: "error", children: errors.localidad })] }), _jsxs("div", { className: "actions", children: [_jsx("button", { type: "submit", disabled: submitting, children: submitLabel }), onCancel && (_jsx("button", { type: "button", className: "secondary", onClick: onCancel, children: "Cancelar" }))] })] }));
}
