// src/pages/RiderDashboard.jsx
import React, { useState } from 'react';
import RiderRejectionModal from '../components/RiderRejectionModal';

function RiderDashboard() {
  const [showRejectionModal, setShowRejectionModal] = useState(false);

  // Simulamos que el sistema le ha asignado este pedido
  const currentAssignedOrder = {
    id: "9092",
    restaurant: "Pizzería Luigi",
    address: "Calle Falsa 123",
  };

  const handleRejectionSubmit = (orderId, reason) => {
    // Aquí en el futuro irá el fetch() al backend
    alert(`[Simulated Backend Call]\nRejecting Order: ${orderId}\nReason: ${reason}`);
    setShowRejectionModal(false);
  };

  return (
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Rider Dashboard</h2>
      
      <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', marginTop: '20px', maxWidth: '400px' }}>
        <h3>Active Order: #{currentAssignedOrder.id}</h3>
        <p><strong>Restaurant:</strong> {currentAssignedOrder.restaurant}</p>
        <p><strong>Delivery Address:</strong> {currentAssignedOrder.address}</p>
        
        <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
          <button style={{ padding: '10px', background: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
            Accept & Start
          </button>
          
          {/* Aquí está el trigger de tu Modal */}
          <button 
            onClick={() => setShowRejectionModal(true)}
            style={{ padding: '10px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
          >
            Reject Order
          </button>
        </div>
      </div>

      {showRejectionModal && (
        <RiderRejectionModal 
          orderId={currentAssignedOrder.id} 
          onClose={() => setShowRejectionModal(false)} 
          onSubmit={handleRejectionSubmit} 
        />
      )}
    </div>
  );
}

export default RiderDashboard;