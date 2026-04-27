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

describe("CustomerInformationPage", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = fetchThroughNode;
    });

    test("renders live dashboard data from the backend", async () => {
        const response = await fetchThroughNode(DASHBOARD_URL);
        expect(response.ok).toBe(true);

        const dashboard = await response.json();

        // Ensure the component treats a customer as logged in so it renders the dashboard
        localStorage.setItem("userType", "customer");
        localStorage.setItem(
            "user",
            JSON.stringify({ id: dashboard.customerId, name: dashboard.customerName || "Test Customer" })
        );

        render(<CustomerInformationPage />);

        expect(await screen.findByRole("heading", { name: /Information Dashboard/i })).toBeInTheDocument();
        await waitFor(() => {
            expect(screen.getByText(`Customer: ${dashboard.customerName}`)).toBeInTheDocument();
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
