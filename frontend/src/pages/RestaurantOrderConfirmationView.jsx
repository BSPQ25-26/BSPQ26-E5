import React, { useMemo, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "../assets/css/OrderConfirmation.css";

const formatPrice = (value) => {
  if (value === undefined || value === null || Number.isNaN(Number(value))) {
    return "-";
  }
  return `${Number(value).toFixed(2)} EUR`;
};

function RestaurantOrderConfirmationView() {
  const location = useLocation();
  const navigate = useNavigate();
  const [copied, setCopied] = useState(false);

  const orderData = location.state?.order ?? null;

  const verificationCode = useMemo(() => {
    const code = orderData?.secretCode;
    return typeof code === "string" ? code : null;
  }, [orderData]);

  const handleCopyCode = async () => {
    if (!verificationCode) {
      return;
    }

    try {
      await navigator.clipboard.writeText(verificationCode);
      setCopied(true);
      window.setTimeout(() => setCopied(false), 1800);
    } catch {
      setCopied(false);
    }
  };

  if (!orderData) {
    return (
      <main className="confirmation-page">
        <section className="confirmation-shell confirmation-shell-empty">
          <h1>Order confirmation unavailable</h1>
          <p>
            This page needs checkout data. Place an order first to see your verification code.
          </p>
          <div className="confirmation-actions">
            <Link className="btn-secondary" to="/checkout">
              Go to checkout
            </Link>
            <button className="btn-primary" type="button" onClick={() => navigate("/orders")}>
              View my orders
            </button>
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="confirmation-page">
      <section className="confirmation-shell">
        <header className="confirmation-header">
          <p className="eyebrow">Order placed successfully</p>
          <h1>Show this verification code at delivery</h1>
          <p className="subtext">
            The rider must enter this code to validate your purchase and mark the order as delivered.
          </p>
        </header>

        <article className="code-card" aria-label="verification code card">
          <p className="code-label">Verification code</p>
          <p className="code-value">{verificationCode ?? "N/A"}</p>
          <button
            type="button"
            className="btn-primary"
            onClick={handleCopyCode}
            disabled={!verificationCode}
          >
            {copied ? "Code copied" : "Copy code"}
          </button>
        </article>

        <section className="order-summary" aria-label="order summary">
          <h2>Order summary</h2>
          <div className="summary-grid">
            <div>
              <span className="label">Order ID</span>
              <span className="value">#{orderData.id ?? "-"}</span>
            </div>
            <div>
              <span className="label">Status</span>
              <span className="value">{orderData.status ?? "Pending"}</span>
            </div>
            <div>
              <span className="label">Total</span>
              <span className="value">{formatPrice(orderData.totalPrice)}</span>
            </div>
          </div>
        </section>

        <footer className="confirmation-actions">
          <Link className="btn-secondary" to="/customer-marketplace">
            Continue shopping
          </Link>
          <button className="btn-primary" type="button" onClick={() => navigate("/orders")}>
            Go to my orders
          </button>
        </footer>
      </section>
    </main>
  );
}

export default RestaurantOrderConfirmationView;
