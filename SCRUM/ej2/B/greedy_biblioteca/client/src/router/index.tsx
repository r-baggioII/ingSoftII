import { Navigate, Route, Routes } from 'react-router-dom';
import { PersonasPage } from '../pages/PersonasPage';
import { PersonaDetailPage } from '../pages/PersonaDetailPage';
import { AutoresPage } from '../pages/AutoresPage';
import { LibrosPage } from '../pages/LibrosPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/personas" replace />} />
      <Route path="/personas" element={<PersonasPage />} />
      <Route path="/personas/:id" element={<PersonaDetailPage />} />
      <Route path="/autores" element={<AutoresPage />} />
      <Route path="/libros" element={<LibrosPage />} />
      <Route path="*" element={<Navigate to="/personas" replace />} />
    </Routes>
  );
}
