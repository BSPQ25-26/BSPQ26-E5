import React from "react";
import { act, render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import CustomerInformationPage from "../../pages/CustomerInformationPage";
import { getCustomerDashboard } from "../../api/authApi";

const mockNavigate = jest.fn();

jest.mock("../../api/authApi", () => ({
    getCustomerDashboard: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

const mockDashboard = {
    customerId: 1,
    totalOrders: 7,
    activeOrders: 4,
    cancelledOrders: 2,
    deliveredOrders: 1,
    totalSpent: 120.5,
    totalRefunded: 25,
    recentOrders: [
        {
            id: 15,
            status: "Pending",
            createdAt: "2026-04-22T10:45:00",
            totalPrice: 18.5,
        },
    ],
};

describe("CustomerInformationPage", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test("loads dashboard for customer 1 on mount", async () => {
        getCustomerDashboard.mockResolvedValueOnce(mockDashboard);

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(getCustomerDashboard).toHaveBeenCalledWith(1);
    });

    test("renders dashboard title and summary cards", async () => {
        getCustomerDashboard.mockResolvedValueOnce(mockDashboard);

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(screen.getByRole("heading", { name: /Information Dashboard/i })).toBeInTheDocument();
        expect(screen.getByText("Total orders")).toBeInTheDocument();
        expect(screen.getByText("Active orders")).toBeInTheDocument();
        expect(screen.getByText("Delivered orders")).toBeInTheDocument();
        expect(screen.getByText("Cancelled orders")).toBeInTheDocument();
        expect(screen.getByText("Total spent")).toBeInTheDocument();
        expect(screen.getByText("Total refunded")).toBeInTheDocument();
    });

    test("shows recent orders section when orders exist", async () => {
        getCustomerDashboard.mockResolvedValueOnce(mockDashboard);

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(screen.getByText(/Recent orders/i)).toBeInTheDocument();
        expect(screen.getByText(/Order #15/i)).toBeInTheDocument();
        expect(screen.getByText("Pending")).toBeInTheDocument();
    });

    test("shows empty message when there are no recent orders", async () => {
        getCustomerDashboard.mockResolvedValueOnce({ ...mockDashboard, recentOrders: [] });

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(screen.getByText(/No recent orders to display yet/i)).toBeInTheDocument();
    });

    test("shows error message when dashboard request fails", async () => {
        getCustomerDashboard.mockRejectedValueOnce(new Error("Network error"));

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(screen.getByText(/Could not load your information dashboard/i)).toBeInTheDocument();
    });

    test("shows loading state while waiting for dashboard", () => {
        getCustomerDashboard.mockImplementation(() => new Promise(() => {}));

        render(<CustomerInformationPage />);

        expect(screen.getByText(/Loading your dashboard/i)).toBeInTheDocument();
    });
});
