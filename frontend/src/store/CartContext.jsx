import React, { createContext, useContext, useMemo, useState, useCallback } from "react";

const CartContext = createContext(undefined);

/**
 * Stores cart items by dish id and exposes the operations used by the
 * checkout flow. Enforces the rule that a cart can only contain dishes from
 * one restaurant at a time.
 */
export const CartProvider = ({ children }) => {
  const [items, setItems] = useState([]);
  const [restaurantId, setRestaurantId] = useState(null);
  const [restaurantName, setRestaurantName] = useState(null);

  /**
   * Tries to add a dish to the cart.
   * Returns { success: true } on success.
   * Returns { success: false, reason: 'different_restaurant', ... } if the dish
   * belongs to a restaurant different from the one currently in the cart.
   */
  const addToCart = useCallback((dish) => {
    if (!dish || dish.id == null) {
      return { success: false, reason: "invalid_dish" };
    }

    const cartHasItems = items.length > 0;
    const dishIsFromDifferentRestaurant =
      cartHasItems &&
      restaurantId != null &&
      dish.restaurantId !== restaurantId;

    if (dishIsFromDifferentRestaurant) {
      return {
        success: false,
        reason: "different_restaurant",
        currentRestaurantName: restaurantName,
        attemptedRestaurantName: dish.restaurantName,
      };
    }

    const existing = items.find((item) => item.id === dish.id);
    const nextItems = existing
      ? items.map((item) =>
          item.id === dish.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      : [
          ...items,
          {
            id: dish.id,
            name: dish.name,
            price: Number(dish.price) || 0,
            quantity: 1,
          },
        ];

    setItems(nextItems);

    // Capture the restaurant the first time an item is added to an empty cart.
    if (!cartHasItems) {
      setRestaurantId(dish.restaurantId ?? null);
      setRestaurantName(dish.restaurantName ?? null);
    }

    return { success: true };
  }, [items, restaurantId, restaurantName]);

  /**
   * Clears the cart and replaces it with a single dish. Used when the user
   * confirms switching restaurants.
   */
  const replaceCartWith = useCallback((dish) => {
    if (!dish || dish.id == null) {
      return { success: false, reason: "invalid_dish" };
    }

    setItems([
      {
        id: dish.id,
        name: dish.name,
        price: Number(dish.price) || 0,
        quantity: 1,
      },
    ]);
    setRestaurantId(dish.restaurantId ?? null);
    setRestaurantName(dish.restaurantName ?? null);

    return { success: true };
  }, []);

  const removeFromCart = useCallback((dishId) => {
    const next = items.filter((item) => item.id !== dishId);
    setItems(next);
    if (next.length === 0) {
      setRestaurantId(null);
      setRestaurantName(null);
    }
  }, [items]);

  const decreaseQuantity = useCallback((dishId) => {
    const next = items
      .map((item) =>
        item.id === dishId
          ? { ...item, quantity: item.quantity - 1 }
          : item
      )
      .filter((item) => item.quantity > 0);
    setItems(next);
    if (next.length === 0) {
      setRestaurantId(null);
      setRestaurantName(null);
    }
  }, [items]);

  const clearCart = () => {
    setItems([]);
    setRestaurantId(null);
    setRestaurantName(null);
  };

  // Test helper: seed the cart synchronously with items and restaurant info.
  const seedCart = useCallback((seededItems, restId = null, restName = null) => {
    if (!Array.isArray(seededItems)) return;
    setItems(seededItems.map(i => ({ id: i.id, name: i.name, price: Number(i.price) || 0, quantity: i.quantity || 1 })));
    setRestaurantId(restId);
    setRestaurantName(restName);
  }, []);

  const totalPrice = useMemo(
    () =>
      items.reduce(
        (accumulator, item) => accumulator + item.price * item.quantity,
        0
      ),
    [items]
  );

  const totalItems = useMemo(
    () => items.reduce((accumulator, item) => accumulator + item.quantity, 0),
    [items]
  );

  const value = {
    items,
    restaurantId,
    restaurantName,
    addToCart,
    replaceCartWith,
    removeFromCart,
    decreaseQuantity,
    clearCart,
    seedCart,
    totalPrice,
    totalItems,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};