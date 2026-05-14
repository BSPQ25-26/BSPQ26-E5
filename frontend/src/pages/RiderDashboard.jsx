import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import RiderRejectionModal from '../components/RiderRejectionModal';
import { readLoggedInRider } from '../utils/auth';
import '../assets/css/Home.css';
import '../assets/css/CustomerMarketplace.css';
import '../assets/css/RiderDashboard.css';

function RiderDashboard() {
  const navigate = useNavigate();

  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  const [showRejectionModal, setShowRejectionModal] = useState(false);
  const [orderToReject, setOrderToReject] = useState(null);

  const [showPinModal, setShowPinModal] = useState(false);
  const [orderToDeliver, setOrderToDeliver] = useState(null);
  const [pinInput, setPinInput] = useState('');

  const [newOrders, setNewOrders] = useState([]);
  const [assignedOrders, setAssignedOrders] = useState([]);

  // Read the rider that is currently logged in from localStorage.
  // If no rider is logged in, redirect to the home page.
  const [loggedInRider, setLoggedInRider] = useState(null);

  useEffect(() => {
    const rider = readLoggedInRider();
    if (!rider) {
      navigate("/");
      return;
    }
    setLoggedInRider(rider);
  }, [navigate]);

  useEffect(() => {
    if (!loggedInRider) {
      return;
    }

    Promise.all([
      fetch(`http://localhost:8080/api/riders/orders/available`).then(res => {
        if (!res.ok) throw new Error('Network response was not ok');
        return res.json();
      }),
      fetch(`http://localhost:8080/api/riders/${loggedInRider.id}/orders`).then(res => {
        if (!res.ok) throw new Error('Network response was not ok');
        return res.json();
      })
    ])
      .then(([availableData, assignedData]) => {
        console.log("🚀 Available orders:", availableData);
        console.log("🚀 Assigned orders:", assignedData);

        const formatOrders = (data) => data.map((order, index) => ({
          ...order,
          id: order.id || index + 100,
          restaurantName: order.restaurantName || `Restaurant ID: ${order.customerId || 'N/A'}`,
          deliveryAddress: order.deliveryAddress || "Pending delivery address..."
        }));

        setNewOrders(formatOrders(availableData));
        setAssignedOrders(formatOrders(assignedData).filter(o => o.status !== "Delivered" && o.status !== "Cancelled"));
      })
      .catch(error => {
        console.error("Error loading orders:", error);
      });
  }, [loggedInRider]);

  const handleSignOut = () => {
    setIsProfileMenuOpen(false);
    navigate("/");
  };

  const triggerRejection = (orderId) => {
    setOrderToReject(orderId);
    setShowRejectionModal(true);
  };

  const handleAcceptOrder = async (order) => {
    if (!loggedInRider) return;

    try {
      const response = await fetch(`http://localhost:8080/api/riders/${loggedInRider.id}/orders/${order.id}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(order)
      });

      if (response.ok) {
        const assignedOrder = await response.json();
        setNewOrders(prev => prev.filter(o => o.id !== order.id));
        setAssignedOrders(prev => [...prev, {
          ...assignedOrder,
          restaurantName: assignedOrder.restaurantName || order.restaurantName,
          deliveryAddress: assignedOrder.deliveryAddress || order.deliveryAddress
        }]);
      } else if (response.status === 400) {
        alert("This order has already been assigned to another rider.");
        // Refresh orders to get the latest list
        setNewOrders(prev => prev.filter(o => o.id !== order.id));
      } else {
        alert("Failed to assign order on the server.");
      }
    } catch (error) {
      console.error("Connection error:", error);
      alert("Network error. Could not assign order.");
    }
  };

  const handleRejectionSubmit = async (orderId, reason) => {
    if (!loggedInRider) return;

    try {
      const response = await fetch(`http://localhost:8080/api/riders/${loggedInRider.id}/orders/${orderId}/reject`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
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

  const handleStatusChange = async (orderId, newStatus) => {
    if (!loggedInRider) return;

    if (newStatus === "Delivered") {
      setOrderToDeliver(orderId);
      setShowPinModal(true);
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/riders/${loggedInRider.id}/orders/${orderId}/status`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newStatus })
      });

      if (response.ok) {
        const updatedOrder = await response.json();
        setAssignedOrders(prev => prev.map(o => o.id === orderId ? { ...o, status: updatedOrder.status, timeline: updatedOrder.timeline } : o));
      } else {
        alert("Failed to update status.");
      }
    } catch (error) {
      console.error("Connection error:", error);
      alert("Network error.");
    }
  };

  const handlePinSubmit = async () => {
    if (!loggedInRider) return;

    try {
      const response = await fetch(`http://localhost:8080/api/riders/${loggedInRider.id}/orders/${orderToDeliver}/verify-pin`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ pin: pinInput })
      });

      if (response.ok) {
        setAssignedOrders(prev => prev.filter(o => o.id !== orderToDeliver)); // Remove delivered from assigned
        setShowPinModal(false);
        setOrderToDeliver(null);
        setPinInput('');
        alert("Order successfully delivered!");
      } else {
        alert("Invalid PIN or error verifying.");
      }
    } catch (error) {
      console.error("Connection error:", error);
      alert("Network error.");
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

  // Block rendering until we know who the rider is.
  if (!loggedInRider) {
    return (
      <div style={{ padding: '100px', textAlign: 'center' }}>
        <h2>Checking your session...</h2>
      </div>
    );
  }

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

          <div style={{ marginBottom: '20px', color: '#333', fontSize: '0.95rem' }}>
            Logged in as <strong>{loggedInRider.name || loggedInRider.email}</strong>
          </div>

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
                        <button className="btn-accept" onClick={() => handleAcceptOrder(order)} style={{ backgroundColor: '#00cc66', color: 'white', padding: '10px 15px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
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
                        <div style={{ marginTop: '10px' }}>
                          <strong style={{marginRight: '10px'}}>Current Status:</strong>
                          <select 
                            value={order.status} 
                            onChange={(e) => handleStatusChange(order.id, e.target.value)}
                            style={{ padding: '5px', borderRadius: '4px', border: '1px solid #ccc' }}
                          >
                            <option value="Confirmed">Confirmed</option>
                            <option value="Preparing">Preparing</option>
                            <option value="Out for Delivery">Out for Delivery</option>
                            <option value="Delivered">Delivered</option>
                          </select>
                        </div>
                      </div>
                      
                      {order.timeline && order.timeline.length > 0 && (
                        <div className="order-timeline" style={{marginTop: '15px', backgroundColor: '#f9f9f9', padding: '10px', borderRadius: '5px'}}>
                            <h4 style={{marginBottom: '10px', fontSize: '1rem', color: '#444'}}>Timeline</h4>
                            <div style={{borderLeft: '2px solid #ccc', marginLeft: '5px', paddingLeft: '10px'}}>
                                {order.timeline.map((event, idx) => (
                                    <div key={idx} style={{marginBottom: '10px', position: 'relative'}}>
                                        <div style={{
                                            position: 'absolute',
                                            left: '-16px',
                                            top: '4px',
                                            width: '8px',
                                            height: '8px',
                                            borderRadius: '50%',
                                            backgroundColor: event.statusOrEvent === 'Cancelled' || event.statusOrEvent === 'Rejected' ? '#ff4d4d' : '#00cc66'
                                        }}></div>
                                        <div style={{fontSize: '0.8rem', color: '#888'}}>{new Date(event.timestamp).toLocaleString()}</div>
                                        <div style={{fontWeight: 'bold', fontSize: '0.9rem', color: '#333'}}>{event.statusOrEvent}</div>
                                        {event.details && <div style={{fontSize: '0.85rem', color: '#555'}}>{event.details}</div>}
                                    </div>
                                ))}
                            </div>
                        </div>
                      )}

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

        {showPinModal && (
          <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 2000 }}>
            <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '8px', width: '300px', textAlign: 'center' }}>
              <h3 style={{ marginBottom: '15px' }}>Verify Delivery PIN</h3>
              <p style={{ fontSize: '0.9rem', color: '#666', marginBottom: '15px' }}>Please ask the customer for their 6-digit PIN to confirm delivery.</p>
              <input 
                type="text" 
                maxLength="6" 
                value={pinInput} 
                onChange={e => setPinInput(e.target.value)} 
                placeholder="000000"
                style={{ width: '100%', padding: '10px', fontSize: '1.2rem', textAlign: 'center', letterSpacing: '2px', border: '1px solid #ccc', borderRadius: '4px', marginBottom: '20px' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <button onClick={() => { setShowPinModal(false); setOrderToDeliver(null); setPinInput(''); }} style={{ padding: '8px 15px', border: 'none', backgroundColor: '#eaeaea', borderRadius: '4px', cursor: 'pointer' }}>Cancel</button>
                <button onClick={handlePinSubmit} style={{ padding: '8px 15px', border: 'none', backgroundColor: '#00cc66', color: 'white', fontWeight: 'bold', borderRadius: '4px', cursor: 'pointer' }}>Verify & Complete</button>
              </div>
            </div>
          </div>
        )}
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