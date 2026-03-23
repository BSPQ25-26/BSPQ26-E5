import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import MenuEditor from "../../pages/MenuEditor";
import {
  getAllergens,
  getMenuByRestaurantId,
  createDish,
  updateDish,
  deleteDish,
} from "../../api/authApi";

jest.mock("../../api/authApi", () => ({
  getAllergens: jest.fn(),
  getMenuByRestaurantId: jest.fn(),
  createDish: jest.fn(),
  updateDish: jest.fn(),
  deleteDish: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  Link: ({ children, ...props }) => <a {...props}>{children}</a>,
}), { virtual: true });

const renderMenuEditor = () => render(<MenuEditor />);

const initialDishes = [
  {
    id: 1,
    name: "Margherita",
    description: "Tomato, mozzarella and basil",
    price: 11,
    allergenNames: ["Gluten", "Milk"],
  },
  {
    id: 2,
    name: "Caesar Salad",
    description: "Romaine lettuce with dressing",
    price: 9.5,
    allergenNames: [],
  },
];

describe("MenuEditor", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    getAllergens.mockResolvedValue(["Gluten", "Milk", "Nuts"]);
    getMenuByRestaurantId.mockResolvedValue(initialDishes);
    createDish.mockResolvedValue(null);
    updateDish.mockResolvedValue(null);
    deleteDish.mockResolvedValue(true);
  });

  test("loads allergens and dishes on mount", async () => {
    renderMenuEditor();

    expect(screen.getByText(/Loading allergens/i)).toBeInTheDocument();
    expect(screen.getByText(/Loading dishes/i)).toBeInTheDocument();

    expect(await screen.findByText("Margherita")).toBeInTheDocument();
    expect(screen.getByText(/11\.00 EUR/i)).toBeInTheDocument();
    expect(screen.getByText(/Gluten, Milk/i)).toBeInTheDocument();

    expect(getAllergens).toHaveBeenCalledTimes(1);
    expect(getMenuByRestaurantId).toHaveBeenCalledWith(1);
  });

  test("shows empty states when no data is available", async () => {
    getAllergens.mockResolvedValueOnce([]);
    getMenuByRestaurantId.mockResolvedValueOnce([]);

    renderMenuEditor();

    expect(await screen.findByText(/No allergens available/i)).toBeInTheDocument();
    expect(screen.getByText(/No dishes yet/i)).toBeInTheDocument();
  });

  test("shows allergen and dishes loading errors", async () => {
    getAllergens.mockRejectedValueOnce(new Error("Allergen service down"));
    getMenuByRestaurantId.mockRejectedValueOnce(new Error("Menu service down"));

    renderMenuEditor();

    expect(
      await screen.findByText(/Error loading allergens: Allergen service down/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Error loading dishes: Menu service down/i)
    ).toBeInTheDocument();
  });

  test("validates required fields before submitting", async () => {
    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.click(screen.getByRole("button", { name: /Create dish/i }));
    expect(screen.getByText(/Dish name is required/i)).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText(/^Name$/i), {
      target: { value: "Pizza" },
    });
    fireEvent.click(screen.getByRole("button", { name: /Create dish/i }));
    expect(screen.getByText(/Description is required/i)).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText(/Description/i), {
      target: { value: "Nice and simple" },
    });
    fireEvent.change(screen.getByLabelText(/Price/i), {
      target: { value: "0" },
    });
    fireEvent.click(screen.getByRole("button", { name: /Create dish/i }));
    expect(screen.getByText(/Price must be greater than 0/i)).toBeInTheDocument();

    expect(createDish).not.toHaveBeenCalled();
  });

  test("creates a dish, normalizes payload and reloads menu", async () => {
    const refreshedMenu = [
      ...initialDishes,
      {
        id: 3,
        name: "Truffle Pasta",
        description: "Fresh pasta with truffle cream",
        price: 15.2,
        allergenNames: ["Milk"],
      },
    ];

    getMenuByRestaurantId
      .mockResolvedValueOnce(initialDishes)
      .mockResolvedValueOnce(refreshedMenu);

    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.change(screen.getByLabelText(/^Name$/i), {
      target: { value: "  Truffle Pasta  " },
    });
    fireEvent.change(screen.getByLabelText(/Description/i), {
      target: { value: "  Fresh pasta with truffle cream  " },
    });
    fireEvent.change(screen.getByLabelText(/Price/i), {
      target: { value: "15.199" },
    });

    fireEvent.click(screen.getByLabelText("Milk"));

    fireEvent.click(screen.getByRole("button", { name: /Create dish/i }));

    await waitFor(() => {
      expect(createDish).toHaveBeenCalledTimes(1);
    });

    expect(createDish).toHaveBeenCalledWith(1, {
      name: "Truffle Pasta",
      description: "Fresh pasta with truffle cream",
      price: 15.2,
      allergenNames: ["Milk"],
    });

    await waitFor(() => {
      expect(getMenuByRestaurantId).toHaveBeenCalledTimes(2);
    });

    expect(await screen.findByText(/Dish created successfully/i)).toBeInTheDocument();
    expect(screen.getByText("Truffle Pasta")).toBeInTheDocument();
  });

  test("shows error message when createDish fails", async () => {
    createDish.mockRejectedValueOnce(new Error("Create failed"));

    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.change(screen.getByLabelText(/^Name$/i), {
      target: { value: "New dish" },
    });
    fireEvent.change(screen.getByLabelText(/Description/i), {
      target: { value: "Test description" },
    });
    fireEvent.change(screen.getByLabelText(/Price/i), {
      target: { value: "10" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Create dish/i }));

    expect(await screen.findByText(/Error: Create failed/i)).toBeInTheDocument();
  });

  test("enters edit mode, updates a dish and exits edit mode", async () => {
    const refreshedMenu = [
      {
        id: 1,
        name: "Margherita Deluxe",
        description: "Tomato, mozzarella, basil and burrata",
        price: 12.5,
        allergenNames: ["Gluten", "Milk"],
      },
      initialDishes[1],
    ];

    getMenuByRestaurantId
      .mockResolvedValueOnce(initialDishes)
      .mockResolvedValueOnce(refreshedMenu);

    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.click(screen.getAllByRole("button", { name: /Edit/i })[0]);

    expect(screen.getByRole("heading", { name: /Edit dish/i })).toBeInTheDocument();
    expect(screen.getByDisplayValue("Margherita")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Cancel edit/i })).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText(/^Name$/i), {
      target: { value: "Margherita Deluxe" },
    });
    fireEvent.change(screen.getByLabelText(/Description/i), {
      target: { value: "Tomato, mozzarella, basil and burrata" },
    });
    fireEvent.change(screen.getByLabelText(/Price/i), {
      target: { value: "12.50" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Save changes/i }));

    await waitFor(() => {
      expect(updateDish).toHaveBeenCalledTimes(1);
    });

    expect(updateDish).toHaveBeenCalledWith(1, {
      name: "Margherita Deluxe",
      description: "Tomato, mozzarella, basil and burrata",
      price: 12.5,
      allergenNames: ["Gluten", "Milk"],
    });

    expect(await screen.findByText(/Dish updated successfully/i)).toBeInTheDocument();
    expect(screen.getByText("Margherita Deluxe")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: /New dish/i })).toBeInTheDocument();
  });

  test("cancel edit resets the form", async () => {
    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.click(screen.getAllByRole("button", { name: /Edit/i })[0]);

    const nameInput = screen.getByLabelText(/^Name$/i);
    fireEvent.change(nameInput, { target: { value: "Changed" } });

    fireEvent.click(screen.getByRole("button", { name: /Cancel edit/i }));

    expect(screen.getByRole("heading", { name: /New dish/i })).toBeInTheDocument();
    expect(nameInput).toHaveValue("");
  });

  test("deletes a dish and removes it from the list", async () => {
    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.click(screen.getAllByRole("button", { name: /Delete/i })[0]);

    await waitFor(() => {
      expect(deleteDish).toHaveBeenCalledWith(1);
    });

    expect(await screen.findByText(/Dish deleted successfully/i)).toBeInTheDocument();
    expect(screen.queryByText("Margherita")).not.toBeInTheDocument();
  });

  test("shows error message when delete fails", async () => {
    deleteDish.mockRejectedValueOnce(new Error("Cannot delete"));

    renderMenuEditor();

    await screen.findByText("Margherita");

    fireEvent.click(screen.getAllByRole("button", { name: /Delete/i })[0]);

    expect(
      await screen.findByText(/Error deleting dish: Cannot delete/i)
    ).toBeInTheDocument();
  });
});
