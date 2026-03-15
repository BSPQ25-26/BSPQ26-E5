import React, { useMemo, useState } from "react";
import Cart from "../components/Cart";
import { useCart } from "../store/CartContext";

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

  const handleCheckout = async () => {
    setSuccessMessage("");
    setErrorMessage("");

    const parsedCustomerId = Number(customerId);
    if (!parsedCustomerId || parsedCustomerId <= 0) {
      setErrorMessage("Introduce un customerId valido.");
      return;
    }

    if (dishIds.length === 0) {
      setErrorMessage("El carrito esta vacio.");
      return;
    }

    if (!paymentToken.trim()) {
      setErrorMessage("paymentToken es obligatorio.");
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
            `Checkout fallo con codigo ${response.status}. Revisa customerId, dishes o pago.`
        );
      }

      clearCart();
      setSuccessMessage(
        `Pedido confirmado. ID: ${data?.id ?? "N/A"}. Codigo secreto: ${data?.secretCode ?? "N/A"}.`
      );
    } catch (error) {
      setErrorMessage(error.message || "No se pudo completar el checkout.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="home" style={{ maxWidth: "980px" }}>
      <section className="home-hero">
        <p className="home-kicker">Checkout</p>
        <h1>Shopping Cart & Checkout</h1>
        <p>Selecciona platos, revisa tu carrito y confirma el pedido contra el backend.</p>
      </section>

      <section className="home-grid" style={{ marginTop: "18px" }}>
        <article className="card" style={{ gridColumn: "span 2" }}>
          <h2>Platos de prueba</h2>
          <p style={{ color: "#5d6b87" }}>
            Esta lista es temporal para Sprint 1 y te permite probar el flujo completo de carrito.
          </p>

          <div style={{ display: "grid", gap: "10px" }}>
            {MOCK_DISHES.map((dish) => (
              <div
                key={dish.id}
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  border: "1px solid #dbe7f5",
                  borderRadius: "12px",
                  padding: "10px 12px",
                }}
              >
                <div>
                  <strong>{dish.name}</strong>
                  <p style={{ margin: "6px 0 0", color: "#5d6b87" }}>{formatPrice(dish.price)}</p>
                </div>
                <button type="button" className="btn btn-primary" onClick={() => addToCart(dish)}>
                  Anadir al carrito
                </button>
              </div>
            ))}
          </div>
        </article>

        <Cart />
      </section>

      <section className="card" style={{ marginTop: "18px" }}>
        <h2>Pago simulado</h2>

        <div style={{ display: "grid", gap: "10px", maxWidth: "420px" }}>
          <label htmlFor="customerIdInput">Customer ID</label>
          <input
            id="customerIdInput"
            type="number"
            min="1"
            value={customerId}
            onChange={(event) => setCustomerId(event.target.value)}
          />

          <label htmlFor="paymentTokenInput">Payment Token</label>
          <input
            id="paymentTokenInput"
            type="text"
            value={paymentToken}
            onChange={(event) => setPaymentToken(event.target.value)}
          />

          <p style={{ margin: 0, color: "#5d6b87" }}>Total a enviar: {formatPrice(totalPrice)}</p>

          <button
            type="button"
            className="btn btn-primary"
            onClick={handleCheckout}
            disabled={isLoading}
          >
            {isLoading ? "Procesando..." : "Pagar y Confirmar"}
          </button>
        </div>

        {successMessage && (
          <p role="status" style={{ marginTop: "12px", color: "#0f766e", fontWeight: 700 }}>
            {successMessage}
          </p>
        )}

        {errorMessage && (
          <p role="alert" style={{ marginTop: "12px", color: "#b42318", fontWeight: 700 }}>
            {errorMessage}
          </p>
        )}
      </section>
    </main>
  );
};

export default CheckoutPage;
