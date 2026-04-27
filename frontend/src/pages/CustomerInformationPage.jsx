import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import cartImage from "../assets/images/Shopping cart.png";
import { getCustomerDashboard } from "../api/authApi";
import { readLoggedInCustomer } from "../utils/auth";
import "../assets/css/Home.css";
import "../assets/css/CustomerMarketplace.css";
import "../assets/css/CustomerInformationPage.css";

const formatPrice = (value) => `${Number(value || 0).toFixed(2)} EUR`;

const formatDate = (dateString) => {
    if (!dateString) return "-";
    return new Date(dateString).toLocaleString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

function CustomerInformationPage() {
    const [dashboard, setDashboard] = useState(null);
    const [loggedInCustomer, setLoggedInCustomer] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        const customer = readLoggedInCustomer();
        setLoggedInCustomer(customer);

        if (!customer) {
            setErrorMessage("You must be logged in as a customer to see this dashboard.");
            setIsLoading(false);
            return;
        }

        getCustomerDashboard(customer.id)
            .then((data) => setDashboard(data))
            .catch(() => setErrorMessage("Could not load your information dashboard. Please try again later."))
            .finally(() => setIsLoading(false));
    }, []);

    const handleSignOut = () => {
        setIsProfileMenuOpen(false);
        navigate("/");
    };

    const recentOrders = dashboard?.recentOrders || [];

    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <button className="menu-button" type="button" aria-label="Open navigation menu">
                            <span /><span /><span />
                        </button>
                        <h1 className="brand-title">JustOrder</h1>
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
                                        <Link to="/orders" className="dropdown-item" onClick={() => setIsProfileMenuOpen(false)}>
                                            My Orders
                                        </Link>
                                        <Link to="/customer/profile" className="dropdown-item" onClick={() => setIsProfileMenuOpen(false)}>
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

                <div className="customer-info-content">
                    <div className="customer-info-header">
                        <h2>Information Dashboard</h2>
                        <p>Your personal delivery stats and latest order activity.</p>
                    </div>

                    {isLoading && <p className="customer-info-loading">Loading your dashboard...</p>}
                    {errorMessage && <p className="customer-info-error">{errorMessage}</p>}

                    {!isLoading && !errorMessage && dashboard && (
                        <>
                            <div className="customer-info-meta">
                                <span>
                                    Customer: {dashboard.customerName || loggedInCustomer?.name || "Unknown"}
                                </span>
                            </div>

                            <div className="customer-info-stats-grid">
                                <article className="customer-info-stat-card">
                                    <h3>Total orders</h3>
                                    <p>{dashboard.totalOrders}</p>
                                </article>
                                <article className="customer-info-stat-card">
                                    <h3>Active orders</h3>
                                    <p>{dashboard.activeOrders}</p>
                                </article>
                                <article className="customer-info-stat-card">
                                    <h3>Delivered orders</h3>
                                    <p>{dashboard.deliveredOrders}</p>
                                </article>
                                <article className="customer-info-stat-card">
                                    <h3>Cancelled orders</h3>
                                    <p>{dashboard.cancelledOrders}</p>
                                </article>
                                <article className="customer-info-stat-card">
                                    <h3>Total spent</h3>
                                    <p>{formatPrice(dashboard.totalSpent)}</p>
                                </article>
                                <article className="customer-info-stat-card">
                                    <h3>Total refunded</h3>
                                    <p>{formatPrice(dashboard.totalRefunded)}</p>
                                </article>
                            </div>

                            <section className="customer-info-orders-section" aria-label="Recent orders">
                                <div className="customer-info-orders-header">
                                    <h3>Recent orders</h3>
                                    <Link to="/orders">See all orders</Link>
                                </div>

                                {recentOrders.length === 0 ? (
                                    <div className="customer-info-empty-state">
                                        <p>No recent orders to display yet.</p>
                                    </div>
                                ) : (
                                    <div className="customer-info-recent-orders">
                                        {recentOrders.map((order) => (
                                            <article key={order.id} className="customer-info-order-card">
                                                <div className="customer-info-order-top-row">
                                                    <strong>Order #{order.id}</strong>
                                                    <span>{order.status}</span>
                                                </div>
                                                <div className="customer-info-order-bottom-row">
                                                    <span>{formatDate(order.createdAt)}</span>
                                                    <span>{formatPrice(order.totalPrice)}</span>
                                                </div>
                                            </article>
                                        ))}
                                    </div>
                                )}
                            </section>
                        </>
                    )}
                </div>
            </section>
        </main>
    );
}

export default CustomerInformationPage;
