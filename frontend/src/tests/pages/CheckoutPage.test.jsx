import React from "react";
import { render, screen, fireEvent, waitFor, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import CheckoutPage from "../../pages/CheckoutPage";
import { CartProvider, useCart } from "../../store/CartContext";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
  Link: ({ children, to }) => <a href={to}>{children}</a>,
}), { virtual: true });

const loggedInCustomer = { id: 42, name: "Alice Example", email: "alice@example.com" };

const sampleDish = {
  id: 8,
  name: "Beef Taco",
  price: 4.5,
  restaurantId: 5,
  restaurantName: "Taco Loco",
};

const CartSeeder = ({ dish }) => {
  const { addToCart } = useCart();
  React.useEffect(() => {
    if (dish) addToCart(dish);
  }, []);
  return null;
};

const renderCheckout = async ({ seedDish } = {}) => {
  await act(async () => {
    render(
      <CartProvider>
        {seedDish && <CartSeeder dish={seedDish} />}
        <CheckoutPage />
      </CartProvider>
    );
  });
};

describe("CheckoutPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    global.fetch = jest.fn();
  });

  test("shows a warning and disables the checkout button when no customer is logged in", async () => {
    await renderCheckout({ seedDish: sampleDish });

    expect(
      screen.getByText(/You are not logged in/i)
    ).toBeInTheDocument();

    const checkoutButton = screen.getByRole("button", { name: /Pay & Confirm Order/i });
    expect(checkoutButton).toBeDisabled();
  });

  test("shows the logged-in customer's name when a customer is in localStorage", async () => {
    localStorage.setItem("userType", "customer");
    localStorage.setItem("user", JSON.stringify(loggedInCustomer));

    await renderCheckout({ seedDish: sampleDish });

    expect(screen.getByText(/Ordering as/i)).toBeInTheDocument();
    expect(screen.getByText(loggedInCustomer.name)).toBeInTheDocument();
  });

  test("does NOT auto-fill when the logged-in user is a rider, not a customer", async () => {
    localStorage.setItem("userType", "rider");
    localStorage.setItem("user", JSON.stringify({ id: 99, name: "Not A Customer" }));

    await renderCheckout({ seedDish: sampleDish });

    expect(screen.getByText(/You are not logged in/i)).toBeInTheDocument();
    expect(screen.queryByText(/Ordering as/i)).not.toBeInTheDocument();
  });

  test("sends the logged-in customer's id in the checkout request", async () => {
    localStorage.setItem("userType", "customer");
    localStorage.setItem("user", JSON.stringify(loggedInCustomer));

    global.fetch.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: async () =>
        JSON.stringify({ id: 501, secretCode: "123456", status: "Pending" }),
    });

    await renderCheckout({ seedDish: sampleDish });

    const checkoutButton = screen.getByRole("button", { name: /Pay & Confirm Order/i });

    await act(async () => {
      fireEvent.click(checkoutButton);
    });

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/api/orders/checkout",
        expect.objectContaining({
          method: "POST",
          body: expect.stringContaining(`"customerId":${loggedInCustomer.id}`),
        })
      );
    });

    expect(mockNavigate).toHaveBeenCalledWith(
      "/orders/confirmation",
      expect.objectContaining({
        state: expect.objectContaining({
          order: expect.objectContaining({ id: 501 }),
        }),
      })
    );
  });

  test("shows an error message when the checkout API fails", async () => {
    localStorage.setItem("userType", "customer");
    localStorage.setItem("user", JSON.stringify(loggedInCustomer));

    global.fetch.mockResolvedValueOnce({
      ok: false,
      status: 400,
      text: async () => JSON.stringify({ message: "Invalid payment" }),
    });

    await renderCheckout({ seedDish: sampleDish });

    const checkoutButton = screen.getByRole("button", { name: /Pay & Confirm Order/i });

    await act(async () => {
      fireEvent.click(checkoutButton);
    });

    await waitFor(() => {
      expect(screen.getByRole("alert")).toHaveTextContent(/Invalid payment/i);
    });

    expect(mockNavigate).not.toHaveBeenCalled();
  });

  test("shows an error when the cart is empty", async () => {
    localStorage.setItem("userType", "customer");
    localStorage.setItem("user", JSON.stringify(loggedInCustomer));

    await renderCheckout();

    const checkoutButton = screen.getByRole("button", { name: /Pay & Confirm Order/i });

    await act(async () => {
      fireEvent.click(checkoutButton);
    });

    expect(screen.getByRole("alert")).toHaveTextContent(/cart is empty/i);
    expect(global.fetch).not.toHaveBeenCalled();
  });
});