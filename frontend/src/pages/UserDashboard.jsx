import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../services/api';
import ScoreForm from '../components/ScoreForm';
import ScoreList from '../components/ScoreList';
import SubscriptionCard from '../components/SubscriptionCard';
import CharitySelector from '../components/CharitySelector';
import DrawResultsCard from '../components/DrawResultsCard';

export default function UserDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [draw, setDraw] = useState(null);

  const loadDashboard = async () => {
    const [{ data: dashboardData }, { data: drawData }] = await Promise.all([
      api.get('/dashboard/me'),
      api.get('/draws/latest').catch(() => ({ data: null }))
    ]);
    setDashboard(dashboardData);
    setDraw(drawData);
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  return (
    <div className="space-y-6">
      <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="card p-6">
        <h1 className="font-display text-3xl font-bold text-white">User Dashboard</h1>
        <p className="mt-1 text-slate-300">Manage scores, charity allocation, and draw participation.</p>
      </motion.div>

      <div className="grid gap-4 lg:grid-cols-3">
        <SubscriptionCard subscription={dashboard?.subscription} onChanged={loadDashboard} />
        <CharitySelector onSelected={loadDashboard} />
        <DrawResultsCard draw={draw} winnings={dashboard?.recentWinnings} />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <ScoreForm onSaved={loadDashboard} />
        <ScoreList scores={dashboard?.scores || []} />
      </div>
    </div>
  );
}
