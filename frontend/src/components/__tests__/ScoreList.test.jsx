import { render, screen } from '@testing-library/react';
import ScoreList from '../ScoreList';

describe('ScoreList', () => {
  it('renders score items', () => {
    const scores = [
      { id: '1', scoreValue: 34, scoreDate: '2026-03-19' },
      { id: '2', scoreValue: 29, scoreDate: '2026-03-15' }
    ];

    render(<ScoreList scores={scores} />);

    expect(screen.getByText('Last 5 Scores')).toBeInTheDocument();
    expect(screen.getByText('2026-03-19')).toBeInTheDocument();
    expect(screen.getByText('34')).toBeInTheDocument();
    expect(screen.getByText('29')).toBeInTheDocument();
  });

  it('renders empty state when no scores exist', () => {
    render(<ScoreList scores={[]} />);
    expect(screen.getByText('No scores yet.')).toBeInTheDocument();
  });
});
