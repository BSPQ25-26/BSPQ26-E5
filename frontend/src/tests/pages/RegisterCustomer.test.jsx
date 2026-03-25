import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterCustomer from '../../pages/RegisterCustomer';
import { MemoryRouter } from 'react-router-dom';
const renderWithRouter = (ui) =>
  render(
    <MemoryRouter
      future={{ v7_startTransition: true, v7_relativeSplatPath: true }}
    >
      {ui}
    </MemoryRouter>
  );
test('must display the registration title', () => {
    renderWithRouter(<RegisterCustomer />);    const titleElement = screen.getByText(/Register Customer/i);
    expect(titleElement).toBeInTheDocument();
});

test('must allow writing in the inputs', () => {
    renderWithRouter(<RegisterCustomer />);
    const nameInput = screen.getByPlaceholderText(/Full Name/i);
    fireEvent.change(nameInput, { target: { value: 'Juan Perez' } });
    expect(nameInput.value).toBe('Juan Perez');
});