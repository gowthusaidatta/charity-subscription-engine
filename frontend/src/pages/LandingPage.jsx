import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';

export default function LandingPage() {
  return (
    <div className="space-y-10">
      <motion.section
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        className="card overflow-hidden p-8"
      >
        <div className="grid gap-8 md:grid-cols-2 md:items-center">
          <div>
            <p className="mb-3 text-sm uppercase tracking-wider text-accentSoft">Play. Win. Give.</p>
            <h1 className="font-display text-4xl font-bold leading-tight text-white md:text-5xl">
              Turn Every Round Into Real-World Impact
            </h1>
            <p className="mt-4 max-w-xl text-slate-300">
              Subscribe, submit your latest scores, enter monthly prize draws, and direct your contribution to the charity you care about.
            </p>
            <div className="mt-6 flex flex-wrap gap-3">
              <Link to="/signup" className="btn-primary">Start Subscription</Link>
              <Link to="/login" className="btn-secondary">I Already Have an Account</Link>
            </div>
          </div>
          <div className="rounded-2xl border border-white/10 bg-gradient-to-br from-cyan-400/20 via-teal-400/10 to-sky-500/20 p-6">
            <h2 className="font-display text-xl font-semibold text-white">How it works</h2>
            <ul className="mt-4 space-y-3 text-sm text-slate-200">
              <li>1. Pick monthly or yearly subscription.</li>
              <li>2. Keep your latest 5 Stableford scores up to date.</li>
              <li>3. Enter monthly draw (random or weighted).</li>
              <li>4. Win prizes while funding charitable impact.</li>
            </ul>
          </div>
        </div>
      </motion.section>

      <section className="grid gap-4 md:grid-cols-3">
        {[
          { title: 'Subscription Engine', body: 'Status tracking, access control, lifecycle states.' },
          { title: 'Performance Draws', body: 'Monthly draw logic with 3, 4, and 5 match tiers.' },
          { title: 'Charity Allocation', body: 'Minimum 10% contribution with user-level choice.' }
        ].map((item, idx) => (
          <motion.div
            key={item.title}
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: idx * 0.1 + 0.2 }}
            className="card p-5"
          >
            <h3 className="font-display text-lg font-semibold text-white">{item.title}</h3>
            <p className="mt-2 text-sm text-slate-300">{item.body}</p>
          </motion.div>
        ))}
      </section>
    </div>
  );
}
