import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import RegisterCustomer from "../../pages/RegisterCustomer";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("RegisterCustomer", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("renders registration page", () => {
        render(<RegisterCustomer />);
        expect(screen.getByText(/Register Customer/i)).toBeInTheDocument();
    });

    test("submits customer registration to live backend", async () => {
        const uniqueEmail = `customer-${Date.now()}@example.com`;

        render(<RegisterCustomer />);

        fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: "Integration Customer" } });
        fireEvent.change(screen.getByPlaceholderText(/Email/i), { target: { value: uniqueEmail } });
        fireEvent.change(screen.getByPlaceholderText(/Password/i), { target: { value: "supersecurepassword1" } });
        fireEvent.change(screen.getByPlaceholderText(/Phone/i), { target: { value: "600000000" } });
        fireEvent.change(screen.getByPlaceholderText(/Age/i), { target: { value: "25" } });
        fireEvent.change(screen.getByPlaceholderText(/DNI/i), { target: { value: `INT${Date.now()}` } });
        fireEvent.change(screen.getByPlaceholderText(/City/i), { target: { value: "Bilbao" } });
        fireEvent.change(screen.getByPlaceholderText(/Province/i), { target: { value: "Bizkaia" } });
        fireEvent.change(screen.getByPlaceholderText(/Country/i), { target: { value: "Spain" } });
        fireEvent.change(screen.getByPlaceholderText(/Postal Code/i), { target: { value: "48001" } });
        fireEvent.change(screen.getByPlaceholderText(/Street Number/i), { target: { value: "12" } });
        fireEvent.change(screen.getByPlaceholderText(/Longitude/i), { target: { value: "-2.935" } });
        fireEvent.change(screen.getByPlaceholderText(/Latitude/i), { target: { value: "43.263" } });

        fireEvent.click(screen.getByRole("button", { name: /Register/i }));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/api/customers/create",
                expect.objectContaining({ method: "POST" })
            );
        });
    });
});
