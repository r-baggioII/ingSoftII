import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { login } from '../api/authApi';
import { setToken } from '../utils/auth';
import { useToast } from '../components/ToastProvider';
export function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const { showToast } = useToast();
    const navigate = useNavigate();
    const location = useLocation();
    const handleSubmit = async (event) => {
        event.preventDefault();
        setLoading(true);
        try {
            const response = await login({ username, password });
            setToken(response.access_token);
            showToast('Sesión iniciada');
            const redirectTo = location.state?.from?.pathname ?? '/personas';
            navigate(redirectTo, { replace: true });
        }
        catch (error) {
            console.error(error);
            showToast('Credenciales inválidas', 'error');
        }
        finally {
            setLoading(false);
        }
    };
    return (_jsx("section", { className: "login-page", children: _jsxs("div", { className: "card", style: { maxWidth: '420px', margin: '4rem auto', padding: '2rem' }, children: [_jsx("h2", { style: { textAlign: 'center', marginBottom: '1.5rem' }, children: "Iniciar sesi\u00F3n" }), _jsxs("form", { onSubmit: handleSubmit, className: "form", children: [_jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "username", children: "Usuario" }), _jsx("input", { id: "username", value: username, onChange: (e) => setUsername(e.target.value), autoComplete: "username", required: true })] }), _jsxs("div", { className: "form-row", children: [_jsx("label", { htmlFor: "password", children: "Contrase\u00F1a" }), _jsx("input", { id: "password", type: "password", value: password, onChange: (e) => setPassword(e.target.value), autoComplete: "current-password", required: true })] }), _jsx("div", { className: "actions", style: { justifyContent: 'flex-end' }, children: _jsx("button", { type: "submit", disabled: loading, children: loading ? 'Ingresando...' : 'Ingresar' }) })] })] }) }));
}
