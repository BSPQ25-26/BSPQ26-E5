import React from "react";
import { render, screen, waitFor, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import OrderStatusPage from "../../pages/OrderStatusPage";
import { getCustomerOrders } from "../../api/authApi";

const mockNavigate = jest.fn();

jest.mock("../../api/authApi", () => ({
    getCustomerOrders: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

const mockOrders = [
    {
        id: 1,
        status: "Pending",
        totalPrice: 23.0,
        createdAt: "2026-03-15T20:25:08.968431",
        dishes: [],
        rejectionReason: null,
    },
    {
        id: 2,
        status: "Cancelled",
        totalPrice: 14.0,
        createdAt: "2026-03-15T20:25:08.970665",
        dishes: [],
        rejectionReason: "Area unreachable due to road closure",
    },
];

describe("OrderStatusPage", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    test("renders the page title on mount", async () => {
        getCustomerOrders.mockResolvedValueOnce([]);
        await act(async () => { render(<OrderStatusPage />); });
        expect(screen.getByRole("heading", { name: /My Orders/i })).toBeInTheDocument();
    });

    test("loads orders for customer 1 automatically on mount", async () => {
        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(getCustomerOrders).toHaveBeenCalledWith(1);
    });

    test("displays all orders after loading", async () => {
        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText(/Order #1/i)).toBeInTheDocument();
        expect(screen.getByText(/Order #2/i)).toBeInTheDocument();
    });

    test("shows correct status badge for each order", async () => {
        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText("Pending")).toBeInTheDocument();
        expect(screen.getByText("Cancelled")).toBeInTheDocument();
    });

    test("shows verification code under the status when stored locally", async () => {
        localStorage.setItem(
            "justorder:verificationCodes",
            JSON.stringify({ "1": "123456" })
        );

        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText(/Verification code:/i)).toBeInTheDocument();
        expect(screen.getByText("123456")).toBeInTheDocument();
    });

    test("shows refund notice for cancelled orders", async () => {
        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText(/Order cancelled — refund issued/i)).toBeInTheDocument();
        expect(screen.getByText(/Area unreachable due to road closure/i)).toBeInTheDocument();
    });

    test("does not show refund notice for non-cancelled orders", async () => {
        getCustomerOrders.mockResolvedValueOnce([mockOrders[0]]);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.queryByText(/Order cancelled — refund issued/i)).not.toBeInTheDocument();
    });

    test("shows summary with total, active and refunded counts", async () => {
        getCustomerOrders.mockResolvedValueOnce(mockOrders);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText("Total orders")).toBeInTheDocument();
        expect(screen.getByText("Active")).toBeInTheDocument();
        expect(screen.getByText("Refunded")).toBeInTheDocument();
    });

    test("shows empty state when customer has no orders", async () => {
        getCustomerOrders.mockResolvedValueOnce([]);
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText(/No orders yet/i)).toBeInTheDocument();
        expect(screen.getByRole("link", { name: /checkout/i })).toBeInTheDocument();
    });

    test("shows error message when API call fails", async () => {
        getCustomerOrders.mockRejectedValueOnce(new Error("Network error"));
        await act(async () => { render(<OrderStatusPage />); });

        expect(screen.getByText(/Could not load your orders/i)).toBeInTheDocument();
    });

    test("shows loading state before orders arrive", () => {
        getCustomerOrders.mockImplementation(() => new Promise(() => {}));
        render(<OrderStatusPage />);

        expect(screen.getByText(/Loading your orders/i)).toBeInTheDocument();
    });
});