import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AdminLogin from '../../pages/AdminLogin';
import { loginAdmin } from '../../api/authService';

const mockNavigate = jest.fn();

// MASTER TRICK: virtual: true prevents Jest from trying to find the real library
jest.mock('react-router-dom', () => ({
    useNavigate: () => mockNavigate
}), { virtual: true });

// Interceptamos la llamada a la API
jest.mock('../../api/authService', () => ({
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

        // Type values into the inputs
        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passInput, { target: { value: 'mysecretpass' } });

        // Verify that the inputs hold the typed values
        expect(emailInput.value).toBe('test@example.com');
        expect(passInput.value).toBe('mysecretpass');
    });

    test('3. Logs in successfully, saves token and redirects', async () => {
        // Le decimos a la API mockeada que devuelva el token falso
        loginAdmin.mockResolvedValueOnce('fake-token');
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        fireEvent.change(emailInput, { target: { value: 'admin@admin.com' } });
        fireEvent.change(passInput, { target: { value: '1234' } });
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            expect(localStorage.getItem('adminToken')).toBe('fake-token');
            expect(mockNavigate).toHaveBeenCalledWith('/admin/dashboard');
        });
    });

    test('4. Shows an error message on failed login (Invalid Credentials)', async () => {
        // Mock backend rejecting the request (Invalid credentials)
        loginAdmin.mockRejectedValueOnce(new Error('Invalid credentials'));
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        // Type fake credentials
        fireEvent.change(emailInput, { target: { value: 'hacker@malomalisimo.com' } });
        fireEvent.change(passInput, { target: { value: 'contraseñaincorrecta' } });
        
        // Click the login button
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            // Verify that the token was NOT saved
            expect(localStorage.getItem('adminToken')).toBeNull();
            // Verify that the error message appears on screen
            expect(screen.getByText(/Email o contraseña incorrectos/i)).toBeInTheDocument();
        });
    });

    test('5. Handles network errors gracefully', async () => {
        // Mock backend throwing a generic network error
        loginAdmin.mockRejectedValueOnce(new Error('Network Error'));
        
        const { container } = render(<AdminLogin />);
        const emailInput = container.querySelector('input[type="email"]');
        const passInput = container.querySelector('input[type="password"]');
        
        fireEvent.change(emailInput, { target: { value: 'admin@admin.com' } });
        fireEvent.change(passInput, { target: { value: '1234' } });

        // Click the login button
        fireEvent.click(screen.getByRole('button', { name: /Entrar/i }));

        await waitFor(() => {
            // Navigation should not occur and token should not be saved
            expect(localStorage.getItem('adminToken')).toBeNull();
            expect(mockNavigate).not.toHaveBeenCalled();
            // Verify that the UI gracefully shows the error message instead of crashing
            expect(screen.getByText(/Email o contraseña incorrectos/i)).toBeInTheDocument();
        });
    });
});