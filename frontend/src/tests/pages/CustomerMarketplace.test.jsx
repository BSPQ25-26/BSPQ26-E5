import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import CustomerMarketplace from "../../pages/CustomerMarketplace";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();
const RESTAURANTS_URL = "http://localhost:8080/api/restaurants/search";

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => (
        <a
            href={to}
            onClick={(e) => {
                e.preventDefault();
                mockNavigate(to);
            }}
        >
            {children}
        </a>
    ),
    useNavigate: () => mockNavigate,
}), { virtual: true });

describe("CustomerMarketplace", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
    });

    test("loads restaurants from the live backend", async () => {
        const response = await fetchThroughNode(RESTAURANTS_URL);
        expect(response.ok).toBe(true);

        const restaurants = await response.json();

        render(<CustomerMarketplace />);

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(RESTAURANTS_URL);
        });

        if (restaurants.length > 0) {
            const restaurantNameNodes = await screen.findAllByText(restaurants[0].name);
            expect(
                restaurantNameNodes.some((node) =>
                    node.closest("a")?.getAttribute("href") === `/restaurants/${restaurants[0].id}`
                )
            ).toBe(true);
        } else {
            expect(await screen.findByText(/No restaurants found/i)).toBeInTheDocument();
        }
    });
});
