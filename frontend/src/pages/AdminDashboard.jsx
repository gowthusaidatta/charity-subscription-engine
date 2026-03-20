import { useEffect, useState } from 'react';
import api from '../services/api';

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [drawMode, setDrawMode] = useState('RANDOM');
  const [drawMonth, setDrawMonth] = useState('');
  const [executedDraw, setExecutedDraw] = useState(null);
  const [winners, setWinners] = useState([]);
  const [charityForm, setCharityForm] = useState({
    name: '',
    slug: '',
    description: '',
    imageUrl: '',
    active: true,
    featured: false,
    contributionPercent: 10
  });

  const loadUsers = async () => {
    const { data } = await api.get('/admin/users');
    setUsers(data);
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const executeDraw = async () => {
    const { data } = await api.post('/draws/admin/execute', {
      monthKey: drawMonth || null,
      mode: drawMode,
      publish: true
    });
    setExecutedDraw(data);
    const winnersRes = await api.get(`/draws/admin/${data.id}/winners`);
    setWinners(winnersRes.data);
  };

  const verifyWinner = async (winnerId, verificationStatus) => {
    await api.put(`/admin/winners/${winnerId}/verify`, { verificationStatus, proofUrl: '' });
    if (executedDraw?.id) {
      const winnersRes = await api.get(`/draws/admin/${executedDraw.id}/winners`);
      setWinners(winnersRes.data);
    }
  };

  const markPaid = async (winnerId) => {
    await api.put(`/admin/winners/${winnerId}/pay`);
    if (executedDraw?.id) {
      const winnersRes = await api.get(`/draws/admin/${executedDraw.id}/winners`);
      setWinners(winnersRes.data);
    }
  };

  const createCharity = async () => {
    await api.post('/charities/admin', charityForm);
    setCharityForm({
      name: '',
      slug: '',
      description: '',
      imageUrl: '',
      active: true,
      featured: false,
      contributionPercent: 10
    });
  };

  return (
    <div className="space-y-6">
      <div className="card p-6">
        <h1 className="font-display text-3xl font-bold text-white">Admin Dashboard</h1>
        <p className="mt-1 text-slate-300">Manage users, draws, charities, and winner payouts.</p>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <div className="card p-4">
          <h2 className="mb-3 font-display text-xl font-semibold">Run Monthly Draw</h2>
          <div className="space-y-3">
            <input className="input" placeholder="Month key (YYYY-MM, optional)" value={drawMonth} onChange={(e) => setDrawMonth(e.target.value)} />
            <select className="input" value={drawMode} onChange={(e) => setDrawMode(e.target.value)}>
              <option value="RANDOM">Random</option>
              <option value="WEIGHTED">Weighted</option>
            </select>
            <button className="btn-primary w-full" onClick={executeDraw}>Execute Draw</button>
          </div>
          {executedDraw && (
            <div className="mt-4 rounded-lg border border-white/10 bg-black/20 p-3 text-sm text-slate-200">
              Winning numbers: {executedDraw.winningNumbers.join(', ')}
            </div>
          )}
        </div>

        <div className="card p-4">
          <h2 className="mb-3 font-display text-xl font-semibold">Create Charity</h2>
          <div className="space-y-2">
            <input className="input" placeholder="Name" value={charityForm.name} onChange={(e) => setCharityForm({ ...charityForm, name: e.target.value })} />
            <input className="input" placeholder="Slug" value={charityForm.slug} onChange={(e) => setCharityForm({ ...charityForm, slug: e.target.value })} />
            <textarea className="input" placeholder="Description" value={charityForm.description} onChange={(e) => setCharityForm({ ...charityForm, description: e.target.value })} />
            <input className="input" placeholder="Image URL" value={charityForm.imageUrl} onChange={(e) => setCharityForm({ ...charityForm, imageUrl: e.target.value })} />
            <button className="btn-primary w-full" onClick={createCharity}>Save Charity</button>
          </div>
        </div>
      </div>

      <div className="card p-4">
        <h2 className="mb-3 font-display text-xl font-semibold">Users</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead>
              <tr className="border-b border-white/10 text-slate-300">
                <th className="p-2">Name</th>
                <th className="p-2">Email</th>
                <th className="p-2">Role</th>
                <th className="p-2">Charity</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id} className="border-b border-white/5 text-slate-200">
                  <td className="p-2">{user.fullName}</td>
                  <td className="p-2">{user.email}</td>
                  <td className="p-2">{user.role}</td>
                  <td className="p-2">{user.selectedCharity || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="card p-4">
        <h2 className="mb-3 font-display text-xl font-semibold">Winners</h2>
        <div className="space-y-2">
          {winners.map((winner) => (
            <div key={winner.id} className="flex flex-col gap-3 rounded-lg border border-white/10 bg-black/20 p-3 md:flex-row md:items-center md:justify-between">
              <div className="text-sm text-slate-200">
                <p>{winner.userEmail}</p>
                <p>Match: {winner.matchCount} | Prize: {winner.prizeAmount}</p>
                <p>Status: {winner.verificationStatus} / {winner.payoutStatus}</p>
              </div>
              <div className="flex gap-2">
                <button className="btn-secondary" onClick={() => verifyWinner(winner.id, 'APPROVED')}>Approve</button>
                <button className="btn-secondary" onClick={() => verifyWinner(winner.id, 'REJECTED')}>Reject</button>
                <button className="btn-primary" onClick={() => markPaid(winner.id)}>Mark Paid</button>
              </div>
            </div>
          ))}
          {!winners.length && <p className="text-sm text-slate-400">No winners yet.</p>}
        </div>
      </div>
    </div>
  );
}
