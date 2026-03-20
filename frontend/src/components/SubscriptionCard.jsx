import api from '../services/api';

export default function SubscriptionCard({ subscription, onChanged }) {
  const activate = async (plan) => {
    await api.post('/subscriptions/activate', { plan });
    onChanged();
  };

  return (
    <div className="card p-4">
      <h3 className="mb-3 font-display text-lg font-semibold">Subscription</h3>
      {subscription ? (
        <div className="space-y-3 text-sm text-slate-300">
          <p><span className="font-semibold text-white">Plan:</span> {subscription.plan}</p>
          <p><span className="font-semibold text-white">Status:</span> {subscription.status}</p>
          <p><span className="font-semibold text-white">Renewal:</span> {subscription.renewalDate}</p>
          <div className="flex gap-2">
            <button className="btn-secondary" onClick={() => activate('MONTHLY')}>Monthly</button>
            <button className="btn-primary" onClick={() => activate('YEARLY')}>Yearly</button>
          </div>
        </div>
      ) : (
        <p className="text-sm text-slate-400">No subscription data.</p>
      )}
    </div>
  );
}
