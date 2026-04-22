import React, { useState, useEffect, useRef } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import cartImage from '../assets/images/Shopping cart.png';
import { useCart } from '../store/CartContext';
import '../assets/css/Home.css';
import '../assets/css/CustomerMarketplace.css';

function RestaurantDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();

  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const toastTimerRef = useRef(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/restaurants/search')
      .then(res => res.json())
      .then(data => {
        const currentRest = data.find(r => r.id.toString() === id);
        setRestaurant(currentRest);
      })
      .catch(err => console.error("Error fetching restaurant details:", err));

    fetch(`http://localhost:8080/api/restaurants/${id}/menu`)
      .then(res => res.json())
      .then(data => {
        console.log("Menu data received:", data);
        setMenu(data);
      })
      .catch(err => console.error("Error fetching menu:", err));
  }, [id]);

  useEffect(() => {
    return () => {
      if (toastTimerRef.current) {
        clearTimeout(toastTimerRef.current);
      }
    };
  }, []);

  const handleSignOut = () => {
    alert("Signing out and destroying JWT token...");
    setIsProfileMenuOpen(false);
    navigate("/");
  };

  const handleAddToCart = (dish) => {
    addToCart(dish);
    setToastMessage(`"${dish.name}" added to cart`);

    if (toastTimerRef.current) {
      clearTimeout(toastTimerRef.current);
    }
    toastTimerRef.current = setTimeout(() => {
      setToastMessage("");
      toastTimerRef.current = null;
    }, 2000);
  };

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


        <div className="marketplace-content" style={{ padding: '0 20px' }}>

          <div style={{ padding: '30px 0', borderBottom: '1px solid #eaeaea', marginBottom: '30px' }}>
            <h1 style={{ fontSize: '2.5rem', marginBottom: '10px' }}>{restaurant.name}</h1>
            <p style={{ fontSize: '1.1rem', color: '#666', marginBottom: '15px' }}>{restaurant.description}</p>
            <div style={{ display: 'flex', gap: '20px', fontWeight: 'bold', color: '#333' }}>
              <span>⭐ {restaurant.averageRating || "New"}</span>
              <span>📍 {(restaurant.cuisineCategoryNames && restaurant.cuisineCategoryNames.length > 0) ? restaurant.cuisineCategoryNames.join(', ') : "Various"}</span>
            </div>
          </div>

          <h2 style={{ marginBottom: '20px' }}>Menu</h2>

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
                    <button
                      type="button"
                      onClick={() => handleAddToCart(dish)}
                      style={{ backgroundColor: '#00cc66', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '30px', fontWeight: 'bold', cursor: 'pointer' }}
                    >
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

      {toastMessage && (
        <div
          role="status"
          aria-live="polite"
          style={{
            position: 'fixed',
            bottom: '30px',
            left: '50%',
            transform: 'translateX(-50%)',
            backgroundColor: '#00cc66',
            color: 'white',
            padding: '14px 28px',
            borderRadius: '30px',
            fontWeight: 'bold',
            boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
            zIndex: 1000,
          }}
        >
          ✓ {toastMessage}
        </div>
      )}
    </main>
  );
}

export default RestaurantDetail;