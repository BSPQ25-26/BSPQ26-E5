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
    <section className="cart-card" aria-labelledby="cart-title">
      <h3 id="cart-title">Your Cart</h3>

      {items.length === 0 ? (
        <div className="cart-empty">
          <div className="cart-empty-icon">📭</div>
          <p>Your cart is empty.</p>
          <p style={{ fontSize: "12px", margin: "4px 0 0" }}>
            Add dishes from the menu to get started
          </p>
        </div>
      ) : (
        <>
          <ul className="cart-items-list">
            {items.map((item) => (
              <li key={item.id} className="cart-item">
                <div className="cart-item-header">
                  <p className="cart-item-name">{item.name}</p>
                  <button
                    type="button"
                    className="cart-item-remove"
                    onClick={() => removeFromCart(item.id)}
                    title="Remove from cart"
                  >
                    Remove
                  </button>
                </div>
                <p className="cart-item-price">
                  {item.quantity} × {formatPrice(item.price)} = <strong>{formatPrice(item.price * item.quantity)}</strong>
                </p>
                <div className="cart-item-controls">
                  <button
                    type="button"
                    className="quantity-btn"
                    onClick={() => decreaseQuantity(item.id)}
                    title="Decrease quantity"
                  >
                    −
                  </button>
                  <span className="quantity-display">{item.quantity}</span>
                  <button
                    type="button"
                    className="quantity-btn"
                    onClick={() => addToCart(item)}
                    title="Increase quantity"
                  >
                    +
                  </button>
                </div>
              </li>
            ))}
          </ul>

          <div className="cart-summary">
            <div className="summary-row">
              <span>Subtotal:</span>
              <span>{formatPrice(totalPrice)}</span>
            </div>
            <div className="summary-row">
              <span>Items:</span>
              <span>{totalItems}</span>
            </div>
            <div className="summary-row total">
              <span>Total:</span>
              <span>{formatPrice(totalPrice)}</span>
            </div>
          </div>

          <button
            type="button"
            className="clear-cart-btn"
            onClick={clearCart}
            title="Remove all items from cart"
          >
            Clear Cart
          </button>
        </>
      )}
    </section>
  );
};

export default Cart;
