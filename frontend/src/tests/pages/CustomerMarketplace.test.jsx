import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import CustomerMarketplace from "../../pages/CustomerMarketplace"; 


const mockNavigate = jest.fn();

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

const mockRestaurants = [
  { id: 1, name: "Pizza Planet", description: "Best Italian pizza", rating: 4.8 },
  { id: 2, name: "Burger Joint", description: "American style smash burgers", rating: 4.5 },
  { id: 3, name: "Sushi Master", description: "Fresh Japanese sushi", rating: 4.9 },
];

describe("CustomerMarketplace Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockRestaurants),
      })
    );
  });

  test("loads and displays restaurants from the API on mount", async () => {
    render(<CustomerMarketplace />);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledTimes(1);
    });


    expect(await screen.findByText("Pizza Planet")).toBeInTheDocument();
    expect(screen.getByText("Burger Joint")).toBeInTheDocument();
    expect(screen.getByText("Sushi Master")).toBeInTheDocument();
  });

  test("filters the restaurant list when typing in the search bar", async () => {
    render(<CustomerMarketplace />);

    await screen.findByText("Pizza Planet");


    const searchInput = screen.getByPlaceholderText(/search/i);

    fireEvent.change(searchInput, { target: { value: "Burger" } });

    expect(screen.getByText("Burger Joint")).toBeInTheDocument();
    expect(screen.queryByText("Pizza Planet")).not.toBeInTheDocument();
    expect(screen.queryByText("Sushi Master")).not.toBeInTheDocument();
  });

  test("navigates to the restaurant menu when a restaurant card/button is clicked", async () => {
    render(<CustomerMarketplace />);

  
    await screen.findByText("Pizza Planet");

    const pizzaRestaurantCard = screen.getByText("Pizza Planet");
    fireEvent.click(pizzaRestaurantCard);

    
    expect(mockNavigate).toHaveBeenCalledWith("/restaurants/1");
  });

  test("shows an empty state or message if no restaurants match the filter", async () => {
    render(<CustomerMarketplace />);
    await screen.findByText("Pizza Planet");

    const searchInput = screen.getByPlaceholderText(/search/i);

    fireEvent.change(searchInput, { target: { value: "Tacos" } });


    expect(screen.queryByText("Pizza Planet")).not.toBeInTheDocument();
    expect(screen.getByText(/no restaurants found/i)).toBeInTheDocument();
  });
});