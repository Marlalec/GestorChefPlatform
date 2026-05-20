import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { AlertMessage } from '../../components/common/AlertMessage';
import { useAuth } from '../../context/AuthContext';
import { getErrorMessage } from '../../services/api';
import chefImage from '../../assets/chef.png';

export function LoginPage() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const [values, setValues] = useState({ email: 'admin@gestor.chef', password: 'Admin123!' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(values);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="theme login-page">
      <header className="navbar">
        <div className="container nav-inner">
          <Link className="brand" to="/">
            <span className="material-symbols-rounded">restaurant_menu</span>
            <strong>GESTOR CHEF</strong>
          </Link>
        </div>
      </header>
      <main className="login-wrap">
        <div className="container login-grid">
          <section className="card--teal login-info login-title">
            <h1 className="display display--sm ">Bienvenido a <br></br>Gestor Chef</h1>
            <p>Inicia sesión para acceder a las opciones disponibles para tu usuario</p>
          </section>
          <section className="login-card">
            <h2>Iniciar sesión</h2>
            <br />
            <AlertMessage type="error" message={error} />
            <form className="form" onSubmit={handleSubmit}>
              <label className="label">
                <span>Email</span>
                <input className="input" type="email" value={values.email} onChange={(event) => updateField('email', event.target.value)} required />
              </label>
              <label className="label">
                <span>Contraseña</span>
                <input className="input" type="password" value={values.password} onChange={(event) => updateField('password', event.target.value)} required />
              </label>
              <button className="btn btn-primary btn-full" type="submit" disabled={loading}>{loading ? 'Ingresando...' : 'Ingresar'}</button>
            </form>
          </section>
        </div>
      </main>
      <footer className="footer">© 2026 Gestor Chef</footer>
    </div>
  );
}
