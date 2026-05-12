import React, { useState } from 'react';
import '../assets/css/RiderRejectionModal.css';

const RestaurantRejectionModal = ({ orderId, onClose, onSubmit }) => {
  const [reason, setReason] = useState("");

  const handleConfirm = () => {
    onSubmit(orderId, reason);
  };

  const isButtonDisabled = reason.trim() === "";

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3>Reject Order #{orderId}</h3>
        <p className="modal-subtitle">Please explain why you are rejecting this order. The customer will be notified.</p>

        <textarea 
          className="custom-reason-input"
          rows="4" 
          placeholder="e.g. We ran out of pizza dough..."
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          style={{ 
            width: '100%', 
            padding: '12px', 
            marginTop: '15px', 
            borderRadius: '8px', 
            border: '1px solid #ccc',
            fontFamily: 'inherit',
            resize: 'vertical'
          }}
        />

        <div className="modal-actions">
          <button className="btn-cancel" onClick={onClose}>
            Cancel
          </button>
          <button 
            className="btn-reject" 
            onClick={handleConfirm}
            disabled={isButtonDisabled}
          >
            Confirm Rejection
          </button>
        </div>
      </div>
    </div>
  );
};

export default RestaurantRejectionModal;