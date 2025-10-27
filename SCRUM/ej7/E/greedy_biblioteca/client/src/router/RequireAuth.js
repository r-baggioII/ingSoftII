import { jsx as _jsx } from "react/jsx-runtime";
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { hasToken } from '../utils/auth';
export function RequireAuth() {
    const location = useLocation();
    if (!hasToken()) {
        return _jsx(Navigate, { to: "/login", state: { from: location }, replace: true });
    }
    return _jsx(Outlet, {});
}
