import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import Home from "../../pages/Home";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
  }), { virtual: true });

global.fetch = jest.fn();
global.alert = jest.fn();

describe("Home Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test("renders the home page correctly with title and main buttons", () => {
    render(<Home />);

    expect(screen.getByText(/DELIVERING HAPPINESS/i)).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: /JustOrder/i })).toBeInTheDocument();

    expect(screen.getByText(/Register your restaurant/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Log in/i })).toBeInTheDocument();
  });

  test("toggles the login dropdown when 'Log in' is clicked", () => {
    render(<Home />);

    expect(screen.queryByRole("button", { name: "Customer" })).not.toBeInTheDocument();

    const loginTriggerButton = screen.getByRole("button", { name: /Log in/i });
    fireEvent.click(loginTriggerButton);

    expect(screen.getByRole("button", { name: "Customer" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Rider" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Restaurant" })).toBeInTheDocument();
  });

  test("switches login form inputs dynamically based on selected tab", () => {
    render(<Home />);

    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));

    const emailInput = screen.getByPlaceholderText(/Enter your Email/i);
    expect(emailInput).toBeInTheDocument();
    expect(emailInput.type).toBe("email");

    fireEvent.click(screen.getByRole("button", { name: "Rider" }));

    expect(screen.queryByPlaceholderText(/Enter your Email/i)).not.toBeInTheDocument();
    const dniInput = screen.getByPlaceholderText(/Enter your DNI/i);
    expect(dniInput).toBeInTheDocument();
    expect(dniInput.type).toBe("text");
  });

  test("handles the 501 Backend Bypass correctly for a Customer", async () => {

    global.fetch.mockResolvedValueOnce({
      status: 501,
      ok: false,
    });

    render(<Home />);

    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));

    fireEvent.change(screen.getByPlaceholderText(/Enter your Email/i), {
      target: { value: "test@student.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: "123456" },
    });

    fireEvent.click(screen.getAllByRole("button", { name: /Log In/i })[1]);

    await waitFor(() => {

      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/sessions/users",
        expect.any(Object)
      );

      expect(localStorage.getItem("token")).toBe("dummy-dev-token");

      expect(global.alert).toHaveBeenCalledWith(
        "Backend endpoint is NOT IMPLEMENTED yet. Bypassing for UI testing."
      );
 
      expect(mockNavigate).toHaveBeenCalledWith("/customer-marketplace");
    });
  });

  test("shows an error alert when login fails (e.g., 401 Unauthorized)", async () => {

    global.fetch.mockResolvedValueOnce({
      status: 401,
      ok: false,
    });

    render(<Home />);
    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));
    
    fireEvent.click(screen.getByRole("button", { name: "Rider" }));
    
    fireEvent.change(screen.getByPlaceholderText(/Enter your DNI/i), {
      target: { value: "12345678A" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: "wrongpassword" },
    });

    fireEvent.click(screen.getAllByRole("button", { name: /Log In/i })[1]);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/sessions/riders",
        expect.any(Object)
      );
      expect(global.alert).toHaveBeenCalledWith("Login failed. Please check your credentials.");
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });
});