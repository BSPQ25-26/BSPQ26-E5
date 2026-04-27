import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import Cart from "../components/Cart";
import { useCart } from "../store/CartContext";
import "../assets/css/Checkout.css";

const formatPrice = (value) => `${value.toFixed(2)} EUR`;

const ORDER_CODE_STORAGE_KEY = "justorder:verificationCodes";

const readVerificationCodes = () => {
  try {
    const rawValue = localStorage.getItem(ORDER_CODE_STORAGE_KEY);
    return rawValue ? JSON.parse(rawValue) : {};
  } catch {
    return {};
  }
};

const storeVerificationCode = (orderId, secretCode) => {
  if (!orderId || !secretCode) {
    return;
  }

  const currentCodes = readVerificationCodes();
  currentCodes[String(orderId)] = secretCode;
  localStorage.setItem(ORDER_CODE_STORAGE_KEY, JSON.stringify(currentCodes));
};

const readLoggedInCustomer = () => {
  try {
    const userType = localStorage.getItem("userType");
    const rawUser = localStorage.getItem("user");
    if (userType !== "customer" || !rawUser) {
      return null;
    }
    const user = JSON.parse(rawUser);
    return user && user.id ? user : null;
  } catch {
    return null;
  }
};

const CheckoutPage = () => {
  const navigate = useNavigate();
  const { items, clearCart, totalPrice } = useCart();
  const [loggedInCustomer, setLoggedInCustomer] = useState(null);
  const [paymentToken, setPaymentToken] = useState("mock-payment-token");
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Load the logged-in customer from localStorage on mount.
  useEffect(() => {
    setLoggedInCustomer(readLoggedInCustomer());
  }, []);

  const dishIds = useMemo(
    () => items.flatMap((item) => Array.from({ length: item.quantity }, () => item.id)),
    [items]
  );

  const handleCheckout = async () => {
    setErrorMessage("");

    if (!loggedInCustomer) {
      setErrorMessage("You must be logged in as a customer to place an order.");
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
      customerId: loggedInCustomer.id,
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
            `Checkout failed with status ${response.status}. Check your cart or payment data.`
        );
      }

      clearCart();
      storeVerificationCode(data?.id, data?.secretCode);
      navigate("/orders/confirmation", {
        state: {
          order: data,
        },
      });
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
        <p>Review your cart and complete your purchase</p>
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

      <div style={{ maxWidth: '500px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <Cart />

        <div className="payment-card">
          <h3>Payment</h3>

          {loggedInCustomer ? (
            <p style={{ margin: '0 0 16px', color: '#333' }}>
              Ordering as <strong>{loggedInCustomer.name || loggedInCustomer.email}</strong>
            </p>
          ) : (
            <p style={{ margin: '0 0 16px', color: '#b00020' }}>
              You are not logged in. Please log in to place an order.
            </p>
          )}

          <div className="payment-form">
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
              disabled={isLoading || !loggedInCustomer}
            >
              {isLoading ? "Processing..." : "Pay & Confirm Order"}
            </button>
          </div>

          {errorMessage && (
            <div className="message-box message-error" role="alert">
              <span>{errorMessage}</span>
            </div>
          )}
        </div>
      </div>
    </main>
  );
};

export default CheckoutPage;