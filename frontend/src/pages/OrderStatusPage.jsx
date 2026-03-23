import React, { useState, useEffect } from "react";
import { getCustomerOrders } from "../api/authApi";

const STATUS_COLOURS = {
    Pending:           { background: "#fff8e1", color: "#92680a", border: "#f5c842" },
    Confirmed:         { background: "#e8f5e9", color: "#2e7d32", border: "#66bb6a" },
    Preparing:         { background: "#e3f2fd", color: "#1565c0", border: "#42a5f5" },
    "Out for Delivery":{ background: "#ede7f6", color: "#4527a0", border: "#ab47bc" },
    Delivered:         { background: "#e8f5e9", color: "#1b5e20", border: "#43a047" },
    Cancelled:         { background: "#fce4ec", color: "#b71c1c", border: "#ef9a9a" },
};

const formatPrice = (value) => `${Number(value).toFixed(2)} EUR`;

const formatDate = (dateString) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

const StatusBadge = ({ status }) => {
    const style = STATUS_COLOURS[status] || { background: "#f5f5f5", color: "#555", border: "#ccc" };
    return (
        <span
            style={{
                display: "inline-block",
                padding: "3px 10px",
                borderRadius: "20px",
                fontSize: "0.8rem",
                fontWeight: 700,
                background: style.background,
                color: style.color,
                border: `1px solid ${style.border}`,
            }}
        >
            {status}
        </span>
    );
};

const OrderCard = ({ order }) => {
    const isCancelled = order.status === "Cancelled";

    return (
        <article
            style={{
                border: isCancelled ? "1px solid #ef9a9a" : "1px solid #dbe7f5",
                borderRadius: "14px",
                padding: "18px 20px",
                background: isCancelled ? "#fff8f8" : "#fff",
                marginBottom: "14px",
            }}
        >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: "8px" }}>
                <div>
                    <span style={{ fontWeight: 700, fontSize: "0.95rem", color: "#1a2740" }}>
                        Order #{order.id}
                    </span>
                    <span style={{ marginLeft: "10px", color: "#5d6b87", fontSize: "0.85rem" }}>
                        {formatDate(order.createdAt)}
                    </span>
                </div>
                <StatusBadge status={order.status} />
            </div>

            <p style={{ margin: "10px 0 0", color: "#1a2740", fontWeight: 600 }}>
                Total: {formatPrice(order.totalPrice)}
            </p>

            {order.dishes && order.dishes.length > 0 && (
                <ul style={{ margin: "8px 0 0", paddingLeft: "18px", color: "#5d6b87", fontSize: "0.9rem" }}>
                    {order.dishes.map((dish) => (
                        <li key={dish.id}>
                            {dish.name} — {formatPrice(dish.price)}
                        </li>
                    ))}
                </ul>
            )}

            {isCancelled && (
                <div
                    role="alert"
                    style={{
                        marginTop: "14px",
                        padding: "12px 16px",
                        background: "#fce4ec",
                        border: "1px solid #ef9a9a",
                        borderRadius: "10px",
                    }}
                >
                    <p style={{ margin: 0, fontWeight: 700, color: "#b71c1c", fontSize: "0.95rem" }}>
                        ⚠️ Order cancelled — refund issued
                    </p>
                    <p style={{ margin: "4px 0 0", color: "#b71c1c", fontSize: "0.88rem" }}>
                        No available rider could complete this delivery. A full refund of{" "}
                        <strong>{formatPrice(order.totalPrice)}</strong> has been issued.
                    </p>
                    {order.rejectionReason && (
                        <p style={{ margin: "6px 0 0", color: "#7f1d1d", fontSize: "0.85rem", fontStyle: "italic" }}>
                            Reason: "{order.rejectionReason}"
                        </p>
                    )}
                </div>
            )}
        </article>
    );
};

const OrderStatusPage = () => {
    const [orders, setOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        const fetchOrders = async () => {
            setIsLoading(true);
            setErrorMessage("");

            try {
                const CUSTOMER_ID = 1; // 👈 hardcode

                const data = await getCustomerOrders(CUSTOMER_ID);
                setOrders(data);
            } catch (err) {
                setErrorMessage(err.message || "Could not fetch orders.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchOrders();
    }, []);

    const cancelledCount = orders.filter((o) => o.status === "Cancelled").length;
    const activeCount = orders.length - cancelledCount;

    return (
        <main className="home" style={{ maxWidth: "720px" }}>
            <section className="home-hero">
                <p className="home-kicker">My Orders</p>
                <h1>Order Status</h1>
                <p>Here are your recent orders and their current status.</p>
            </section>

            <section style={{ marginTop: "18px" }}>
                {isLoading ? (
                    <p>Loading orders...</p>
                ) : errorMessage ? (
                    <p style={{ color: "#b42318", fontWeight: 700 }}>{errorMessage}</p>
                ) : orders.length === 0 ? (
                    <div className="card">
                        <p style={{ color: "#5d6b87", margin: 0 }}>
                            No orders found.
                        </p>
                    </div>
                ) : (
                    <>
                        <div
                            className="card"
                            style={{ marginBottom: "14px", display: "flex", gap: "24px", flexWrap: "wrap" }}
                        >
                            <div>
                                <p style={{ margin: 0, fontSize: "0.85rem", color: "#5d6b87" }}>Total orders</p>
                                <p style={{ margin: "2px 0 0", fontWeight: 700, fontSize: "1.3rem", color: "#1a2740" }}>
                                    {orders.length}
                                </p>
                            </div>
                            <div>
                                <p style={{ margin: 0, fontSize: "0.85rem", color: "#5d6b87" }}>Active</p>
                                <p style={{ margin: "2px 0 0", fontWeight: 700, fontSize: "1.3rem", color: "#1565c0" }}>
                                    {activeCount}
                                </p>
                            </div>
                            {cancelledCount > 0 && (
                                <div>
                                    <p style={{ margin: 0, fontSize: "0.85rem", color: "#5d6b87" }}>Refunded</p>
                                    <p style={{ margin: "2px 0 0", fontWeight: 700, fontSize: "1.3rem", color: "#b71c1c" }}>
                                        {cancelledCount}
                                    </p>
                                </div>
                            )}
                        </div>

                        {orders.map((order) => (
                            <OrderCard key={order.id} order={order} />
                        ))}
                    </>
                )}
            </section>
        </main>
    );
};

export default OrderStatusPage;