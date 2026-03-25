import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterCustomer from '../../pages/RegisterCustomer';

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate,
}), { virtual: true });

test('must display the registration title', () => {
  render(<RegisterCustomer />);
  const titleElement = screen.getByText(/Register Customer/i);
  expect(titleElement).toBeInTheDocument();
});

test('must allow writing in the inputs', () => {
  render(<RegisterCustomer />);
  const nameInput = screen.getByPlaceholderText(/Full Name/i);
  fireEvent.change(nameInput, { target: { value: 'Juan Perez' } });
  expect(nameInput.value).toBe('Juan Perez');
});