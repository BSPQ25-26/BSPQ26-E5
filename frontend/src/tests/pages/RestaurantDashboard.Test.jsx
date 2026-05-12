import React from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import RestaurantDashboard from "../../pages/RestaurantDashboard";
import { getMenuByRestaurantId, getRestaurantDashboard } from "../../api/authApi";


jest.mock("../../api/authApi", () => ({
    getMenuByRestaurantId: jest.fn(),
    getRestaurantDashboard: jest.fn(),
    rejectRestaurantOrder: jest.fn()
}));

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("RestaurantDashboard", () => {
    const mockMenu = [
        { id: 1, name: "Pizza Margherita", description: "Classic pizza", price: 10.50 }
    ];

    const mockDashboardData = {
        totalOrders: 5,
        recentOrders: [
            { id: 100, status: "Pending", totalPrice: 20.0, createdAt: "2026-05-01T12:00:00Z" },
            { id: 101, status: "Delivered", totalPrice: 15.0, createdAt: "2026-05-01T11:00:00Z" }
        ]
    };

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem("token", "fake-token");
        localStorage.setItem("user", JSON.stringify({ id: 1, name: "La Marina Bistro" }));

        getMenuByRestaurantId.mockResolvedValue(mockMenu);
        getRestaurantDashboard.mockResolvedValue(mockDashboardData);
    });

    test("renders Live Orders by default and shows pending orders", async () => {
        render(<RestaurantDashboard />);

        
        expect(await screen.findByRole("heading", { name: /Live Orders/i })).toBeInTheDocument();
        
        
        expect(await screen.findByText(/Order #100/i)).toBeInTheDocument();
    });

    test("switches to Menu Management tab", async () => {
        render(<RestaurantDashboard />);

    
        const menuTabButton = screen.getByRole("button", { name: /Menu Management/i });
        fireEvent.click(menuTabButton);

      
        expect(await screen.findByText(/Pizza Margherita/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Edit Full Menu/i })).toBeInTheDocument();
    });

    test("switches to Order History tab and shows all recent orders", async () => {
        render(<RestaurantDashboard />);


        const historyTabButton = screen.getByRole("button", { name: /Order History/i });
        fireEvent.click(historyTabButton);


        expect(await screen.findByText(/#100/i)).toBeInTheDocument();
        expect(screen.getByText(/#101/i)).toBeInTheDocument();
        expect(screen.getByText(/Delivered/i)).toBeInTheDocument();
    });

    test("redirects to home if no user is logged in", () => {
        localStorage.clear(); 
        render(<RestaurantDashboard />);

        expect(mockNavigate).toHaveBeenCalledWith("/");
    });
});