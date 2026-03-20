import { useState } from 'react';
import api from '../services/api';

export default function ScoreForm({ onSaved }) {
  const [scoreValue, setScoreValue] = useState('');
  const [scoreDate, setScoreDate] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post('/scores', { scoreValue: Number(scoreValue), scoreDate });
      setScoreValue('');
      setScoreDate('');
      onSaved();
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card p-4">
      <h3 className="mb-3 font-display text-lg font-semibold">Add Stableford Score</h3>
      <div className="grid gap-3 md:grid-cols-2">
        <input
          type="number"
          min="1"
          max="45"
          className="input"
          placeholder="Score (1-45)"
          value={scoreValue}
          onChange={(e) => setScoreValue(e.target.value)}
          required
        />
        <input
          type="date"
          className="input"
          value={scoreDate}
          onChange={(e) => setScoreDate(e.target.value)}
          required
        />
      </div>
      <button disabled={loading} className="btn-primary mt-4 w-full">
        {loading ? 'Saving...' : 'Save Score'}
      </button>
    </form>
  );
}
