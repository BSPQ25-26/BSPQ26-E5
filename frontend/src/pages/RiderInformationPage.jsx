import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getRiderById, getRiderDashboard } from "../api/authApi";
import { readLoggedInRider } from "../utils/auth";
import "../assets/css/Home.css";
import "../assets/css/RiderInformationPage.css";

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

function RiderInformationPage() {
    const navigate = useNavigate();
    const [rider, setRider] = useState(null);
    const [dashboard, setDashboard] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

    const token = useMemo(() => localStorage.getItem("token") || "", []);

    useEffect(() => {
        const loggedInRider = readLoggedInRider();
        if (!loggedInRider || !token) {
            setErrorMessage("You must be logged in as a rider to access this page.");
            setIsLoading(false);
            return;
        }

        Promise.all([getRiderById(loggedInRider.id, token), getRiderDashboard(token)])
            .then(([profileData, dashboardData]) => {
                setRider(profileData);
                setDashboard(dashboardData);
            })
            .catch(() => {
                setErrorMessage("Could not load rider data. Please try again later.");
            })
            .finally(() => setIsLoading(false));
    }, [token]);

    const handleSignOut = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        localStorage.removeItem("userType");
        navigate("/");
    };

    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <Link to="/rider-dashboard" style={{ textDecoration: 'none', color: 'inherit' }}>
                            <h1 className="brand-title">JustOrder</h1>
                        </Link>
                    </div>
                    <div className="home-header-right" style={{ position: 'relative' }}>
                        <nav className="home-nav-links" aria-label="Main navigation">
                            <div className="profile-menu-container" style={{ position: 'relative' }}>
                                <button
                                    className="profile-avatar-btn"
                                    aria-label="User profile"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        setIsProfileMenuOpen((open) => !open);
                                    }}
                                    style={{
                                        width: '42px',
                                        height: '42px',
                                        borderRadius: '50%',
                                        overflow: 'hidden',
                                        display: 'flex',
                                        justifyContent: 'center',
                                        alignItems: 'center',
                                    }}
                                >
                                    <img
                                        src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                                        alt="Profile Avatar"
                                        className="profile-avatar-img"
                                    />
                                </button>
                                {isProfileMenuOpen && (
                                    <div className="profile-dropdown" style={{ right: 0, left: 'auto' }}>
                                        <Link to="/rider/profile" className="dropdown-item" onClick={() => setIsProfileMenuOpen(false)}>
                                            Information
                                        </Link>
                                        <button className="dropdown-item sign-out" onClick={handleSignOut}>
                                            Sign out
                                        </button>
                                    </div>
                                )}
                            </div>
                        </nav>
                    </div>
                </header>

                <div className="rider-info-content">
                    <section className="rider-hero">
                        <div className="rider-hero-copy">
                            <p className="rider-kicker">Rider profile</p>
                            <h2>Information dashboard</h2>
                            <p>Everything you need at a glance: identity, starter point, delivery performance, and current assignments.</p>
                            <div className="rider-chip-row">
                                <span className="rider-chip">ID #{rider?.id ?? '-'}</span>
                                <span className="rider-chip rider-chip-muted">{rider?.email ?? 'email unavailable'}</span>
                                <span className="rider-chip rider-chip-muted">{dashboard?.totalOrders ?? 0} total orders</span>
                            </div>
                        </div>

                        <aside className="rider-hero-card">
                            <div className="rider-avatar-wrap">
                                <img
                                    src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                                    alt="Rider avatar"
                                    className="rider-avatar"
                                />
                            </div>
                            <div>
                                <h3>{rider?.name ?? 'Unknown rider'}</h3>
                                <p>{rider?.phoneNumber ?? 'Phone unavailable'}</p>
                                <p>{rider?.dni ? `DNI ${rider.dni}` : 'DNI unavailable'}</p>
                            </div>
                        </aside>
                    </section>

                    {isLoading && <p className="customer-info-loading">Loading your rider data...</p>}
                    {errorMessage && <p className="customer-info-error">{errorMessage}</p>}

                    {!isLoading && !errorMessage && (
                        <div className="rider-layout-grid">
                            <section className="rider-panel rider-panel-profile">
                                <div className="rider-panel-header">
                                    <h3>Profile details</h3>
                                    <span>Registered rider</span>
                                </div>

                                <dl className="rider-details-list">
                                    <div>
                                        <dt>Name</dt>
                                        <dd>{rider?.name ?? '-'}</dd>
                                    </div>
                                    <div>
                                        <dt>Email</dt>
                                        <dd>{rider?.email ?? '-'}</dd>
                                    </div>
                                    <div>
                                        <dt>Phone</dt>
                                        <dd>{rider?.phoneNumber ?? '-'}</dd>
                                    </div>
                                    <div>
                                        <dt>DNI</dt>
                                        <dd>{rider?.dni ?? '-'}</dd>
                                    </div>
                                </dl>

                                <div className="rider-starter-point">
                                    <h4>Starter point</h4>
                                    <p>
                                        {rider?.starterPoint?.city ?? '-'}{rider?.starterPoint?.province ? `, ${rider.starterPoint.province}` : ''}
                                    </p>
                                    <p>
                                        {rider?.starterPoint?.country ?? '-'}{rider?.starterPoint?.postalCode ? ` • ${rider.starterPoint.postalCode}` : ''}
                                    </p>
                                    <p>Number {rider?.starterPoint?.number ?? '-'}</p>
                                </div>
                            </section>

                            <section className="rider-panel rider-panel-stats">
                                <div className="rider-panel-header">
                                    <h3>Delivery performance</h3>
                                    <span>Live summary</span>
                                </div>

                                <div className="customer-info-stats-grid rider-stats-grid">
                                    <article className="customer-info-stat-card">
                                        <h3>Total orders</h3>
                                        <p>{dashboard?.totalOrders ?? 0}</p>
                                    </article>
                                    <article className="customer-info-stat-card">
                                        <h3>Pending orders</h3>
                                        <p>{dashboard?.pendingOrders ?? 0}</p>
                                    </article>
                                    <article className="customer-info-stat-card">
                                        <h3>In progress</h3>
                                        <p>{dashboard?.inProgressOrders ?? 0}</p>
                                    </article>
                                    <article className="customer-info-stat-card">
                                        <h3>Delivered</h3>
                                        <p>{dashboard?.deliveredOrders ?? 0}</p>
                                    </article>
                                    <article className="customer-info-stat-card">
                                        <h3>Cancelled</h3>
                                        <p>{dashboard?.cancelledOrders ?? 0}</p>
                                    </article>
                                </div>

                                <div className="rider-highlight-strip">
                                    <div>
                                        <span>Active load</span>
                                        <strong>{dashboard?.pendingOrders ?? 0}</strong>
                                    </div>
                                    <div>
                                        <span>Completed</span>
                                        <strong>{dashboard?.deliveredOrders ?? 0}</strong>
                                    </div>
                                    <div>
                                        <span>In progress</span>
                                        <strong>{dashboard?.inProgressOrders ?? 0}</strong>
                                    </div>
                                </div>
                            </section>

                            <section className="rider-panel rider-panel-orders" aria-label="Assigned orders">
                                <div className="rider-panel-header">
                                    <h3>Assigned orders</h3>
                                    <span>{Array.isArray(dashboard?.assignedOrders) ? `${dashboard.assignedOrders.length} orders` : '0 orders'}</span>
                                </div>

                                {Array.isArray(dashboard?.assignedOrders) && dashboard.assignedOrders.length > 0 ? (
                                    <div className="rider-orders-grid">
                                        {dashboard.assignedOrders.map((order) => (
                                            <article key={order.id} className="rider-order-card">
                                                <div className="rider-order-top-row">
                                                    <strong>Order #{order.id}</strong>
                                                    <span className={`rider-order-status rider-order-status-${String(order.status ?? 'pending').toLowerCase().replace(/[^a-z0-9]+/g, '-')}`}>
                                                        {order.status ?? 'Pending'}
                                                    </span>
                                                </div>
                                                <p className="rider-order-meta">
                                                    <span>{formatDate(order.createdAt)}</span>
                                                    <span>{formatPrice(order.totalPrice)}</span>
                                                </p>
                                                <div className="rider-order-bottom-row">
                                                    <span>{formatDate(order.createdAt)}</span>
                                                    <span className="rider-order-pill">Ready for action</span>
                                                </div>
                                            </article>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="customer-info-empty-state rider-empty-state"><p>No assigned orders yet.</p></div>
                                )}
                            </section>

                            <div className="rider-footer-actions">
                                <Link to="/rider-dashboard" className="rider-back-link">Back to dashboard</Link>
                            </div>
                        </div>
                    )}
                </div>
            </section>
        </main>
    );
}

export default RiderInformationPage;
