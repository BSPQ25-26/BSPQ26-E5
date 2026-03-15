import React, { createContext, useContext, useMemo, useState } from "react";

const CartContext = createContext(undefined);

/**
 * Stores cart items grouped by dish id and exposes the operations used by the
 * checkout flow.
 */
export const CartProvider = ({ children }) => {
  const [items, setItems] = useState([]);

  const addToCart = (dish) => {
    if (!dish || dish.id == null) {
      return;
    }

    setItems((prevItems) => {
      const existing = prevItems.find((item) => item.id === dish.id);
      if (existing) {
        return prevItems.map((item) =>
          item.id === dish.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }

      return [
        ...prevItems,
        {
          id: dish.id,
          name: dish.name,
          price: Number(dish.price) || 0,
          quantity: 1,
        },
      ];
    });
  };

  const removeFromCart = (dishId) => {
    setItems((prevItems) => prevItems.filter((item) => item.id !== dishId));
  };

  const decreaseQuantity = (dishId) => {
    setItems((prevItems) =>
      prevItems
        .map((item) =>
          item.id === dishId
            ? { ...item, quantity: item.quantity - 1 }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const clearCart = () => {
    setItems([]);
  };

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
    addToCart,
    removeFromCart,
    decreaseQuantity,
    clearCart,
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
