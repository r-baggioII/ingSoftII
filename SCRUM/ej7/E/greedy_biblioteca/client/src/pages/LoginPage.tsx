import { useState } from 'react';
import { Location, useLocation, useNavigate } from 'react-router-dom';
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

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await login({ username, password });
      setToken(response.access_token);
      showToast('Sesión iniciada');
      const redirectTo =
        (location.state as { from?: Location })?.from?.pathname ?? '/personas';
      navigate(redirectTo, { replace: true });
    } catch (error) {
      console.error(error);
      showToast('Credenciales inválidas', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="login-page">
      <div className="card" style={{ maxWidth: '420px', margin: '4rem auto', padding: '2rem' }}>
        <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Iniciar sesión</h2>
        <form onSubmit={handleSubmit} className="form">
          <div className="form-row">
            <label htmlFor="username">Usuario</label>
            <input
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div className="form-row">
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          <div className="actions" style={{ justifyContent: 'flex-end' }}>
            <button type="submit" disabled={loading}>
              {loading ? 'Ingresando...' : 'Ingresar'}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
}
