// src/pages/CustomerMarketplace.jsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import cartImage from '../assets/images/Shopping cart.png';
import '../assets/css/Home.css'; 
import '../assets/css/CustomerMarketplace.css';

// 1. Datos simulados (Mock Data) para la interfaz
const MOCK_RESTAURANTS = [
  { id: 1, name: "Sakura Sushi", category: "Sushi", rating: "4.8", time: "25-35 min", fee: "Free", image: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500&q=80" },
  { id: 2, name: "Burger Joint", category: "Burgers", rating: "4.5", time: "15-25 min", fee: "€1.99", image: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&q=80" },
  { id: 3, name: "La Pizzería", category: "Pizza", rating: "4.2", time: "30-45 min", fee: "€2.50", image: "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=500&q=80" },
  { id: 4, name: "Green Bowl", category: "Healthy", rating: "4.9", time: "10-20 min", fee: "Free", image: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&q=80" },
  { id: 5, name: "Tokyo Roll", category: "Sushi", rating: "4.6", time: "20-30 min", fee: "€1.50", image: "https://images.unsplash.com/photo-1553621042-f6e147245754?w=500&q=80" },
];

const CATEGORIES = ["All", "Sushi", "Burgers", "Pizza", "Healthy", "Desserts"];

function CustomerMarketplace() {
  // Estados de la interfaz
  const [searchTerm, setSearchTerm] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  
  const navigate = useNavigate();

  // Función de cierre de sesión simulada
  const handleSignOut = () => {
    // Aquí se borraría el token JWT en el futuro: localStorage.removeItem("token");
    alert("Cerrando sesión y destruyendo el token JWT...");
    setIsProfileMenuOpen(false);
    navigate("/"); 
  };

  // Motor de filtrado cruzado
  const filteredRestaurants = MOCK_RESTAURANTS.filter((rest) => {
    const matchesSearch = rest.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = activeCategory === "All" || rest.category === activeCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <main className="home-page">
      <section className="home-shell">
        
        {/* --- HEADER --- */}
        <header className="home-navbar">
          <div className="brand-group" aria-label="JustOrder home">
            <button className="menu-button" type="button" aria-label="Open navigation menu">
              <span />
              <span />
              <span />
            </button>
            <h1 className="brand-title">JustOrder</h1>
          </div>

          <div className="home-header-right">
            <nav className="home-nav-links" aria-label="Main navigation">
              
              {/* Contenedor del Perfil y Menú Desplegable */}
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

                {/* Dropdown Menu (Sin Emojis) */}
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

        {/* --- MAIN CONTENT --- */}
        <div className="marketplace-content">
          
          <section className="search-filter-section">
            <input 
              type="text" 
              className="search-input" 
              placeholder="Search for restaurants, cuisines, or dishes..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            
            <div className="category-filters">
              {CATEGORIES.map(category => (
                <button 
                  key={category}
                  className={`filter-chip ${activeCategory === category ? 'active' : ''}`}
                  onClick={() => setActiveCategory(category)}
                >
                  {category}
                </button>
              ))}
            </div>
          </section>

          <section className="restaurant-grid">
            {filteredRestaurants.length > 0 ? (
              filteredRestaurants.map(restaurant => (
                <Link to={`/restaurants/${restaurant.id}`} key={restaurant.id} className="restaurant-card">
                  <div className="card-image-wrapper">
                    <img src={restaurant.image} alt={restaurant.name} className="card-image" />
                  </div>
                  <div className="card-info">
                    <div className="card-header">
                      <h3 className="card-title">{restaurant.name}</h3>
                      <span className="card-rating">★ {restaurant.rating}</span>
                    </div>
                    <p className="card-tags">{restaurant.category} • Modern Cuisine</p>
                    <div className="card-delivery">
                      <span>⏱ {restaurant.time}</span>
                      <span>🛵 {restaurant.fee}</span>
                    </div>
                  </div>
                </Link>
              ))
            ) : (
              <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                <h3>No restaurants found</h3>
                <p>Try adjusting your search or filters.</p>
              </div>
            )}
          </section>

        </div>
      </section>
    </main>
  );
}

export default CustomerMarketplace;