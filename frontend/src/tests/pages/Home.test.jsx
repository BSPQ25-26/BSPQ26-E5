import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import Home from "../../pages/Home";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("Home", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        global.alert = jest.fn();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("renders the home page shell", () => {
        render(<Home />);

        expect(screen.getByRole("heading", { name: /JustOrder/i })).toBeInTheDocument();
        expect(screen.getByText(/DELIVERING HAPPINESS/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Log in/i })).toBeInTheDocument();
    });

    test("submits login against live backend endpoint", async () => {
        render(<Home />);

        fireEvent.click(screen.getByRole("button", { name: /Log in/i }));

        fireEvent.change(screen.getByPlaceholderText(/Enter your Email/i), {
            target: { value: `integration-${Date.now()}@example.com` },
        });
        fireEvent.change(screen.getByPlaceholderText(/Password/i), {
            target: { value: "wrongpassword" },
        });

        fireEvent.click(screen.getAllByRole("button", { name: /Log In/i })[1]);

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/sessions/users",
                expect.objectContaining({ method: "POST" })
            );
        });
    });
});
