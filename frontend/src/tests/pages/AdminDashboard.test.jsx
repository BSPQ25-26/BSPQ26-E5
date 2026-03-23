import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AdminDashboard from '../../pages/AdminDashboard';

const mockNavigate = jest.fn();

// MASTER TRICK: virtual: true prevents Jest from trying to find the real library
jest.mock('react-router-dom', () => ({
    useNavigate: () => mockNavigate
}), { virtual: true });

describe('AdminDashboard Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.setItem('adminToken', 'fake-jwt-token');
        
        // By default, we mock fetch to always return empty arrays so the component 
        // doesn't crash when it does the 8 simultaneous requests on mount.
        global.fetch = jest.fn(() => Promise.resolve({ 
            ok: true, 
            json: async () => [] 
        }));
    });

    afterEach(() => {
        localStorage.clear();
    });

    test('1. Renders navigation tabs and logs out', async () => {
        render(<AdminDashboard />);

        // Verify tabs exist
        expect(screen.getByText('🍔 Restaurantes')).toBeInTheDocument();
        expect(screen.getByText('👥 Clientes')).toBeInTheDocument();
        expect(screen.getByText('🍲 Platos')).toBeInTheDocument();
        expect(screen.getByText('📦 Pedidos')).toBeInTheDocument();

        // Click logout
        const logoutButton = screen.getByText(/Cerrar Sesión/i);
        fireEvent.click(logoutButton);

        // Verify session is cleared and redirected
        expect(localStorage.getItem('adminToken')).toBeNull();
        expect(mockNavigate).toHaveBeenCalledWith('/admin/login');
    });

    test('2. Loads and displays restaurants in the table on start', async () => {
        const mockRestaurants = [
            { id: 1, name: 'El Gran Taco', email: 'taco@rest.com', phone: '600111222' }
        ];

        // Override the default fetch to return our fake restaurant
        global.fetch.mockImplementation((url) => {
            if (url.includes('/api/restaurants')) return Promise.resolve({ ok: true, json: async () => mockRestaurants });
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        render(<AdminDashboard />);

        await waitFor(() => {
            expect(screen.getByText('El Gran Taco')).toBeInTheDocument();
            expect(screen.getByText('taco@rest.com')).toBeInTheDocument();
        });
    });

    test('3. Allows opening and closing the add Restaurant form', async () => {
        render(<AdminDashboard />);

        await waitFor(() => expect(screen.getByText('+ Añadir Restaurante')).toBeInTheDocument());

        // Open form
        fireEvent.click(screen.getByText('+ Añadir Restaurante'));
        expect(screen.getByPlaceholderText('Nombre')).toBeInTheDocument();

        // Close form
        fireEvent.click(screen.getByText('Cancelar'));
        expect(screen.queryByPlaceholderText('Nombre')).not.toBeInTheDocument();
    });

    test('4. Allows switching between tabs', async () => {
        render(<AdminDashboard />);

        await waitFor(() => expect(screen.getByText('Gestión de Restaurantes')).toBeInTheDocument());

        // Switch to Dishes tab
        const dishesTab = screen.getByText('🍲 Platos');
        fireEvent.click(dishesTab);

        await waitFor(() => {
            expect(screen.getByText('Gestión de Platos')).toBeInTheDocument();
            expect(screen.queryByText('Gestión de Restaurantes')).not.toBeInTheDocument();
        });
    });

    test('5. Deletes a restaurant when confirming', async () => {
        const mockRestaurants = [
            { id: 1, name: 'Restaurante a Eliminar', email: 'delete@rest.com', phone: '123123123' }
        ];

        global.fetch.mockImplementation((url, options) => {
            if (options?.method === 'DELETE') return Promise.resolve({ ok: true });
            if (url.includes('/api/restaurants')) return Promise.resolve({ ok: true, json: async () => mockRestaurants });
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        // Mock window.confirm to automatically click "Yes"
        window.confirm = jest.fn(() => true);

        render(<AdminDashboard />);

        await waitFor(() => {
            expect(screen.getByText('Restaurante a Eliminar')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('Eliminar'));

        // Verify the confirmation prompt and the DELETE API call
        expect(window.confirm).toHaveBeenCalledWith('¿Seguro que quieres eliminar este restaurante?');
        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8080/api/restaurants/1',
            expect.objectContaining({ method: 'DELETE' })
        );
    });

    test('6. Creates a new restaurant successfully', async () => {
        render(<AdminDashboard />);
        await waitFor(() => expect(screen.getByText('Gestión de Restaurantes')).toBeInTheDocument());

        fireEvent.click(screen.getByText('+ Añadir Restaurante'));
        
        // Fill the form
        fireEvent.change(screen.getByPlaceholderText('Nombre'), { target: { value: 'New Rest' } });
        fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'new@test.com' } });
        fireEvent.change(screen.getByPlaceholderText('Teléfono'), { target: { value: '123456789' } });
        fireEvent.change(screen.getByPlaceholderText('Contraseña'), { target: { value: 'pass123' } });
        fireEvent.change(screen.getByPlaceholderText('Descripción breve'), { target: { value: 'Good food' } });

        // Mock the POST request to succeed
        global.fetch.mockImplementation((url, options) => {
            if (options?.method === 'POST' && url.includes('/create')) return Promise.resolve({ ok: true });
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        fireEvent.click(screen.getByText('Guardar Restaurante'));

        // Verify that the payload was sent to the server
        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                'http://localhost:8080/api/restaurants/create',
                expect.objectContaining({ method: 'POST' })
            );
        });
    });

    test('7. Navigates to Riders tab and deletes a rider', async () => {
        global.fetch.mockImplementation((url, options) => {
            // If it's a DELETE request, return OK
            if (options?.method === 'DELETE') return Promise.resolve({ ok: true });
            
            // If it's the request to fetch riders, return Flash Rider
            // (Notice we removed the "!options" check because the component sends headers)
            if (url.includes('/api/riders')) {
                return Promise.resolve({ ok: true, json: async () => [{ id: 10, name: 'Flash Rider', phoneNumber: '555' }] });
            }
            
            // For all other requests the component makes, return an empty array
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        // Mock window.confirm to automatically click "Yes"
        window.confirm = jest.fn(() => true);

        render(<AdminDashboard />);
        
        // Go to the riders tab
        fireEvent.click(screen.getByText('🛵 Repartidores'));

        // Wait for the mock rider to appear on the screen
        await waitFor(() => {
            expect(screen.getByText('Flash Rider')).toBeInTheDocument();
        });

        // Click the delete/fire button
        fireEvent.click(screen.getByText('Despedir'));

        // Verify the confirmation prompt and the DELETE API call
        expect(window.confirm).toHaveBeenCalledWith('¿Seguro que quieres despedir a este repartidor?');
        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8080/api/riders/10',
            expect.objectContaining({ method: 'DELETE' })
        );
    });

    test('8. Navigates to Categories, enters edit mode and updates a category', async () => {
        global.fetch.mockImplementation((url, options) => {
            if (url.includes('/api/categories/all')) {
                return Promise.resolve({ ok: true, json: async () => [{ id: 5, name: 'Mexicana', description: 'Tacos y burritos' }] });
            }
            if (options?.method === 'PUT') return Promise.resolve({ ok: true });
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        render(<AdminDashboard />);
        fireEvent.click(screen.getByText('🍽️ Categorías'));

        await waitFor(() => {
            expect(screen.getByText('Mexicana')).toBeInTheDocument();
        });

        // Click Edit
        fireEvent.click(screen.getByText('Editar'));

        // Check if form opened and input is populated
        const nameInput = await screen.findByPlaceholderText('Nombre (Ej: Italiana)');
        expect(nameInput.value).toBe('Mexicana');

        // Change value and save
        fireEvent.change(nameInput, { target: { value: 'Italiana' } });
        fireEvent.click(screen.getByText('Guardar Categoría'));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                'http://localhost:8080/api/categories/update/5',
                expect.objectContaining({ method: 'PUT' })
            );
        });
    });

    test('9. Navigates to Dishes and triggers create dish', async () => {
        render(<AdminDashboard />);
        fireEvent.click(screen.getByText('🍲 Platos'));

        await waitFor(() => expect(screen.getByText('+ Añadir Plato')).toBeInTheDocument());
        fireEvent.click(screen.getByText('+ Añadir Plato'));

        // Fill required fields
        fireEvent.change(screen.getByPlaceholderText('Nombre del Plato'), { target: { value: 'Spaghetti' } });
        fireEvent.change(screen.getByPlaceholderText('Precio (€)'), { target: { value: '12.50' } });
        fireEvent.change(screen.getByPlaceholderText('Descripción breve'), { target: { value: 'Pasta con tomate' } });
        
        global.fetch.mockImplementation((url, options) => {
            if (options?.method === 'POST') return Promise.resolve({ ok: true });
            return Promise.resolve({ ok: true, json: async () => [] });
        });

        fireEvent.click(screen.getByText('Guardar Plato'));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                expect.stringContaining('/api/dishes/'),
                expect.objectContaining({ method: 'POST' })
            );
        });
    });

    test('10. Handles network errors gracefully during initial data fetch', async () => {
        // Force all initial fetches to fail
        global.fetch.mockRejectedValue(new Error('Server is down'));
        
        // Spy on console.error so the test terminal output stays clean
        const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

        render(<AdminDashboard />);

        await waitFor(() => {
            // Verify that the errors were caught and printed to console internally
            expect(consoleSpy).toHaveBeenCalledTimes(8); // Because there are 8 fetches on mount
        });

        // The dashboard should still be visible even if data failed to load
        expect(screen.getByText('Panel de Control del Administrador 🚀')).toBeInTheDocument();
        expect(screen.getByText('Gestión de Restaurantes')).toBeInTheDocument();

        consoleSpy.mockRestore();
    });
});