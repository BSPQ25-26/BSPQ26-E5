import React from "react";
import { useCart } from "../store/CartContext";

const formatPrice = (value) => `${value.toFixed(2)} EUR`;

const Cart = () => {
  const {
    items,
    addToCart,
    decreaseQuantity,
    removeFromCart,
    clearCart,
    totalPrice,
    totalItems,
  } = useCart();

  return (
    <section className="card" aria-labelledby="cart-title">
      <h2 id="cart-title">Cart</h2>

      {items.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <>
          <ul style={{ listStyle: "none", margin: 0, padding: 0 }}>
            {items.map((item) => (
              <li
                key={item.id}
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr auto",
                  gap: "10px",
                  alignItems: "center",
                  padding: "10px 0",
                  borderBottom: "1px solid #dbe7f5",
                }}
              >
                <div>
                  <strong>{item.name}</strong>
                  <p style={{ margin: "6px 0 0", color: "#5d6b87" }}>
                    {item.quantity} x {formatPrice(item.price)}
                  </p>
                </div>
                <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => decreaseQuantity(item.id)}
                  >
                    -
                  </button>
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={() => addToCart(item)}
                  >
                    +
                  </button>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => removeFromCart(item.id)}
                  >
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>

          <div style={{ marginTop: "14px", display: "flex", justifyContent: "space-between" }}>
            <strong>Total ({totalItems} items)</strong>
            <strong>{formatPrice(totalPrice)}</strong>
          </div>

          <button
            type="button"
            className="btn btn-secondary"
            style={{ marginTop: "12px" }}
            onClick={clearCart}
          >
            Clear cart
          </button>
        </>
      )}
    </section>
  );
};

export default Cart;
