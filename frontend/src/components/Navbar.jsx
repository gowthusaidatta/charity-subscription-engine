import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="sticky top-0 z-40 border-b border-white/10 bg-slate-950/80 backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-4 py-4">
        <Link to="/" className="font-display text-xl font-bold tracking-tight text-white">
          Impact Draw
        </Link>

        <nav className="flex items-center gap-2 md:gap-3">
          {!isAuthenticated && (
            <>
              <Link to="/login" className="btn-secondary text-sm">Login</Link>
              <Link to="/signup" className="btn-primary text-sm">Join Now</Link>
            </>
          )}

          {isAuthenticated && (
            <>
              <Link to={user?.role === 'ADMIN' ? '/admin' : '/dashboard'} className="btn-secondary text-sm">
                {user?.role === 'ADMIN' ? 'Admin' : 'Dashboard'}
              </Link>
              <button onClick={handleLogout} className="btn-primary text-sm">Logout</button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
