import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { AppRoutes } from './router';
import { clearToken, hasToken } from './utils/auth';
export default function App() {
    const navigate = useNavigate();
    const location = useLocation();
    const isAuthenticated = hasToken();
    const handleLogout = () => {
        clearToken();
        navigate('/login', { replace: true });
    };
    return (_jsxs("div", { className: "app-layout", children: [_jsxs("header", { className: "app-header", children: [_jsx("h1", { children: "Biblioteca" }), isAuthenticated ? (_jsxs("nav", { className: "nav-links", children: [_jsx(NavLink, { to: "/personas", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Personas" }), _jsx(NavLink, { to: "/autores", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Autores" }), _jsx(NavLink, { to: "/libros", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Libros" }), _jsx("button", { type: "button", className: "secondary", onClick: handleLogout, children: "Cerrar sesi\u00F3n" })] })) : (location.pathname !== '/login' && (_jsx("nav", { className: "nav-links", children: _jsx(NavLink, { to: "/login", className: "nav-link", children: "Ingresar" }) })))] }), _jsx("main", { className: "app-content", children: _jsx(AppRoutes, {}) })] }));
}
