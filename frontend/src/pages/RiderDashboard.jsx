import React, { useState } from 'react';
import RiderRejectionModal from '../components/RiderRejectionModal';
import '../assets/css/RiderDashboard.css';

function RiderDashboard() {
  const [showRejectionModal, setShowRejectionModal] = useState(false);
  const [orderToReject, setOrderToReject] = useState(null);

  const [activeOrders, setActiveOrders] = useState([
    {
      id: "9092",
      restaurant: "McDonald's",
      address: "Calle Falsa 123",
    },
    {
      id: "9093",
      restaurant: "McDonald's",
      address: "Avenida Siempreviva 742",
    }
  ]);

  const triggerRejection = (orderId) => {
    setOrderToReject(orderId);
    setShowRejectionModal(true);
  };

  const handleRejectionSubmit = (orderId, reason) => {
    alert(`[Simulated Backend Call]\nRejecting Order: ${orderId}\nReason: ${reason}`);
    
    setShowRejectionModal(false);
    setOrderToReject(null);

    setActiveOrders(prevOrders => prevOrders.filter(order => order.id !== orderId));
  };

  return (
    // Usamos React.Fragment (<>) para devolver dos elementos raíz (header y main)
    <>
      {/* --- NUEVO: Estructura del Header alineada con Home.jsx --- */}
      <header className="rider-header">
        <h1 className="header-brand">JustOrder</h1>
        
        {/* Botón de Perfil circular */}
        <button className="profile-button">
          {/* Imagen del avatar (el muñeco). He usado un placeholder estándar de internet */}
          <img 
            src="https://www.w3schools.com/howto/img_avatar.png" 
            alt="User Profile" 
            className="profile-icon" 
          />
        </button>
      </header>

      {/* Contenedor principal (con la clase antigua ajustada en CSS) */}
      <main className="dashboard-container">
        {/* Borramos el <h2>Dashboard</h2> antiguo, ya tenemos el header */}
        
        {activeOrders.length > 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', width: '100%', alignItems: 'center' }}>
            
            {activeOrders.map(order => (
              <div key={order.id} className="order-card">
                <h3>Active Order: #{order.id}</h3>
                
                <div className="order-details">
                  <p><strong>Restaurant:</strong> {order.restaurant}</p>
                  <p><strong>Delivery Address:</strong> {order.address}</p>
                </div>
                
                <div className="dashboard-actions">
                  <button className="btn-accept">
                    Accept & Start
                  </button>
                  
                  <button 
                    className="btn-reject-trigger"
                    onClick={() => triggerRejection(order.id)}
                  >
                    Reject Order
                  </button>
                </div>
              </div>
            ))}

          </div>
        ) : (
          <div className="order-card" style={{ textAlign: 'center', borderTopColor: '#aaa' }}>
            <h3 style={{ borderBottom: 'none' }}>No active orders</h3>
            <p>Waiting for new assignments...</p>
            <div style={{ marginTop: '20px', width: '40px', height: '40px', border: '4px solid #f3f3f3', borderTop: '4px solid #4CAF50', borderRadius: '50%', animation: 'spin 1s linear infinite', margin: '0 auto' }}></div>
            <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
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
      </main>
    </>
  );
}

export default RiderDashboard;