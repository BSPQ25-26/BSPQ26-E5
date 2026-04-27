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

  test("switches login tabs and keeps email input", () => {
    render(<Home />);

    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));

    const customerTab = screen.getByRole("button", { name: "Customer" });
    const riderTab = screen.getByRole("button", { name: "Rider" });
    const emailInput = screen.getByPlaceholderText(/Enter your Email/i);

    expect(customerTab).toHaveClass("active");
    expect(emailInput).toBeInTheDocument();
    expect(emailInput.type).toBe("email");

    fireEvent.click(riderTab);

    expect(riderTab).toHaveClass("active");
    expect(screen.getByPlaceholderText(/Enter your Email/i)).toBeInTheDocument();
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

    fireEvent.change(screen.getByPlaceholderText(/Enter your Email/i), {
      target: { value: "rider@example.com" },
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

  test("stores token, userType and user in localStorage on successful customer login", async () => {
    const fakeCustomer = { id: 7, name: "Alice Example", email: "alice@example.com" };

    global.fetch.mockResolvedValueOnce({
      status: 200,
      ok: true,
      json: async () => ({ token: "real-token-abc", customer: fakeCustomer }),
    });

    render(<Home />);

    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));
    fireEvent.change(screen.getByPlaceholderText(/Enter your Email/i), {
      target: { value: "alice@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: "somepassword" },
    });

    fireEvent.click(screen.getAllByRole("button", { name: /Log In/i })[1]);

    await waitFor(() => {
      expect(localStorage.getItem("token")).toBe("real-token-abc");
      expect(localStorage.getItem("userType")).toBe("customer");
      expect(JSON.parse(localStorage.getItem("user"))).toEqual(fakeCustomer);
      expect(mockNavigate).toHaveBeenCalledWith("/customer-marketplace");
    });
  });

  test("sends the user type in the login request body", async () => {
    global.fetch.mockResolvedValueOnce({
      status: 200,
      ok: true,
      json: async () => ({ token: "t", rider: { id: 2, name: "Bob" } }),
    });

    render(<Home />);

    fireEvent.click(screen.getByRole("button", { name: /Log in/i }));
    fireEvent.click(screen.getByRole("button", { name: "Rider" }));

    fireEvent.change(screen.getByPlaceholderText(/Enter your Email/i), {
      target: { value: "bob@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: "pw" },
    });

    fireEvent.click(screen.getAllByRole("button", { name: /Log In/i })[1]);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/sessions/riders",
        expect.objectContaining({
          method: "POST",
          body: expect.stringContaining('"type":"rider"'),
        })
      );
      expect(localStorage.getItem("userType")).toBe("rider");
    });
  });
});