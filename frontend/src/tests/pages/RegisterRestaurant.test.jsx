import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import RegisterRestaurant from "../../pages/RegisterRestaurant";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("RegisterRestaurant", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("renders restaurant registration page", () => {
        render(<RegisterRestaurant />);
        expect(screen.getByRole("heading", { name: /Register Restaurant/i, level: 2 })).toBeInTheDocument();
    });

    test("submits restaurant registration to live backend", async () => {
        const uniqueEmail = `restaurant-${Date.now()}@example.com`;

        render(<RegisterRestaurant />);

        fireEvent.change(screen.getByPlaceholderText(/Restaurant Name/i), { target: { value: "Integration Bistro" } });
        fireEvent.change(screen.getByPlaceholderText(/Description/i), { target: { value: "Integration test restaurant" } });
        fireEvent.change(screen.getByPlaceholderText(/Phone/i), { target: { value: "+34 600 000 000" } });
        fireEvent.change(screen.getByPlaceholderText(/^Email$/i), { target: { value: uniqueEmail } });
        fireEvent.change(screen.getByPlaceholderText(/Password/i), { target: { value: "supersecurepassword1" } });
        fireEvent.change(screen.getByPlaceholderText(/Monday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Tuesday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Wednesday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Thursday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-22:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Friday \(HH:mm-HH:mm\)/i), { target: { value: "09:00-23:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Saturday \(HH:mm-HH:mm\)/i), { target: { value: "10:00-23:00" } });
        fireEvent.change(screen.getByPlaceholderText(/Sunday \(HH:mm-HH:mm\)/i), { target: { value: "10:00-21:00" } });
        fireEvent.change(screen.getByPlaceholderText(/City/i), { target: { value: "Bilbao" } });
        fireEvent.change(screen.getByPlaceholderText(/Province/i), { target: { value: "Bizkaia" } });
        fireEvent.change(screen.getByPlaceholderText(/Country/i), { target: { value: "Spain" } });
        fireEvent.change(screen.getByPlaceholderText(/Postal Code/i), { target: { value: "48001" } });
        fireEvent.change(screen.getByPlaceholderText(/Street Number/i), { target: { value: "12" } });
        fireEvent.change(screen.getByPlaceholderText(/Longitude/i), { target: { value: "-2.935" } });
        fireEvent.change(screen.getByPlaceholderText(/Latitude/i), { target: { value: "43.263" } });
        fireEvent.click(screen.getByLabelText(/Italian/i));

        fireEvent.click(screen.getByRole("button", { name: /Register restaurant/i }));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/api/restaurants/create",
                expect.objectContaining({ method: "POST" })
            );
        });
    });
});
