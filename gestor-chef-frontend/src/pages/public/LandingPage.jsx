import { Link } from 'react-router-dom';
import chefImage from '../../assets/chef.png';

export function LandingPage() {
  return (
    <div className="theme landing-page">
      <header className="navbar">
        <div className="container nav-inner">
          <Link className="brand" to="/">
            <span className="material-symbols-rounded">restaurant_menu</span>
            <strong>GESTOR CHEF</strong>
          </Link>
          <nav className="nav-links nav-links--public">
            <Link className="active" to="/">Inicio</Link>
          </nav>
          <div className="nav-actions">
            <Link className="icon-btn" to="/login" aria-label="Iniciar sesión">
              <span className="material-symbols-rounded">person</span>
            </Link>
          </div>
        </div>
      </header>
      <main>
        <section className="hero">
          <div className="container hero-grid">
            <div className="hero-copy">
              <h1 className="display">Gestiona tu<br />restaurante</h1>
              <p>Controla inventario, recetas, pedidos, proveedores, alertas y reportes desde una interfaz clara conectada al backend de Gestor Chef.</p>
              <Link className="btn btn-primary" to="/login">Comenzar</Link>
            </div>
            <div className="hero-figure">
              <img src={chefImage} alt="Chef emplatando" />
            </div>
          </div>
        </section>
        <section className="feature-area">
          <div className="container feature-grid">
            <article className="card feature-card">
              <div className="feature-icon"><span className="material-symbols-rounded">menu_book</span></div>
              <h3>Recetas y menú</h3>
              <p>Estandariza platos, ingredientes y costos para mantener control operativo.</p>
            </article>
            <article className="card feature-card">
              <div className="feature-icon"><span className="material-symbols-rounded">inventory_2</span></div>
              <h3>Inventario</h3>
              <p>Consulta stock, movimientos, mínimos y alertas para evitar rupturas.</p>
            </article>
            <article className="card feature-card">
              <div className="feature-icon"><span className="material-symbols-rounded">monitoring</span></div>
              <h3>Reportes</h3>
              <p>Genera reportes de inventario, mermas, finanzas y demanda.</p>
            </article>
          </div>
        </section>
      </main>
      <footer className="footer">© 2026 Gestor Chef</footer>
    </div>
  );
}
