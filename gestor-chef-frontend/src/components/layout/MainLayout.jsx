import { Link, NavLink, Outlet, useLocation } from 'react-router-dom';
import { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { alertService } from '../../services/alertService';
import { canAccess } from '../../utils/auth';
import { formatRole } from '../../utils/formatters';

const navItems = [
  {
    to: '/dashboard',
    label: 'Inicio',
    icon: 'home'
  },
  {
    label: 'Inventario',
    icon: 'inventory_2',
    children: [
      { to: '/inventory', label: 'Inventario general', icon: 'inventory_2' },
      { to: '/products', label: 'Productos', icon: 'restaurant_menu' },
      { to: '/categories', label: 'Categorías', icon: 'category' },
      { to: '/suppliers', label: 'Proveedores', icon: 'local_shipping' }
    ]
  },
  {
    label: 'Operación',
    icon: 'room_service',
    children: [
      { to: '/orders', label: 'Pedidos mesa', icon: 'table_restaurant' },
      { to: '/recipes', label: 'Recetas', icon: 'menu_book' },
      { to: '/wastes', label: 'Desperdicios', icon: 'delete_sweep' }
    ]
  },
  {
    label: 'Control',
    icon: 'monitoring',
    children: [
      { to: '/alerts', label: 'Alertas', icon: 'notifications' },
      { to: '/reports', label: 'Reportes', icon: 'monitoring' }
    ]
  },
  {
    label: 'Administración',
    icon: 'admin_panel_settings',
    children: [
      { to: '/users', label: 'Usuarios', icon: 'groups' },
    ]
  }
];

export function MainLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const [alertCount, setAlertCount] = useState(0);
  const [activeMenu, setActiveMenu] = useState(null);

  const visibleItems = useMemo(() => {
    return navItems
      .map((item) => {
        if (!item.children) {
          return canAccess(user?.rol, item.to) ? item : null;
        }

        const visibleChildren = item.children.filter((child) =>
          canAccess(user?.rol, child.to)
        );

        if (visibleChildren.length === 0) {
          return null;
        }

        return {
          ...item,
          children: visibleChildren
        };
      })
      .filter(Boolean);
  }, [user?.rol]);

  useEffect(() => {
    let isMounted = true;
    async function loadAlerts() {
      try {
        const data = user?.id ? await alertService.getUnread(user.id) : await alertService.getAll();
        if (isMounted) setAlertCount(Array.isArray(data) ? data.length : 0);
      } catch {
        if (isMounted) setAlertCount(0);
      }
    }
    loadAlerts();
    return () => { isMounted = false; };
  }, [location.pathname, user?.id]);

  return (
    <div className="app-shell theme">
      <header className="navbar">
        <div className="container nav-inner">
          <Link className="brand" to="/dashboard">
            <span className="material-symbols-rounded">restaurant_menu</span>
            <strong>GESTOR CHEF</strong>
          </Link>
          <button className="nav-toggle" type="button" onClick={() => setMenuOpen((open) => !open)}>
            <span className="material-symbols-rounded">menu</span>
          </button>
            <nav className={`nav-links ${menuOpen ? 'nav-links--open' : ''}`}>
              {visibleItems.map((item) => {
                if (item.children) {
                  const isOpen = activeMenu === item.label;

                  return (
                    <div key={item.label} className={`nav-dropdown ${isOpen ? 'nav-dropdown--open' : ''}`}>
                      <button
                        type="button"
                        className={`nav-dropdown-button ${
                          item.children.some((child) => location.pathname.startsWith(child.to))
                            ? 'active'
                            : ''
                        }`}
                        onClick={() =>
                          setActiveMenu((currentMenu) =>
                            currentMenu === item.label ? null : item.label
                          )
                        }
                      >
                        <span className="material-symbols-rounded">{item.icon}</span>
                        {item.label}
                        <span className="material-symbols-rounded">expand_more</span>
                      </button>

                      <div className="nav-dropdown-menu">
                        {item.children.map((child) => (
                          <NavLink
                            key={child.to}
                            to={child.to}
                            className="nav-dropdown-item"
                            onClick={() => {
                              setMenuOpen(false);
                              setActiveMenu(null);
                            }}
                          >
                            <span className="material-symbols-rounded">{child.icon}</span>
                            {child.label}
                          </NavLink>
                        ))}
                      </div>
                    </div>
                  );
                }

                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    onClick={() => {
                      setMenuOpen(false);
                      setActiveMenu(null);
                    }}
                  >
                    <span className="material-symbols-rounded">{item.icon}</span>
                    {item.label}
                  </NavLink>
                );
              })}
            </nav>
          <div className="nav-actions">
            <Link className="icon-btn" to="/alerts" aria-label="Notificaciones">
              <span className="material-symbols-rounded">notifications</span>
              {alertCount > 0 && <span className="badge">{alertCount}</span>}
            </Link>
            <div className="user-pill">
              <strong>{user?.name || 'Usuario'}</strong>
              <span>{formatRole(user?.rol)}</span>
            </div>
            <button className="icon-btn" type="button" onClick={logout} aria-label="Cerrar sesión">
              <span className="material-symbols-rounded">logout</span>
            </button>
          </div>
        </div>
      </header>
      <main className="container dashboard">
        <Outlet />
      </main>
      <footer className="footer">© 2026 Gestor Chef</footer>
    </div>
  );
}
