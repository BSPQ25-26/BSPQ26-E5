import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import cartImage from '../assets/images/Shopping cart.png';
import '../assets/css/Home.css'; 
import '../assets/css/CustomerMarketplace.css';

function RestaurantDetail() {
  const { id } = useParams(); 
  const navigate = useNavigate();

  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  useEffect(() => {
    // 1. Fetch restaurant details (using search as a workaround for NOT_IMPLEMENTED)
    fetch('http://localhost:8080/api/restaurants/search')
      .then(res => res.json())
      .then(data => {
        const currentRest = data.find(r => r.id.toString() === id);
        setRestaurant(currentRest);
      })
      .catch(err => console.error("Error fetching restaurant details:", err));

    // 2. Fetch the specific menu for this restaurant
    fetch(`http://localhost:8080/api/restaurants/${id}/menu`)
      .then(res => res.json())
      .then(data => {
        console.log("Menu data received:", data);
        setMenu(data);
      })
      .catch(err => console.error("Error fetching menu:", err));
  }, [id]);

  const handleSignOut = () => {
    alert("Signing out and destroying JWT token...");
    setIsProfileMenuOpen(false);
    navigate("/"); 
  };

  // Loading state UI
  if (!restaurant) {
    return (
      <div style={{ padding: '100px', textAlign: 'center' }}>
        <h2>Loading restaurant details...</h2>
      </div>
    );
  }

  return (
    <main className="home-page">
      <section className="home-shell">
        
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
                  onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                >
                  <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" alt="Profile Avatar" className="profile-avatar-img"/>
                </button>

                {isProfileMenuOpen && (
                  <div className="profile-dropdown">
                    <Link to="/customer/orders" className="dropdown-item" onClick={() => setIsProfileMenuOpen(false)}>My Orders</Link>
                    <Link to="/customer/profile" className="dropdown-item" onClick={() => setIsProfileMenuOpen(false)}>Information</Link>
                    <button className="dropdown-item sign-out" onClick={handleSignOut}>Sign out</button>
                  </div>
                )}
              </div>
            </nav>

            <Link to="/checkout" className="cart-link">
              <img src={cartImage} alt="Shopping cart" className="cart-icon" />
            </Link>
          </div>
        </header>

        {/* --- RESTAURANT SPECIFIC CONTENT --- */}
        <div className="marketplace-content" style={{ padding: '0 20px' }}>
          
          {/* Restaurant Info Banner */}
          <div style={{ padding: '30px 0', borderBottom: '1px solid #eaeaea', marginBottom: '30px' }}>
            <h1 style={{ fontSize: '2.5rem', marginBottom: '10px' }}>{restaurant.name}</h1>
            <p style={{ fontSize: '1.1rem', color: '#666', marginBottom: '15px' }}>{restaurant.description}</p>
            <div style={{ display: 'flex', gap: '20px', fontWeight: 'bold', color: '#333' }}>
              <span>⭐ {restaurant.averageRating || "New"}</span>
              <span>📍 {(restaurant.cuisineCategoryNames && restaurant.cuisineCategoryNames.length > 0) ? restaurant.cuisineCategoryNames.join(', ') : "Various"}</span>
            </div>
          </div>

          <h2 style={{ marginBottom: '20px' }}>Menu</h2>
          
          {/* Dishes Grid */}
          <section className="restaurant-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
            {menu.length > 0 ? (
              menu.map(dish => (
                <div key={dish.id} style={{ border: '1px solid #eee', borderRadius: '12px', padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', backgroundColor: 'white', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}>
                  <div>
                    <h3 style={{ fontSize: '1.3rem', marginBottom: '10px' }}>{dish.name}</h3>
                    <p style={{ color: '#666', fontSize: '0.95rem', marginBottom: '15px', lineHeight: '1.4' }}>{dish.description}</p>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' }}>
                    <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>€{dish.price.toFixed(2)}</span>
                    <button style={{ backgroundColor: '#00cc66', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '30px', fontWeight: 'bold', cursor: 'pointer' }}>
                      + Add
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px', color: '#666' }}>
                <h3>No dishes available yet</h3>
                <p>This restaurant is currently setting up its menu.</p>
              </div>
            )}
          </section>

        </div>
      </section>
    </main>
  );
}

export default RestaurantDetail;