// src/components/RiderRejectionModal.jsx
import React, { useState } from 'react';
import '../assets/css/RiderRejectionModal.css';

const REASONS = [
  "Vehicle breakdown",
  "Distance too far",
  "Restaurant issue",
  "Heavy traffic",
  "Other reason"
];

const RiderRejectionModal = ({ orderId, onClose, onSubmit }) => {
  const [selectedReason, setSelectedReason] = useState("");
  const [customReason, setCustomReason] = useState("");

  const handleConfirm = () => {
    const finalReason = selectedReason === "Other reason" ? customReason : selectedReason; 
    onSubmit(orderId, finalReason);
  };
const isButtonDisabled = !selectedReason || (selectedReason === "Other reason" && customReason.trim() === "");
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3>Reject Order #{orderId}</h3>
        <p className="modal-subtitle">Please select a reason so we can reassign this order quickly.</p>

        <div className="reason-list">
          {REASONS.map((reason) => (
            <div 
              key={reason}
              className={`reason-option ${selectedReason === reason ? 'selected' : ''}`}
              onClick={() => setSelectedReason(reason)}
            >
              <input 
                type="radio" 
                name="rejectionReason"
                checked={selectedReason === reason} 
                onChange={() => setSelectedReason(reason)}
              />
              <label>{reason}</label>
            </div>
          ))}
        </div>

        {selectedReason === "Other reason" && (
          <textarea 
            className="custom-reason-input"
            rows="3" 
            placeholder="Please specify the reason..."
            value={customReason}
            onChange={(e) => setCustomReason(e.target.value)}
          />
        )}

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

export default RiderRejectionModal;