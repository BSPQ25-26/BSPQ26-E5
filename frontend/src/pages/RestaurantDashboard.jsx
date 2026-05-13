import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getMenuByRestaurantId, getRestaurantDashboard, rejectRestaurantOrder } from "../api/authApi";
import RestaurantRejectionModal from "../components/RestaurantRejectionModal";
import "../assets/css/Home.css";
import "../assets/css/CustomerMarketplace.css";
import "../assets/css/RestaurantDashboard.css";


const formatPrice = (value) => `${Number(value || 0).toFixed(2)} EUR`;
const formatDate = (dateString) => {
    if (!dateString) return "-";
    return new Date(dateString).toLocaleString("en-GB", {
        day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit"
    });
};

function RestaurantDashboard() {
    const [activeTab, setActiveTab] = useState("orders");
    const [menu, setMenu] = useState([]);
    const [dashboardData, setDashboardData] = useState(null);


    const [rejectingOrderId, setRejectingOrderId] = useState(null);

    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const navigate = useNavigate();

    const storedUser = JSON.parse(localStorage.getItem("user"));
    const token = localStorage.getItem("token");
    const RESTAURANT_ID = storedUser?.id;

    useEffect(() => {
        if (!RESTAURANT_ID || !token) {
            navigate("/");
            return;
        }

        getMenuByRestaurantId(RESTAURANT_ID)
            .then(data => setMenu(Array.isArray(data) ? data : []))
            .catch(err => console.error("Error cargando menú:", err));


        getRestaurantDashboard(token)
            .then(data => setDashboardData(data))
            .catch(err => console.error("Error cargando dashboard:", err));

    }, [RESTAURANT_ID, token, navigate]);


    const recentOrders = dashboardData?.recentOrders || [];
    const pendingOrders = recentOrders.filter(order => order.status === "Pending");

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleSignOut = () => {
        setShowDropdown(false);
        localStorage.clear();
        navigate("/");
    };


    const handleConfirmReject = async (orderId, reason) => {
        try {
            await rejectRestaurantOrder(RESTAURANT_ID, orderId, reason, token);


            setDashboardData(prev => ({
                ...prev,
                recentOrders: prev.recentOrders.map(order =>
                    order.id === orderId ? { ...order, status: "Cancelled" } : order
                )
            }));


            setRejectingOrderId(null);
        } catch (error) {
            console.error("Error rechazando el pedido:", error);
            alert("No se pudo rechazar el pedido. Comprueba la consola.");
        }
    };

    return (
        <div className="dashboard-container">
            <header className="home-navbar">
                <div className="brand-group" aria-label="JustOrder home">
                    <button className="menu-button" type="button" aria-label="Open navigation menu"></button>
                    <h1 className="brand-title" style={{ cursor: 'pointer' }} onClick={() => navigate('/restaurant-dashboard')}>JustOrder</h1>
                </div>
                <div className="home-header-right">
                    <nav className="home-nav-links" aria-label="Main navigation">
                        <div className="profile-menu-container" ref={dropdownRef}>
                            <button className="profile-avatar-btn" onClick={() => setShowDropdown(!showDropdown)}>
                                <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Chef" alt="Profile Avatar" className="profile-avatar-img" />
                            </button>
                            {showDropdown && (
                                <div className="profile-dropdown">
                                    <button className="dropdown-item" onClick={() => { setShowDropdown(false); navigate("/restaurant/profile"); }}>
                                        My Information
                                    </button>
                                    <button className="dropdown-item sign-out" onClick={handleSignOut}>
                                        Sign out
                                    </button>
                                </div>
                            )}
                        </div>
                    </nav>
                </div>
            </header>

            <div className="dashboard-layout">

                <aside className="sidebar">
                    <button className={activeTab === "orders" ? "active" : ""} onClick={() => setActiveTab("orders")}>
                        Live Orders
                    </button>
                    <button className={activeTab === "menu" ? "active" : ""} onClick={() => setActiveTab("menu")}>
                        Menu Management
                    </button>
                    <button className={activeTab === "history" ? "active" : ""} onClick={() => setActiveTab("history")}>
                        Order History
                    </button>
                </aside>

                <main className="dashboard-content">

                    {activeTab === "orders" && (
                        <div className="orders-section">
                            <h2>Live Orders</h2>
                            <div style={{ display: 'flex', gap: '20px' }}>
                                <div style={{ flex: 1, backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 15px rgba(0,0,0,0.04)' }}>
                                    <h3 style={{ borderBottom: '2px solid #eaeaea', paddingBottom: '10px', marginTop: 0, color: '#dc3545' }}>
                                        Nuevos (Pending)
                                    </h3>
                                    {pendingOrders.length === 0 ? (
                                        <p style={{ color: '#777' }}>No hay pedidos nuevos.</p>
                                    ) : (
                                        pendingOrders.map(order => (
                                            <div key={order.id} style={{ border: '1px solid #eaeaea', padding: '15px', borderRadius: '8px', marginBottom: '15px' }}>
                                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                                                    <strong>Order #{order.id}</strong>
                                                    <span style={{ color: '#00cc66', fontWeight: 'bold' }}>{formatPrice(order.totalPrice)}</span>
                                                </div>
                                                <p style={{ fontSize: '13px', color: '#666', margin: '0 0 15px 0' }}>{formatDate(order.createdAt)}</p>
                                                <div style={{ display: 'flex', gap: '10px' }}>

                                                    <button
                                                        style={{ flex: 1, backgroundColor: '#00cc66', color: 'white', border: 'none', padding: '8px', borderRadius: '6px', cursor: 'pointer' }}
                                                        onClick={() => alert("Backend team: Missing Accept endpoint!")}
                                                    >
                                                        Accept
                                                    </button>

                                                    <button
                                                        style={{ flex: 1, backgroundColor: '#fff', color: '#dc3545', border: '1px solid #dc3545', padding: '8px', borderRadius: '6px', cursor: 'pointer' }}
                                                        onClick={() => setRejectingOrderId(order.id)}
                                                    >
                                                        Reject
                                                    </button>
                                                </div>
                                            </div>
                                        ))
                                    )}
                                </div>
                                <div style={{ flex: 1, backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 15px rgba(0,0,0,0.04)' }}>
                                    <h3 style={{ borderBottom: '2px solid #eaeaea', paddingBottom: '10px', marginTop: 0, color: '#f39c12' }}>En Cocina (Preparing)</h3>
                                </div>
                                <div style={{ flex: 1, backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 15px rgba(0,0,0,0.04)' }}>
                                    <h3 style={{ borderBottom: '2px solid #eaeaea', paddingBottom: '10px', marginTop: 0, color: '#00cc66' }}>Listos (Ready)</h3>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* MENU MANAGEMENT */}
                    {activeTab === "menu" && (
                        <div className="menu-section">
                            <div className="section-header">
                                <h2>Menu Management</h2>
                                <button className="add-button" onClick={() => navigate(`/restaurants/${RESTAURANT_ID}/menu-editor`)}>Edit Full Menu</button>
                            </div>
                            <div className="dishes-grid">
                                {menu.length > 0 ? (
                                    menu.map(dish => (
                                        <div key={dish.id} className="dish-card-simple">
                                            <h3>{dish.name}</h3><p>{dish.description}</p><span className="price">{dish.price}€</span>
                                        </div>
                                    ))
                                ) : (<p>Loading dishes from database...</p>)}
                            </div>
                        </div>
                    )}

                    {/* ORDER HISTORY */}
                    {activeTab === "history" && (
                        <div className="history-section">
                            <h2>Order History</h2>
                            <div className="table-container">
                                <table className="history-table">
                                    <thead><tr><th>Order ID</th><th>Date & Time</th><th>Total</th><th>Status</th></tr></thead>
                                    <tbody>
                                        {recentOrders.length === 0 ? (
                                            <tr><td colSpan="4" style={{ textAlign: "center" }}>No orders yet.</td></tr>
                                        ) : (
                                            recentOrders.map((order) => (
                                                <tr key={order.id}>
                                                    <td><strong>#{order.id}</strong></td>
                                                    <td>{formatDate(order.createdAt)}</td>
                                                    <td>{formatPrice(order.totalPrice)}</td>
                                                    <td><span className={`status-badge ${order.status?.toLowerCase() || 'unknown'}`}>{order.status}</span></td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}
                </main>
            </div>
            {rejectingOrderId && (
                <RestaurantRejectionModal
                    orderId={rejectingOrderId}
                    onClose={() => setRejectingOrderId(null)}
                    onSubmit={handleConfirmReject}
                />
            )}

        </div>
    );
}

export default RestaurantDashboard;