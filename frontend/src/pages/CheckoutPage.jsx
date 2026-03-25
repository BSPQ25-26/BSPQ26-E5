import React, { useMemo, useState } from "react";
import Cart from "../components/Cart";
import { useCart } from "../store/CartContext";
import "../assets/css/Checkout.css";

const MOCK_DISHES = [
  { id: 1, name: "Four Cheese Pizza", price: 23.0 },
  { id: 2, name: "Grilled Salmon", price: 25.0 },
  { id: 3, name: "Thai Tofu Bowl", price: 19.5 },
];

const formatPrice = (value) => `${value.toFixed(2)} EUR`;

const CheckoutPage = () => {
  const { items, addToCart, clearCart, totalPrice } = useCart();
  const [customerId, setCustomerId] = useState("1");
  const [paymentToken, setPaymentToken] = useState("mock-payment-token");
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const dishIds = useMemo(
    () => items.flatMap((item) => Array.from({ length: item.quantity }, () => item.id)),
    [items]
  );

  /**
   * Sends the checkout payload expected by the backend.
   * A valid request requires a positive customerId, at least one dish id,
   * and a non-empty payment token.
   */
  const handleCheckout = async () => {
    setSuccessMessage("");
    setErrorMessage("");

    const parsedCustomerId = Number(customerId);
    if (!parsedCustomerId || parsedCustomerId <= 0) {
      setErrorMessage("Enter a valid customerId.");
      return;
    }

    if (dishIds.length === 0) {
      setErrorMessage("Your cart is empty.");
      return;
    }

    if (!paymentToken.trim()) {
      setErrorMessage("paymentToken is required.");
      return;
    }

    const payload = {
      customerId: parsedCustomerId,
      dishIds,
      totalPrice,
      clientTotal: totalPrice,
      paymentToken,
    };

    setIsLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/orders/checkout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      const text = await response.text();
      const data = text ? JSON.parse(text) : null;

      if (!response.ok) {
        throw new Error(
          data?.message ||
          `Checkout failed with status ${response.status}. Check customerId, dishes, or payment data.`
        );
      }

      clearCart();
      setSuccessMessage(
        `Order confirmed. ID: ${data?.id ?? "N/A"}. Secret code: ${data?.secretCode ?? "N/A"}.`
      );
    } catch (error) {
      setErrorMessage(error.message || "Checkout could not be completed.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="checkout-page">
      <div className="checkout-header">
        <h1>🛒 Shopping Cart & Checkout</h1>
        <p>Select dishes from our menu, review your order, and complete your purchase</p>
      </div>

      <div className="checkout-steps">
        <div className="step active">
          <div className="step-number">1</div>
          <span>Browse Menu</span>
        </div>
        <div className="step-connector"></div>
        <div className={`step ${items.length > 0 ? "active" : ""}`}>
          <div className="step-number">2</div>
          <span>Review Cart</span>
        </div>
        <div className="step-connector"></div>
        <div className={`step ${isLoading ? "active" : ""}`}>
          <div className="step-number">3</div>
          <span>Confirm Payment</span>
        </div>
      </div>

      <div className="checkout-container">
        {/* Left: Menu Items */}
        <div className="checkout-dishes-section">
          <div className="dishes-header">
            <h2>Available Dishes</h2>
            <p>Select items to add to your cart</p>
          </div>

          <div className="dishes-grid">
            {MOCK_DISHES.map((dish) => (
              <div key={dish.id} className="dish-card">
                <div className="dish-card-info">
                  <p className="dish-name">{dish.name}</p>
                  <p className="dish-price">{formatPrice(dish.price)}</p>
                </div>
                <button type="button" onClick={() => addToCart(dish)}>
                  Add to Cart
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Right: Cart & Payment */}
        <div className="checkout-sidebar">
          <Cart />

          <div className="payment-card">
            <h3>Payment</h3>

            <div className="payment-form">
              <div className="form-group">
                <label htmlFor="customerIdInput">Customer ID</label>
                <input
                  id="customerIdInput"
                  type="number"
                  min="1"
                  value={customerId}
                  onChange={(event) => setCustomerId(event.target.value)}
                  placeholder="e.g., 1"
                />
              </div>

              <div className="form-group">
                <label htmlFor="paymentTokenInput">Payment Token</label>
                <input
                  id="paymentTokenInput"
                  type="text"
                  value={paymentToken}
                  onChange={(event) => setPaymentToken(event.target.value)}
                  placeholder="e.g., card-token-123"
                />
              </div>

              <div className="total-display">
                <span className="label">Total:</span>
                <span className="amount">{formatPrice(totalPrice)}</span>
              </div>

              <button
                type="button"
                className="checkout-btn"
                onClick={handleCheckout}
                disabled={isLoading}
              >
                {isLoading ? "Processing..." : "Pay & Confirm Order"}
              </button>
            </div>

            {successMessage && (
              <div className="message-box message-success" role="status">
                <span>{successMessage}</span>
              </div>
            )}

            {errorMessage && (
              <div className="message-box message-error" role="alert">
                <span>{errorMessage}</span>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
};

export default CheckoutPage;
