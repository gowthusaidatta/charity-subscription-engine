import { render, screen } from '@testing-library/react';
import DrawResultsCard from '../DrawResultsCard';

describe('DrawResultsCard', () => {
  it('renders draw metadata and winning numbers', () => {
    const draw = {
      monthKey: '2026-03',
      mode: 'RANDOM',
      winningNumbers: [4, 11, 23, 35, 40]
    };

    render(<DrawResultsCard draw={draw} winnings={[{ id: 'w1' }]} />);

    expect(screen.getByText('Draw Results')).toBeInTheDocument();
    expect(screen.getByText(/2026-03/)).toBeInTheDocument();
    expect(screen.getByText(/RANDOM/)).toBeInTheDocument();
    expect(screen.getByText('4')).toBeInTheDocument();
    expect(screen.getByText('40')).toBeInTheDocument();
    expect(screen.getByText(/Your Wins:/)).toBeInTheDocument();
  });

  it('renders fallback when draw is missing', () => {
    render(<DrawResultsCard draw={null} winnings={[]} />);
    expect(screen.getByText('No draw published yet.')).toBeInTheDocument();
  });
});
