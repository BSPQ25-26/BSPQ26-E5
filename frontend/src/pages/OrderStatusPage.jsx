import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import cartImage from "../assets/images/Shopping cart.png";
import { getCustomerOrders } from "../api/authApi";
import "../assets/css/Home.css";
import "../assets/css/CustomerMarketplace.css";
import "../assets/css/OrderStatusPage.css";

const ORDER_CODE_STORAGE_KEY = "justorder:verificationCodes";

const STATUS_CLASS = {
    "Pending": "status-Pending",
    "Confirmed": "status-Confirmed",
    "Preparing": "status-Preparing",
    "Out for Delivery": "status-OutForDelivery",
    "Delivered": "status-Delivered",
    "Cancelled": "status-Cancelled",
};

const formatPrice = (value) => `${Number(value).toFixed(2)} €`;

const readVerificationCodes = () => {
    try {
        const rawValue = localStorage.getItem(ORDER_CODE_STORAGE_KEY);
        return rawValue ? JSON.parse(rawValue) : {};
    } catch {
        return {};
    }
};

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

const OrderCard = ({ order }) => {
    const isCancelled = order.status === "Cancelled";
    const statusClass = STATUS_CLASS[order.status] || "";
    const verificationCode = order.verificationCode;

    return (
        <article className={`order-card ${isCancelled ? "cancelled" : ""}`}>
            <div className="order-card-header">
                <div>
                    <span className="order-id">Order #{order.id}</span>
                    <span className="order-date">{formatDate(order.createdAt)}</span>
                </div>
                <div className="order-status-stack">
                    <span className={`status-badge ${statusClass}`}>{order.status}</span>
                    {verificationCode && (
                        <span className="verification-code-label">
                            Verification code: <strong>{verificationCode}</strong>
                        </span>
                    )}
                </div>
            </div>

            <div className="order-card-body">
                <p className="order-price">{formatPrice(order.totalPrice)}</p>

                {order.dishes && order.dishes.length > 0 && (
                    <ul className="order-dishes">
                        {order.dishes.map((dish) => (
                            <li key={dish.id} className="order-dish-item">
                                <span>{dish.name}</span>
                                <span>{formatPrice(dish.price)}</span>
                            </li>
                        ))}
                    </ul>
                )}

                {isCancelled && !order.timeline && (
                    <div className="refund-notice">
                        <p className="refund-notice-title">⚠️ Order cancelled — refund issued</p>
                        <p className="refund-notice-body">
                            No rider was available to complete your delivery.
                            A full refund of <strong>{formatPrice(order.totalPrice)}</strong> has
                            been issued to your original payment method.
                        </p>
                        {order.rejectionReason && (
                            <p className="refund-notice-reason">
                                Rider note: "{order.rejectionReason}"
                            </p>
                        )}
                    </div>
                )}

                {order.timeline && order.timeline.length > 0 && (
                    <div className="order-timeline">
                        <h4 style={{marginTop: '20px', marginBottom: '10px', fontSize: '1.1rem', color: '#444'}}>Order Timeline</h4>
                        <div style={{borderLeft: '2px solid #ccc', marginLeft: '10px', paddingLeft: '15px'}}>
                            {order.timeline.map((event, idx) => (
                                <div key={idx} style={{marginBottom: '15px', position: 'relative'}}>
                                    <div style={{
                                        position: 'absolute',
                                        left: '-21px',
                                        top: '4px',
                                        width: '10px',
                                        height: '10px',
                                        borderRadius: '50%',
                                        backgroundColor: event.statusOrEvent === 'Cancelled' || event.statusOrEvent === 'Rejected' ? '#ff4d4d' : '#00cc66'
                                    }}></div>
                                    <div style={{fontSize: '0.85rem', color: '#888'}}>{formatDate(event.timestamp)}</div>
                                    <div style={{fontWeight: 'bold', color: '#333'}}>{event.statusOrEvent}</div>
                                    {event.details && <div style={{fontSize: '0.9rem', color: '#555', marginTop: '2px'}}>{event.details}</div>}
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </article>
    );
};

function OrderStatusPage() {
    const [orders, setOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

    const navigate = useNavigate();
    const customerId = localStorage.getItem('customerId');
    
    // Auto-load orders on mount, no user input needed
    useEffect(() => {
        getCustomerOrders(customerId)
            .then((data) => {
                const verificationCodes = readVerificationCodes();
                const ordersWithCodes = data.map((order) => ({
                    ...order,
                    verificationCode: verificationCodes[String(order.id)] || order.secretCode || null,
                }));

                setOrders(ordersWithCodes);
            })
            .catch(() => setErrorMessage("Could not load your orders. Please try again later."))
            .finally(() => setIsLoading(false));
    }, [customerId]);

    const handleSignOut = () => {
        setIsProfileMenuOpen(false);
        navigate("/");
    };

    const cancelledCount = orders.filter((o) => o.status === "Cancelled").length;

    return (
        <main className="home-page">
            <section className="home-shell">

                {/* Navbar, same as CustomerMarketplace */}
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <Link to="/customer-marketplace" style={{ textDecoration: 'none', color: 'inherit' }}>
                            <h1 className="brand-title">JustOrder</h1>
                        </Link>
                    </div>

                    <div className="home-header-right">
                        <nav className="home-nav-links" aria-label="Main navigation">
                            <div className="profile-menu-container">
                                <button
                                    className="profile-avatar-btn"
                                    aria-label="User profile"
                                    onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                                >
                                    <img
                                        src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                                        alt="Profile Avatar"
                                        className="profile-avatar-img"
                                    />
                                </button>

                                {isProfileMenuOpen && (
                                    <div className="profile-dropdown">
                                        <Link
                                            to="/orders"
                                            className="dropdown-item"
                                            onClick={() => setIsProfileMenuOpen(false)}
                                        >
                                            My Orders
                                        </Link>
                                        <Link
                                            to="/customer/profile"
                                            className="dropdown-item"
                                            onClick={() => setIsProfileMenuOpen(false)}
                                        >
                                            Information
                                        </Link>
                                        <button className="dropdown-item sign-out" onClick={handleSignOut}>
                                            Sign out
                                        </button>
                                    </div>
                                )}
                            </div>
                        </nav>

                        <Link to="/checkout" className="cart-link" aria-label="Go to cart">
                            <img src={cartImage} alt="Shopping cart" className="cart-icon" />
                        </Link>
                    </div>
                </header>

                {/* Page content */}
                <div className="orders-content">

                    <div className="orders-header">
                        <h2>My Orders</h2>
                        <p>Your order history and refund status for any cancelled orders.</p>
                    </div>

                    {/* Loading state */}
                    {isLoading && (
                        <p style={{ color: "#888" }}>Loading your orders...</p>
                    )}

                    {/* Error state */}
                    {errorMessage && (
                        <p className="orders-error">{errorMessage}</p>
                    )}

                    {/* Orders */}
                    {!isLoading && !errorMessage && (
                        <>
                            {orders.length === 0 ? (
                                <div className="orders-empty">
                                    <h3>No orders yet</h3>
                                    <p>
                                        Head to the <Link to="/checkout">checkout</Link> to place your first order.
                                    </p>
                                </div>
                            ) : (
                                <>
                                    {/* Summary */}
                                    <div className="orders-summary">
                                        <div className="summary-stat">
                                            <span>Total orders</span>
                                            <span>{orders.length}</span>
                                        </div>
                                        <div className="summary-stat">
                                            <span>Active</span>
                                            <span>{orders.length - cancelledCount}</span>
                                        </div>
                                        {cancelledCount > 0 && (
                                            <div className="summary-stat refunded">
                                                <span>Refunded</span>
                                                <span>{cancelledCount}</span>
                                            </div>
                                        )}
                                    </div>

                                    {/* Order cards */}
                                    {orders.map((order) => (
                                        <OrderCard key={order.id} order={order} />
                                    ))}
                                </>
                            )}
                        </>
                    )}
                </div>
                {/* Browse restaurants button at the bottom */}
                <div style={{ padding: '40px 0 20px 0', textAlign: 'center' }}>
                    <Link to="/customer-marketplace" style={{ textDecoration: 'none', color: '#00cc66', fontWeight: 'bold', fontSize: '1.1rem', display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                        <span style={{ fontSize: '1.5rem', lineHeight: 1 }}>&larr;</span> Browse restaurants
                    </Link>
                </div>
            </section>
        </main>
    );
}

export default OrderStatusPage;