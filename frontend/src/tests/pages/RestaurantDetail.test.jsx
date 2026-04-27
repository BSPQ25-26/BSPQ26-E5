import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import RestaurantDetail from "../../pages/RestaurantDetail";
import { CartProvider } from "../../store/CartContext";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();
let mockRouteRestaurantId = "1";

jest.mock("react-router-dom", () => ({
    useParams: () => ({ id: mockRouteRestaurantId }),
    useNavigate: () => mockNavigate,
    Link: ({ children, to }) => <a href={to}>{children}</a>,
}), { virtual: true });

describe("RestaurantDetail", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("loads restaurant and menu from the live backend", async () => {
        const restaurantsResponse = await fetchThroughNode("http://localhost:8080/api/restaurants/search");
        expect(restaurantsResponse.ok).toBe(true);

        const restaurants = await restaurantsResponse.json();
        expect(restaurants.length).toBeGreaterThan(0);

        const targetRestaurant = restaurants[0];
        mockRouteRestaurantId = String(targetRestaurant.id);

        const menuResponse = await fetchThroughNode(`http://localhost:8080/api/restaurants/${mockRouteRestaurantId}/menu`);
        expect(menuResponse.ok).toBe(true);
        const menu = await menuResponse.json();

        render(
            <CartProvider>
                <RestaurantDetail />
            </CartProvider>
        );

        expect(await screen.findByText(targetRestaurant.name)).toBeInTheDocument();

        if (menu.length > 0) {
            expect(await screen.findByText(menu[0].name)).toBeInTheDocument();

            const addButtons = await screen.findAllByRole("button", { name: "+ Add" });
            fireEvent.click(addButtons[0]);

            expect(await screen.findByRole("status")).toBeInTheDocument();
        } else {
            expect(await screen.findByText(/No dishes available yet/i)).toBeInTheDocument();
        }
    });
});
