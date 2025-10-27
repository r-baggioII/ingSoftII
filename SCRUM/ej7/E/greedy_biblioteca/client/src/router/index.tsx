import { Navigate, Route, Routes } from 'react-router-dom';
import { PersonasPage } from '../pages/PersonasPage';
import { PersonaDetailPage } from '../pages/PersonaDetailPage';
import { AutoresPage } from '../pages/AutoresPage';
import { LibrosPage } from '../pages/LibrosPage';
import { LoginPage } from '../pages/LoginPage';
import { RequireAuth } from './RequireAuth';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<RequireAuth />}>
        <Route path="/" element={<Navigate to="/personas" replace />} />
        <Route path="/personas" element={<PersonasPage />} />
        <Route path="/personas/:id" element={<PersonaDetailPage />} />
        <Route path="/autores" element={<AutoresPage />} />
        <Route path="/libros" element={<LibrosPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/personas" replace />} />
    </Routes>
  );
}
