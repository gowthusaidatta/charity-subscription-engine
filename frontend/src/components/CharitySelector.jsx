import { useEffect, useState } from 'react';
import api from '../services/api';

export default function CharitySelector({ onSelected }) {
  const [charities, setCharities] = useState([]);
  const [charityId, setCharityId] = useState('');
  const [contributionPercent, setContributionPercent] = useState(10);

  useEffect(() => {
    const load = async () => {
      const { data } = await api.get('/charities');
      setCharities(data);
      if (data.length > 0) {
        setCharityId(data[0].id);
      }
    };
    load();
  }, []);

  const save = async () => {
    await api.post('/charities/select', {
      charityId,
      contributionPercent: Number(contributionPercent)
    });
    onSelected();
  };

  return (
    <div className="card p-4">
      <h3 className="mb-3 font-display text-lg font-semibold">Charity Selection</h3>
      <div className="space-y-3">
        <select className="input" value={charityId} onChange={(e) => setCharityId(e.target.value)}>
          {charities.map((charity) => (
            <option key={charity.id} value={charity.id}>{charity.name}</option>
          ))}
        </select>
        <input
          type="number"
          min="10"
          step="0.1"
          className="input"
          value={contributionPercent}
          onChange={(e) => setContributionPercent(e.target.value)}
        />
        <button onClick={save} className="btn-primary w-full">Save Charity</button>
      </div>
    </div>
  );
}
