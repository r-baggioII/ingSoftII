import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Navigate, Route, Routes } from 'react-router-dom';
import { PersonasPage } from '../pages/PersonasPage';
import { PersonaDetailPage } from '../pages/PersonaDetailPage';
import { AutoresPage } from '../pages/AutoresPage';
import { LibrosPage } from '../pages/LibrosPage';
import { LoginPage } from '../pages/LoginPage';
import { RequireAuth } from './RequireAuth';
export function AppRoutes() {
    return (_jsxs(Routes, { children: [_jsx(Route, { path: "/login", element: _jsx(LoginPage, {}) }), _jsxs(Route, { element: _jsx(RequireAuth, {}), children: [_jsx(Route, { path: "/", element: _jsx(Navigate, { to: "/personas", replace: true }) }), _jsx(Route, { path: "/personas", element: _jsx(PersonasPage, {}) }), _jsx(Route, { path: "/personas/:id", element: _jsx(PersonaDetailPage, {}) }), _jsx(Route, { path: "/autores", element: _jsx(AutoresPage, {}) }), _jsx(Route, { path: "/libros", element: _jsx(LibrosPage, {}) })] }), _jsx(Route, { path: "*", element: _jsx(Navigate, { to: "/personas", replace: true }) })] }));
}
