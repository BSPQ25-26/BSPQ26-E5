import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AdminLogin from '../../pages/AdminLogin';
import { loginAdmin } from '../../api/authApi';

const mockNavigate = jest.fn();


jest.mock('react-router-dom', () => ({
    useNavigate: () => mockNavigate
}), { virtual: true });

jest.mock('../../api/authApi', () => ({
    loginAdmin: jest.fn()
}));

describe('AdminLogin Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    test('1. Renders the login form correctly', () => {
        render(<AdminLogin />);
        expect(screen.getByText(/JustOrder Admin/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Entrar/i })).toBeInTheDocument();
    });

    test('2. Updates input values as the user types', () => {
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passInput, { target: { value: 'mysecretpass' } });

        expect(emailInput.value).toBe('test@example.com');
        expect(passInput.value).toBe('mysecretpass');
    });

    test('3. Logs in successfully, saves token and redirects', async () => {
        loginAdmin.mockResolvedValueOnce('fake-token');
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        fireEvent.change(emailInput, { target: { value: 'admin@admin.com' } });
        fireEvent.change(passInput, { target: { value: '1234' } });
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            expect(localStorage.getItem('token')).toBe('fake-token');
            expect(mockNavigate).toHaveBeenCalledWith('/admin-dashboard');
        });
    });

    test('4. Shows an error message on failed login (Invalid Credentials)', async () => {
        loginAdmin.mockRejectedValueOnce(new Error('Invalid credentials'));
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        fireEvent.change(emailInput, { target: { value: 'hacker@malomalisimo.com' } });
        fireEvent.change(passInput, { target: { value: 'contraseñaincorrecta' } });
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            expect(localStorage.getItem('token')).toBeNull();
            expect(screen.getByText(/Invalid credentials/i)).toBeInTheDocument();
        });
    });

    test('5. Handles network errors gracefully', async () => {
        loginAdmin.mockRejectedValueOnce(new Error('Network Error'));
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        fireEvent.change(emailInput, { target: { value: 'admin@admin.com' } });
        fireEvent.change(passInput, { target: { value: '1234' } });
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            expect(localStorage.getItem('token')).toBeNull();
            expect(mockNavigate).not.toHaveBeenCalled();
            expect(screen.getByText(/Network Error/i)).toBeInTheDocument();
        });
    });
});