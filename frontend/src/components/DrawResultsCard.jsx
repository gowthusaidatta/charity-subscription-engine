export default function DrawResultsCard({ draw, winnings }) {
  return (
    <div className="card p-4">
      <h3 className="mb-3 font-display text-lg font-semibold">Draw Results</h3>
      {draw ? (
        <div className="space-y-3 text-sm text-slate-300">
          <p><span className="font-semibold text-white">Month:</span> {draw.monthKey}</p>
          <p><span className="font-semibold text-white">Mode:</span> {draw.mode}</p>
          <div className="flex flex-wrap gap-2">
            {draw.winningNumbers?.map((num) => (
              <span key={num} className="rounded-full bg-accent/20 px-3 py-1 text-accentSoft">{num}</span>
            ))}
          </div>
          <p><span className="font-semibold text-white">Your Wins:</span> {winnings?.length || 0}</p>
        </div>
      ) : (
        <p className="text-sm text-slate-400">No draw published yet.</p>
      )}
    </div>
  );
}
