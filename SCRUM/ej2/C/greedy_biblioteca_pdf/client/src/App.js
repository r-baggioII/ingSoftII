import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { NavLink } from 'react-router-dom';
import { AppRoutes } from './router';
export default function App() {
    return (_jsxs("div", { className: "app-layout", children: [_jsxs("header", { className: "app-header", children: [_jsx("h1", { children: "Biblioteca" }), _jsxs("nav", { className: "nav-links", children: [_jsx(NavLink, { to: "/personas", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Personas" }), _jsx(NavLink, { to: "/autores", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Autores" }), _jsx(NavLink, { to: "/libros", className: ({ isActive }) => `nav-link${isActive ? ' active' : ''}`, children: "Libros" })] })] }), _jsx("main", { className: "app-content", children: _jsx(AppRoutes, {}) })] }));
}
