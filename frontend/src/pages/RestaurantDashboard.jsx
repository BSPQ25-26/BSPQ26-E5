import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getMenuByRestaurantId } from "../api/authApi"; 

import "../assets/css/Home.css";
import "../assets/css/CustomerMarketplace.css"; 
import "../assets/css/RestaurantDashboard.css";

// DATOS FALSOS TEMPORALES (Para poder maquetar la tabla de historial)
const MOCK_ORDER_HISTORY = [
    { id: "ORD-001", date: "2026-04-26 14:30", items: "1x Four Cheese Pizza, 1x Coke", total: 25.50, status: "Delivered" },
    { id: "ORD-002", date: "2026-04-26 15:15", items: "2x Grilled Salmon", total: 50.00, status: "Delivered" },
    { id: "ORD-003", date: "2026-04-27 12:00", items: "1x Margherita Pizza", total: 12.00, status: "Cancelled" },
    { id: "ORD-004", date: "2026-04-27 13:45", items: "3x Caesar Salad, 2x Water", total: 35.00, status: "Delivered" }
];

function RestaurantDashboard() {
    const [activeTab, setActiveTab] = useState("orders");
    const [menu, setMenu] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const navigate = useNavigate();

    const RESTAURANT_ID = 1; 

    useEffect(() => {
        if (activeTab === "menu") {
            const fetchMenu = async () => {
                try {
                    const data = await getMenuByRestaurantId(RESTAURANT_ID);
                    setMenu(Array.isArray(data) ? data : []);
                } catch (error) {
                    console.error("Error cargando menú:", error);
                }
            };
            fetchMenu();
        }
    }, [activeTab]);

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

    return (
        <div className="dashboard-container">
            
            <header className="home-navbar">
                <div className="brand-group" aria-label="JustOrder home">
                    <button className="menu-button" type="button" aria-label="Open navigation menu"></button>
                    <h1 className="brand-title">
                        JustOrder
                    </h1>
                </div>

                <div className="home-header-right">
                    <nav className="home-nav-links" aria-label="Main navigation">
                        <div className="profile-menu-container" ref={dropdownRef}>
                            <button 
                                className="profile-avatar-btn" 
                                aria-label="Restaurant profile"
                                onClick={() => setShowDropdown(!showDropdown)}
                            >
                                <img 
                                    src="https://api.dicebear.com/7.x/avataaars/svg?seed=Chef" 
                                    alt="Profile Avatar" 
                                    className="profile-avatar-img"
                                />
                            </button>

                            {showDropdown && (
                                <div className="profile-dropdown">
                                    <button 
                                        className="dropdown-item" 
                                        onClick={() => { setShowDropdown(false); navigate(""); }}
                                    >
                                        My Information
                                    </button>
                                    <button 
                                        className="dropdown-item sign-out" 
                                        onClick={handleSignOut}
                                    >
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
                    <button 
                        className={activeTab === "orders" ? "active" : ""} 
                        onClick={() => setActiveTab("orders")}
                    >
                        Live Orders
                    </button>
                    <button 
                        className={activeTab === "menu" ? "active" : ""} 
                        onClick={() => setActiveTab("menu")}
                    >
                        Menu Management
                    </button>
                    {/* NUEVO: Historial encima de Estadísticas */}
                    <button 
                        className={activeTab === "history" ? "active" : ""} 
                        onClick={() => setActiveTab("history")}
                    >
                        Order History
                    </button>
                    <button 
                        className={activeTab === "statistics" ? "active" : ""} 
                        onClick={() => setActiveTab("statistics")}
                    >
                        Statistics
                    </button>
                </aside>

                <main className="dashboard-content">
                    {activeTab === "orders" && (
                        <div className="orders-section">
                            <h2>Live Orders</h2>
                            <p className="info-msg">Waiting for backend orders endpoint...</p>
                        </div>
                    )}

                    {activeTab === "menu" && (
                        <div className="menu-section">
                            <div className="section-header">
                                <h2>Menu Management</h2>
                                <button className="add-button" onClick={() => navigate(`/restaurants/${RESTAURANT_ID}/menu-editor`)}>
                                    Edit Full Menu
                                </button>
                            </div>
                            
                            <div className="dishes-grid">
                                {menu.length > 0 ? (
                                    menu.map(dish => (
                                        <div key={dish.id} className="dish-card-simple">
                                            <h3>{dish.name}</h3>
                                            <p>{dish.description}</p>
                                            <span className="price">{dish.price}€</span>
                                        </div>
                                    ))
                                ) : (
                                    <p>Loading dishes from database...</p>
                                )}
                            </div>
                        </div>
                    )}

                    {/* NUEVA SECCIÓN: HISTORIAL DE PEDIDOS */}
                    {activeTab === "history" && (
                        <div className="history-section">
                            <h2>Order History</h2>
                            <div className="table-container">
                                <table className="history-table">
                                    <thead>
                                        <tr>
                                            <th>Order ID</th>
                                            <th>Date & Time</th>
                                            <th>Items</th>
                                            <th>Total</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {MOCK_ORDER_HISTORY.map((order) => (
                                            <tr key={order.id}>
                                                <td><strong>{order.id}</strong></td>
                                                <td>{order.date}</td>
                                                <td>{order.items}</td>
                                                <td>{order.total.toFixed(2)}€</td>
                                                <td>
                                                    <span className={`status-badge ${order.status.toLowerCase()}`}>
                                                        {order.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}

                    {activeTab === "statistics" && (
                        <div className="statistics-section">
                            <h2>Restaurant Statistics</h2>
                            <p className="info-msg">This section is reserved for future analytics implementation.</p>
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
}

export default RestaurantDashboard;