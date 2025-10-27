import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { hasToken } from '../utils/auth';

export function RequireAuth() {
  const location = useLocation();
  if (!hasToken()) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return <Outlet />;
}
