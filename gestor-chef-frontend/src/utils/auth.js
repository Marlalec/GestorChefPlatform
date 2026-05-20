import { ROLE_PERMISSIONS } from './constants';

export function getStoredSession() {
  const raw = localStorage.getItem('gestorChefSession');
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem('gestorChefSession');
    return null;
  }
}

export function saveSession(session) {
  localStorage.setItem('gestorChefSession', JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem('gestorChefSession');
}

export function canAccess(role, path) {
  if (!role) return false;
  const allowed = ROLE_PERMISSIONS[role] || [];
  return allowed.some((allowedPath) => path === allowedPath || path.startsWith(`${allowedPath}/`));
}
