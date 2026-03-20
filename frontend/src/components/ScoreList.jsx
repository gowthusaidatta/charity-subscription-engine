export default function ScoreList({ scores }) {
  return (
    <div className="card p-4">
      <h3 className="mb-3 font-display text-lg font-semibold">Last 5 Scores</h3>
      <div className="space-y-2">
        {scores?.length ? (
          scores.map((score) => (
            <div key={score.id} className="flex items-center justify-between rounded-lg border border-white/10 bg-black/20 p-3">
              <span className="text-sm text-slate-300">{score.scoreDate}</span>
              <span className="rounded-md bg-accent/20 px-2 py-1 text-sm font-bold text-accentSoft">{score.scoreValue}</span>
            </div>
          ))
        ) : (
          <p className="text-sm text-slate-400">No scores yet.</p>
        )}
      </div>
    </div>
  );
}
