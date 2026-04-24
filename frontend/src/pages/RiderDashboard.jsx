import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import RiderRejectionModal from '../components/RiderRejectionModal';
import '../assets/css/Home.css'; 
import '../assets/css/CustomerMarketplace.css'; 
import '../assets/css/RiderDashboard.css';

function RiderDashboard() {
  const navigate = useNavigate();
  
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  const [showRejectionModal, setShowRejectionModal] = useState(false);
  const [orderToReject, setOrderToReject] = useState(null);

  const [newOrders, setNewOrders] = useState([]);
  const [assignedOrders, setAssignedOrders] = useState([]);
  
  // Obtenemos el ID real del repartidor
  const RIDER_ID = localStorage.getItem('riderId');

  useEffect(() => {
    // Si no hay repartidor logueado, evitamos hacer la petición
    if (!RIDER_ID) return;

    const token = localStorage.getItem('token'); // Recuperamos el pase VIP

    fetch(`http://localhost:8080/api/riders/${RIDER_ID}/orders`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}` // Lo enviamos por seguridad
      }
    })
      .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
      })
      .then(data => {
        console.log("🚀 Raw data from DB:", data);
        
        const formattedOrders = data.map((order, index) => ({
          ...order,
          id: order.id || index + 100, 
          restaurantName: order.restaurantName || `Restaurant ID: ${order.customerId || 'N/A'}`,
          deliveryAddress: order.deliveryAddress || "Pending delivery address..."
        }));
        
        const pending = formattedOrders.filter(o => o.status === "Pending" || !o.status);
        const accepted = formattedOrders.filter(o => o.status === "Accepted" || o.status === "In Progress");
        
        setNewOrders(pending); 
        setAssignedOrders(accepted); 
      })
      .catch(error => {
        console.error("Error loading orders:", error);
      });
  }, [RIDER_ID]);

  const handleSignOut = () => {
    setIsProfileMenuOpen(false);
    navigate("/"); 
  };

  const triggerRejection = (orderId) => {
    setOrderToReject(orderId);
    setShowRejectionModal(true);
  };

  const handleRejectionSubmit = async (orderId, reason) => {
    const token = localStorage.getItem('token'); // También lo necesitamos aquí
    try {
      const response = await fetch(`http://localhost:8080/api/riders/${RIDER_ID}/orders/${orderId}/reject`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ reason: reason })
      });

      if (response.ok) {
        setNewOrders(prev => prev.filter(o => o.id !== orderId));
        setAssignedOrders(prev => prev.filter(o => o.id !== orderId));
        setShowRejectionModal(false);
        setOrderToReject(null);
      } else {
        alert("Failed to reject order on the server.");
      }
    } catch (error) {
      console.error("Connection error:", error);
    }
  };

  const dropdownItemStyle = {
    display: 'block',
    width: '100%',
    padding: '12px 20px',
    clear: 'both',
    fontWeight: '500',
    color: '#333',
    textAlign: 'inherit',
    whiteSpace: 'nowrap',
    backgroundColor: 'transparent',
    border: '0',
    textDecoration: 'none',
    fontSize: '0.95rem',
    cursor: 'pointer',
    boxSizing: 'border-box'
  };

  return (
    <main className="home-page">
      <section className="home-shell">
        <header className="home-navbar">
          <div className="brand-group" aria-label="JustOrder home">
            <h1 className="brand-title">JustOrder</h1>
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
                    setIsProfileMenuOpen(!isProfileMenuOpen);
                  }}
                  style={{ 
                    width: '42px', 
                    height: '42px', 
                    borderRadius: '50%', 
                    overflow: 'hidden', 
                    display: 'flex', 
                    justifyContent: 'center', 
                    alignItems: 'center',
                    border: '2px solid #eaeaea', 
                    padding: '0',
                    background: 'none',
                    cursor: 'pointer',
                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                  }}
                >
                  <img 
                    src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" 
                    alt="Profile Avatar" 
                    className="profile-avatar-img"
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                </button>
                {isProfileMenuOpen && (
                  <div className="profile-dropdown" style={{
                    position: 'absolute',
                    top: '55px', 
                    right: '0',
                    width: '180px',
                    backgroundColor: '#ffffff',
                    boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
                    borderRadius: '12px',
                    padding: '8px 0',
                    zIndex: 1000,
                    border: '1px solid #eaeaea',
                    display: 'flex',
                    flexDirection: 'column'
                  }}>
                    <style>{`
                      @keyframes dropdownFadeIn {
                        from { opacity: 0; transform: translateY(-10px); }
                        to { opacity: 1; transform: translateY(0); }
                      }
                    `}</style>

                    <Link 
                      to="/rider/profile" 
                      className="dropdown-item" 
                      onClick={() => setIsProfileMenuOpen(false)}
                      style={dropdownItemStyle}
                      onMouseOver={(e) => e.target.style.backgroundColor = '#f8f9fa'} 
                      onMouseOut={(e) => e.target.style.backgroundColor = 'transparent'}
                    >
                      Information
                    </Link>
                    

                    <button 
                      className="dropdown-item sign-out" 
                      onClick={handleSignOut}
                      style={{ 
                        ...dropdownItemStyle, 
                        color: '#ff4d4d', 
                        borderTop: '1px solid #eaeaea',
                        marginTop: '5px',
                        paddingTop: '15px'
                      }}
                      onMouseOver={(e) => e.target.style.backgroundColor = '#fff5f5'} 
                      onMouseOut={(e) => e.target.style.backgroundColor = 'transparent'}
                    >
                      Sign out
                    </button>
                  </div>
                )}
              </div>
            </nav>
            <div style={{ width: '10px', marginLeft: '10px' }}></div>
          </div>
        </header>
        <div className="marketplace-content" style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px' }}>
            <div>
              <h2 style={{ borderBottom: '2px solid #eaeaea', paddingBottom: '10px', marginBottom: '20px', fontSize: '1.5rem' }}>New Order Requests</h2>
              
              {newOrders.length > 0 ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                  {newOrders.map(order => (
                    <div key={order.id} className="order-card" style={{ border: '1px solid #00cc66', borderLeft: '5px solid #00cc66', padding: '15px', borderRadius: '8px', backgroundColor: 'white', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                      <h3 style={{ fontSize: '1.2rem', marginBottom: '10px' }}>Order #{order.id}</h3>
                      <div className="order-details" style={{ fontSize: '0.95rem', color: '#666' }}>
                        <p style={{ margin: '5px 0' }}><strong>Restaurant:</strong> {order.restaurantName}</p>
                        <p style={{ margin: '5px 0' }}><strong>Address:</strong> {order.deliveryAddress}</p>
                        <p style={{ margin: '5px 0', color: '#333' }}><strong>Payout:</strong> €{order.totalPrice}</p>
                      </div>
                      <div className="dashboard-actions" style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                        <button className="btn-accept" style={{ backgroundColor: '#00cc66', color: 'white', padding: '10px 15px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                          Accept Order
                        </button>
                        <button className="btn-reject-trigger" onClick={() => triggerRejection(order.id)} style={{ backgroundColor: '#ff4d4d', color: 'white', padding: '10px 15px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                          Decline
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p style={{ color: '#666' }}>No new requests at the moment.</p>
              )}
            </div>
            <div>
              <h2 style={{ borderBottom: '2px solid #eaeaea', paddingBottom: '10px', marginBottom: '20px', fontSize: '1.5rem' }}>My Assigned Orders</h2>
              
              {assignedOrders.length > 0 ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                  {assignedOrders.map(order => (
                    <div key={order.id} className="order-card" style={{ border: '1px solid #ccc', borderLeft: '5px solid #333', padding: '15px', borderRadius: '8px', backgroundColor: 'white', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                      <h3 style={{ fontSize: '1.2rem', marginBottom: '10px' }}>Order #{order.id}</h3>
                      <div className="order-details" style={{ fontSize: '0.95rem', color: '#666' }}>
                        <p style={{ margin: '5px 0' }}><strong>Restaurant:</strong> {order.restaurantName}</p>
                        <p style={{ margin: '5px 0' }}><strong>Address:</strong> {order.deliveryAddress}</p>
                      </div>
                      <div className="dashboard-actions" style={{ marginTop: '15px', display: 'flex', justifyContent: 'flex-end' }}>
                        <button className="btn-reject-trigger" onClick={() => triggerRejection(order.id)} style={{ backgroundColor: '#ff4d4d', color: 'white', padding: '10px 15px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                          Emergency Reject
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p style={{ color: '#666' }}>You have no assigned orders.</p>
              )}
            </div>

          </div>
        </div>
        {showRejectionModal && orderToReject && (
          <RiderRejectionModal 
            orderId={orderToReject} 
            onClose={() => {
              setShowRejectionModal(false);
              setOrderToReject(null);
            }} 
            onSubmit={handleRejectionSubmit} 
          />
        )}
      </section>
    </main>
  );
}

export default RiderDashboard;