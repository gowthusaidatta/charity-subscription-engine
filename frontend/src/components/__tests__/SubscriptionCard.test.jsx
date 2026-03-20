import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SubscriptionCard from '../SubscriptionCard';

vi.mock('../../services/api', () => ({
  default: {
    post: vi.fn(() => Promise.resolve({ data: {} }))
  }
}));

describe('SubscriptionCard', () => {
  it('renders subscription details', () => {
    const subscription = {
      plan: 'MONTHLY',
      status: 'ACTIVE',
      renewalDate: '2026-04-20'
    };

    render(<SubscriptionCard subscription={subscription} onChanged={vi.fn()} />);

    expect(screen.getByText('Subscription')).toBeInTheDocument();
    expect(screen.getByText(/MONTHLY/)).toBeInTheDocument();
    expect(screen.getByText(/ACTIVE/)).toBeInTheDocument();
    expect(screen.getByText(/2026-04-20/)).toBeInTheDocument();
  });

  it('triggers activation action on yearly button click', async () => {
    const api = (await import('../../services/api')).default;
    const onChanged = vi.fn();

    render(
      <SubscriptionCard
        subscription={{ plan: 'MONTHLY', status: 'INACTIVE', renewalDate: '2026-04-20' }}
        onChanged={onChanged}
      />
    );

    await userEvent.click(screen.getByRole('button', { name: 'Yearly' }));

    expect(api.post).toHaveBeenCalledWith('/subscriptions/activate', { plan: 'YEARLY' });
  });
});
