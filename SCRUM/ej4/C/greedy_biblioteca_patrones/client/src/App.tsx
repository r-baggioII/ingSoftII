import { NavLink } from 'react-router-dom';
import { AppRoutes } from './router';

export default function App() {
  return (
    <div className="app-layout">
      <header className="app-header">
        <h1>Biblioteca</h1>
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
        </nav>
      </header>
      <main className="app-content">
        <AppRoutes />
      </main>
    </div>
  );
}
