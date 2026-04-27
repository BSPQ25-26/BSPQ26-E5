import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { within } from "@testing-library/react";
import CustomerInformationPage from "../../pages/CustomerInformationPage";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();
const DASHBOARD_URL = "http://localhost:8080/api/customers/1/dashboard";

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

const mockDashboard = {
    customerId: 1,
    customerName: "Test Customer",
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
        localStorage.clear();
        localStorage.setItem("userType", "customer");
        localStorage.setItem("user", JSON.stringify({ id: 1, name: "Test Customer" }));
    });

    test("loads dashboard for logged-in customer on mount", async () => {
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
        expect(screen.getByText(/Customer: Test Customer/i)).toBeInTheDocument();
    });

    test("shows login-required message when customer is not logged in", async () => {
        localStorage.clear();

        await act(async () => {
            render(<CustomerInformationPage />);
        });

        expect(getCustomerDashboard).not.toHaveBeenCalled();
        expect(screen.getByText(/You must be logged in as a customer/i)).toBeInTheDocument();
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

        const dashboard = await response.json();

        render(<CustomerInformationPage />);

        expect(await screen.findByRole("heading", { name: /Information Dashboard/i })).toBeInTheDocument();
        await waitFor(() => {
            expect(screen.getByText(`Customer ID: ${dashboard.customerId}`)).toBeInTheDocument();
            expect(screen.getByText("Total orders")).toBeInTheDocument();
            expect(screen.getByText(`${Number(dashboard.totalSpent).toFixed(2)} EUR`)).toBeInTheDocument();
            expect(screen.getByText(`${Number(dashboard.totalRefunded).toFixed(2)} EUR`)).toBeInTheDocument();

            if (dashboard.recentOrders.length > 0) {
                expect(screen.getByText(/Recent orders/i)).toBeInTheDocument();
            }
        });

        const statCards = screen.getAllByRole("article");

        const totalOrdersCard = statCards.find((card) => within(card).queryByText("Total orders"));
        const activeOrdersCard = statCards.find((card) => within(card).queryByText("Active orders"));
        const deliveredOrdersCard = statCards.find((card) => within(card).queryByText("Delivered orders"));
        const cancelledOrdersCard = statCards.find((card) => within(card).queryByText("Cancelled orders"));

        expect(totalOrdersCard).toBeTruthy();
        expect(activeOrdersCard).toBeTruthy();
        expect(deliveredOrdersCard).toBeTruthy();
        expect(cancelledOrdersCard).toBeTruthy();

        expect(within(totalOrdersCard).getByText(String(dashboard.totalOrders))).toBeInTheDocument();
        expect(within(activeOrdersCard).getByText(String(dashboard.activeOrders))).toBeInTheDocument();
        expect(within(deliveredOrdersCard).getByText(String(dashboard.deliveredOrders))).toBeInTheDocument();
        expect(within(cancelledOrdersCard).getByText(String(dashboard.cancelledOrders))).toBeInTheDocument();
    });
});
