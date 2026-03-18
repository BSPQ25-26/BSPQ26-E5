import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import RegisterRider from "../../pages/RegisterRider";
import { registerRider } from "../../api/authApi";

jest.mock("../../api/authApi", () => ({
    registerRider: jest.fn(),
}));

const fillRequiredFields = () => {
    fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: "Rider One" } });
    fireEvent.change(screen.getByPlaceholderText(/DNI/i), { target: { value: "12345678A" } });
    fireEvent.change(screen.getByPlaceholderText(/Phone Number/i), { target: { value: "+34 600 123 123" } });
    fireEvent.change(screen.getByPlaceholderText(/^Email$/i), { target: { value: "rider@test.com" } });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), { target: { value: "supersecurepassword1" } });

    fireEvent.change(screen.getByPlaceholderText(/City/i), { target: { value: "Bilbao" } });
    fireEvent.change(screen.getByPlaceholderText(/Province/i), { target: { value: "Bizkaia" } });
    fireEvent.change(screen.getByPlaceholderText(/Country/i), { target: { value: "Spain" } });
    fireEvent.change(screen.getByPlaceholderText(/Postal Code/i), { target: { value: "48001" } });
    fireEvent.change(screen.getByPlaceholderText(/Street Number/i), { target: { value: "12" } });
    fireEvent.change(screen.getByPlaceholderText(/Longitude/i), { target: { value: "-2.935" } });
    fireEvent.change(screen.getByPlaceholderText(/Latitude/i), { target: { value: "43.263" } });
};

describe("RegisterRider", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test("renders rider registration title", () => {
        render(<RegisterRider />);
        expect(screen.getByRole("heading", { name: /Register Rider/i, level: 2 })).toBeInTheDocument();
    });

    test("submits payload and shows success message", async () => {
        registerRider.mockResolvedValueOnce(null);

        render(<RegisterRider />);
        fillRequiredFields();

        fireEvent.click(screen.getByRole("button", { name: /Register Rider/i }));

        await waitFor(() => {
            expect(registerRider).toHaveBeenCalledTimes(1);
        });

        expect(registerRider).toHaveBeenCalledWith(
            expect.objectContaining({
                name: "Rider One",
                dni: "12345678A",
                phoneNumber: "+34 600 123 123",
                email: "rider@test.com",
                password: "supersecurepassword1",
                starterPoint: expect.objectContaining({
                    city: "Bilbao",
                    province: "Bizkaia",
                    country: "Spain",
                    postalCode: "48001",
                    number: "12",
                    longitude: -2.935,
                    latitude: 43.263,
                }),
            })
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(/Rider registered successfully/i);
    });

    test("shows error message when API fails", async () => {
        registerRider.mockRejectedValueOnce(new Error("Backend error"));

        render(<RegisterRider />);
        fillRequiredFields();

        fireEvent.click(screen.getByRole("button", { name: /Register Rider/i }));

        expect(await screen.findByRole("alert")).toHaveTextContent(/Backend error/i);
    });
});