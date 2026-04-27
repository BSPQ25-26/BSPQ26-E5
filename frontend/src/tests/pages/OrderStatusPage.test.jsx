import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import OrderStatusPage from "../../pages/OrderStatusPage";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();
const ORDERS_URL = "http://localhost:8080/api/customers/1/orders";

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("OrderStatusPage", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("renders live orders from backend", async () => {
        const response = await fetchThroughNode(ORDERS_URL);
        expect(response.ok).toBe(true);

        await response.json();

        render(<OrderStatusPage />);

        expect(await screen.findByRole("heading", { name: /My Orders/i })).toBeInTheDocument();

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                ORDERS_URL,
                expect.objectContaining({
                    method: "GET",
                    headers: { "Content-Type": "application/json" },
                })
            );
        });

        await waitFor(() => {
            expect(screen.queryByText(/Loading your orders/i)).not.toBeInTheDocument();
        });

        const hasOrders = screen.queryAllByText(/Order #\d+/i).length > 0;
        const hasEmptyState = !!screen.queryByText(/No orders yet/i);

        expect(hasOrders || hasEmptyState).toBe(true);
    });
});
