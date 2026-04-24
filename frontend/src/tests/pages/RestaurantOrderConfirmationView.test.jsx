import React from "react";
import { act, fireEvent, render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import RestaurantOrderConfirmationView from "../../pages/RestaurantOrderConfirmationView";

const mockNavigate = jest.fn();
const mockUseLocation = jest.fn();

jest.mock("react-router-dom", () => ({
  Link: ({ children, to, className }) => (
    <a href={to} className={className}>
      {children}
    </a>
  ),
  useNavigate: () => mockNavigate,
  useLocation: () => mockUseLocation(),
}), { virtual: true });

describe("RestaurantOrderConfirmationView", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    Object.assign(navigator, {
      clipboard: {
        writeText: jest.fn().mockResolvedValue(undefined),
      },
    });
  });

  test("renders verification code and order data when checkout state is present", () => {
    mockUseLocation.mockReturnValue({
      state: {
        order: {
          id: 42,
          status: "Pending",
          totalPrice: 23,
          secretCode: "123456",
        },
      },
    });

    render(<RestaurantOrderConfirmationView />);

    expect(screen.getByText(/Show this verification code at delivery/i)).toBeInTheDocument();
    expect(screen.getByText("123456")).toBeInTheDocument();
    expect(screen.getByText("#42")).toBeInTheDocument();
    expect(screen.getByText("Pending")).toBeInTheDocument();
  });

  test("copies verification code when copy button is clicked", async () => {
    mockUseLocation.mockReturnValue({
      state: {
        order: {
          id: 9,
          status: "Pending",
          totalPrice: 14,
          secretCode: "999000",
        },
      },
    });

    render(<RestaurantOrderConfirmationView />);

    await act(async () => {
      fireEvent.click(screen.getByRole("button", { name: /copy code/i }));
    });

    expect(navigator.clipboard.writeText).toHaveBeenCalledWith("999000");
  });

  test("shows fallback screen when there is no checkout state", () => {
    mockUseLocation.mockReturnValue({ state: null });

    render(<RestaurantOrderConfirmationView />);

    expect(screen.getByText(/Order confirmation unavailable/i)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Go to checkout/i })).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: /View my orders/i }));
    expect(mockNavigate).toHaveBeenCalledWith("/orders");
  });
});
