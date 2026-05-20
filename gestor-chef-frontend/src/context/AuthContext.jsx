import { createContext, useContext, useMemo, useState } from 'react';
import { authService } from '../services/authService';
import { clearSession, getStoredSession, saveSession } from '../utils/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getStoredSession());

  async function login(credentials) {
    const data = await authService.login(credentials);
    const nextSession = {
      token: data.token,
      type: data.type || 'Bearer',
      user: {
        id: data.userId,
        name: data.name,
        email: data.email,
        rol: data.rol,
        phone: data.phone,
        accountStatus: data.accountStatus
      }
    };
    saveSession(nextSession);
    setSession(nextSession);
    return nextSession;
  }

  function logout() {
    clearSession();
    setSession(null);
  }

  const value = useMemo(() => ({
    session,
    user: session?.user || null,
    token: session?.token || null,
    isAuthenticated: Boolean(session?.token),
    login,
    logout
  }), [session]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return context;
}
