import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import cartImage from '../assets/images/Shopping cart.png';
import '../assets/css/Home.css';
import '../assets/css/CustomerMarketplace.css';

function CustomerMarketplace() {
  const [restaurants, setRestaurants] = useState([]);
  const [categories, setCategories] = useState(["All"]);

  const [searchTerm, setSearchTerm] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    fetch('http://localhost:8080/api/restaurants/search')
      .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
      })
      .then(data => {
        setRestaurants(data);

        const allCategoriesFromDB = data.flatMap(rest => rest.cuisineCategoryNames || []);

        const uniqueCategories = [...new Set(allCategoriesFromDB)];
        setCategories(["All", ...uniqueCategories]);
      })
      .catch(error => console.error("Error loading:", error));
  }, []);

  const handleSignOut = () => {
    alert("Closing sesion y erasing el token JWT...");
    setIsProfileMenuOpen(false);
    navigate("/");
  };

  const filteredRestaurants = restaurants.filter((rest) => {
    const restName = rest.name?.toLowerCase() || "";
    const matchesSearch = restName.includes(searchTerm.toLowerCase());

    const matchesCategory = activeCategory === "All" ||
      (rest.cuisineCategoryNames && rest.cuisineCategoryNames.includes(activeCategory));

    return matchesSearch && matchesCategory;
  });

  return (
    <main className="home-page">
      <section className="home-shell">

        <header className="home-navbar">
          <div className="brand-group" aria-label="JustOrder home">
            <button className="menu-button" type="button" aria-label="Open navigation menu">
            </button>
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
              {categories.map(category => (
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
                    <img src={restaurant.image || "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500&q=80"} alt={restaurant.name} className="card-image" />
                  </div>
                  <div className="card-info">
                    <div className="card-header">
                      <h3 className="card-title">{restaurant.name}</h3>
                      <span className="card-rating">★ {restaurant.averageRating || "N/A"}</span>
                    </div>
                    <p className="card-tags">
                      {(restaurant.cuisineCategoryNames && restaurant.cuisineCategoryNames.length > 0)
                        ? restaurant.cuisineCategoryNames.join(', ')
                        : "Restaurant"} • Modern Cuisine
                    </p>
                    <div className="card-delivery">
                      <span>⏱ {restaurant.time || "20-30 min"}</span>
                      <span>🛵 {restaurant.fee || "Free"}</span>
                    </div>
                  </div>
                </Link>
              ))
            ) : (
              <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                <h3>No restaurants found</h3>
                <p>Waiting for data from the server or adjust your filters.</p>
              </div>
            )}
          </section>

        </div>
      </section>
    </main>
  );
}

export default CustomerMarketplace;