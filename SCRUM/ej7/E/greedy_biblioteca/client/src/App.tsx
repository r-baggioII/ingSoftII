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

  return (
    <div className="app-layout">
      <header className="app-header">
        <h1>Biblioteca</h1>
        {isAuthenticated ? (
          <nav className="nav-links">
            <NavLink
              to="/personas"
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              Personas
            </NavLink>
            <NavLink
              to="/autores"
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              Autores
            </NavLink>
            <NavLink
              to="/libros"
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              Libros
            </NavLink>
            <button type="button" className="secondary" onClick={handleLogout}>
              Cerrar sesión
            </button>
          </nav>
        ) : (
          location.pathname !== '/login' && (
            <nav className="nav-links">
              <NavLink to="/login" className="nav-link">
                Ingresar
              </NavLink>
            </nav>
          )
        )}
      </header>
      <main className="app-content">
        <AppRoutes />
      </main>
    </div>
  );
}
