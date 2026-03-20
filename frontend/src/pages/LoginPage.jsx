import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(email, password);
      const role = JSON.parse(localStorage.getItem('user') || '{}').role;
      navigate(role === 'ADMIN' ? '/admin' : '/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto w-full max-w-md card p-6">
      <h1 className="font-display text-2xl font-bold text-white">Login</h1>
      <p className="mt-1 text-sm text-slate-300">Access your impact dashboard.</p>
      <form onSubmit={handleSubmit} className="mt-5 space-y-3">
        <input className="input" type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input className="input" type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        {error && <p className="text-sm text-rose-400">{error}</p>}
        <button className="btn-primary w-full" disabled={loading}>{loading ? 'Logging in...' : 'Login'}</button>
      </form>
      <p className="mt-4 text-sm text-slate-300">
        New here? <Link to="/signup" className="text-accentSoft">Create account</Link>
      </p>
    </div>
  );
}
