import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterRestaurant from '../../pages/RegisterRestaurant';
import { registerRestaurant } from '../../api/authApi';

jest.mock('../../api/authApi', () => ({
    registerRestaurant: jest.fn(),
}));

const fillRequiredFields = () => {
    fireEvent.change(screen.getByPlaceholderText(/Restaurant Name/i), { target: { value: 'Pizza House' } });

    fireEvent.change(screen.getByPlaceholderText(/Description/i), { target: { value: 'Best pizza in town' } });
    
    fireEvent.change(screen.getByPlaceholderText(/Phone/i), { target: { value: '+34 600 000 000' } });
    
    fireEvent.change(screen.getByPlaceholderText(/^Email$/i), { target: { value: 'pizza@test.com' } });

    fireEvent.change(screen.getByPlaceholderText(/Password/i), {target: { value: 'supersecurepassword1' },});

    fireEvent.change(screen.getByPlaceholderText(/Monday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Tuesday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Wednesday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Thursday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Friday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-23:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Saturday \(HH:mm-HH:mm\)/i), { target: { value: "10:00-23:00" } });
    fireEvent.change(screen.getByPlaceholderText(/Sunday \(HH:mm-HH:mm\)/i), { target: { value: "10:00-21:00" } });

    fireEvent.change(screen.getByPlaceholderText(/City/i), { target: { value: 'Bilbao' } });
    fireEvent.change(screen.getByPlaceholderText(/Province/i), { target: { value: 'Bizkaia' } });
    fireEvent.change(screen.getByPlaceholderText(/Country/i), { target: { value: 'Spain' } });
    fireEvent.change(screen.getByPlaceholderText(/Postal Code/i), { target: { value: '48001' } });
    fireEvent.change(screen.getByPlaceholderText(/Street Number/i), { target: { value: '12' } });
    fireEvent.change(screen.getByPlaceholderText(/Longitude/i), { target: { value: '-2.935' } });
    fireEvent.change(screen.getByPlaceholderText(/Latitude/i), { target: { value: '43.263' } });

    fireEvent.click(screen.getByLabelText(/Italian/i));
};

describe('RegisterRestaurant', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders restaurant registration title', () => {
        render(<RegisterRestaurant />);
        expect(screen.getByRole("heading", { name: /Register Restaurant/i, level: 2 })).toBeInTheDocument();
    });

    test('allows typing in inputs', () => {
        render(<RegisterRestaurant />);
        const nameInput = screen.getByPlaceholderText(/Restaurant Name/i);
        fireEvent.change(nameInput, { target: { value: 'Sushi Tokyo' } });
        expect(nameInput.value).toBe('Sushi Tokyo');
    });

    test('submits payload and shows success message', async () => {
        registerRestaurant.mockResolvedValueOnce(null);

        render(<RegisterRestaurant />);
        fillRequiredFields();

        fireEvent.click(screen.getByRole('button', { name: /Register restaurant/i }));

        await waitFor(() => {
            expect(registerRestaurant).toHaveBeenCalledTimes(1);
        });

    expect(registerRestaurant).toHaveBeenCalledWith(
    expect.objectContaining({
        name: 'Pizza House',
        description: 'Best pizza in town',
        phone: '+34 600 000 000',
        email: 'pizza@test.com',
        password: 'supersecurepassword1',
        mondayWorkingHours: '09:00-22:00',
        tuesdayWorkingHours: '09:00-22:00',
        wednesdayWorkingHours: '09:00-22:00',
        thursdayWorkingHours: '09:00-22:00',
        fridayWorkingHours: '09:00-23:00',
        saturdayWorkingHours: '10:00-23:00',
        sundayWorkingHours: '10:00-21:00',
        cuisineCategoryNames: ['Italian'],
    })
    );

    expect(registerRestaurant).toHaveBeenCalledWith(
    expect.objectContaining({
        localizations: [
        expect.objectContaining({
            city: 'Bilbao',
            province: 'Bizkaia',
            country: 'Spain',
            postalCode: '48001',
            number: '12',
            longitude: -2.935,
            latitude: 43.263,
        }),
        ],
    })
    );

    expect(await screen.findByRole('alert')).toHaveTextContent(/Restaurant registered successfully/i);
    });

    test('shows error message when API fails', async () => {
        registerRestaurant.mockRejectedValueOnce(new Error('Backend error'));
        render(<RegisterRestaurant />);
        fillRequiredFields();

        fireEvent.click(screen.getByRole('button', { name: /Register restaurant/i }));

        expect(await screen.findByRole('alert')).toHaveTextContent(/Backend error/i);
    });
});