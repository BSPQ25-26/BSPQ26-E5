import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import MenuEditor from "../../pages/MenuEditor";
import { fetchThroughNode } from "../utils/fetchThroughNode";


const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    Link: ({ children, ...props }) => <a {...props}>{children}</a>,
    useNavigate: () => mockNavigate, 
}), { virtual: true });

describe("MenuEditor", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn(fetchThroughNode);
        
   
        localStorage.setItem("user", JSON.stringify({ id: 1, name: "Test Rest" }));
    });

    test("loads allergens and dishes from live backend", async () => {
        const allergenResponse = await fetchThroughNode("http://localhost:8080/api/allergens");
        const menuResponse = await fetchThroughNode("http://localhost:8080/api/restaurants/1/menu");

        expect(allergenResponse.ok).toBe(true);
        expect(menuResponse.ok).toBe(true);

        const allergenData = await allergenResponse.json();
        const menuData = await menuResponse.json();

        render(<MenuEditor />);

        expect(screen.getByRole("heading", { name: /Menu Editor/i })).toBeInTheDocument();

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/api/allergens",
                expect.objectContaining({ method: "GET" })
            );
            expect(global.fetch).toHaveBeenCalledWith(
                "http://localhost:8080/api/restaurants/1/menu",
                expect.objectContaining({ method: "GET" })
            );
        });

        if (Array.isArray(menuData) && menuData.length > 0) {
            expect(await screen.findByText(menuData[0].name)).toBeInTheDocument();
        } else {
            expect(await screen.findByText(/No dishes yet/i)).toBeInTheDocument();
        }

        if (Array.isArray(allergenData) && allergenData.length === 0) {
            expect(screen.getByText(/No allergens available/i)).toBeInTheDocument();
        }
    });
});