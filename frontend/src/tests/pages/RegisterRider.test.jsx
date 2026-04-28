import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import RegisterRider from "../../pages/RegisterRider";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("RegisterRider", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("renders rider registration page", () => {
        render(<RegisterRider />);
        expect(screen.getByRole("heading", { name: /Register Rider/i, level: 2 })).toBeInTheDocument();
    });

    test("submits rider registration to live backend", async () => {
        const uniqueEmail = `rider-${Date.now()}@example.com`;

        render(<RegisterRider />);

        fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: "Integration Rider" } });
        fireEvent.change(screen.getByPlaceholderText(/DNI/i), { target: { value: `RID${Date.now()}` } });
        fireEvent.change(screen.getByPlaceholderText(/Phone Number/i), { target: { value: "+34 600 123 123" } });
        fireEvent.change(screen.getByPlaceholderText(/^Email$/i), { target: { value: uniqueEmail } });
        fireEvent.change(screen.getByPlaceholderText(/Password/i), { target: { value: "supersecurepassword1" } });
        fireEvent.change(screen.getByPlaceholderText(/City/i), { target: { value: "Bilbao" } });
        fireEvent.change(screen.getByPlaceholderText(/Province/i), { target: { value: "Bizkaia" } });
        fireEvent.change(screen.getByPlaceholderText(/Country/i), { target: { value: "Spain" } });
        fireEvent.change(screen.getByPlaceholderText(/Postal Code/i), { target: { value: "48001" } });
        fireEvent.change(screen.getByPlaceholderText(/Street Number/i), { target: { value: "12" } });
        fireEvent.change(screen.getByPlaceholderText(/Longitude/i), { target: { value: "-2.935" } });
        fireEvent.change(screen.getByPlaceholderText(/Latitude/i), { target: { value: "43.263" } });

        fireEvent.click(screen.getByRole("button", { name: /Register Rider/i }));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/api/riders/create",
                expect.objectContaining({ method: "POST" })
            );
        });
    });
});
