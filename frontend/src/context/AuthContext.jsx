import { createContext, useContext, useMemo, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(() => {
    const cached = localStorage.getItem('user');
    return cached ? JSON.parse(cached) : null;
  });

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password });
    setToken(data.token);
    setUser({ email: data.email, role: data.role, fullName: data.fullName });
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({ email: data.email, role: data.role, fullName: data.fullName }));
  };

  const signup = async (fullName, email, password) => {
    const { data } = await api.post('/auth/register', { fullName, email, password });
    setToken(data.token);
    setUser({ email: data.email, role: data.role, fullName: data.fullName });
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({ email: data.email, role: data.role, fullName: data.fullName }));
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const value = useMemo(
    () => ({ token, user, isAuthenticated: Boolean(token), login, signup, logout }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
