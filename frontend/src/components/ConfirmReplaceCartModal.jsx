import React from 'react';

const ConfirmReplaceCartModal = ({
  currentRestaurantName,
  newRestaurantName,
  onConfirm,
  onCancel,
}) => {
  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="replace-cart-title"
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 2000,
        padding: '20px',
      }}
    >
      <div
        style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '32px',
          maxWidth: '440px',
          width: '100%',
          boxShadow: '0 10px 40px rgba(0,0,0,0.2)',
        }}
      >
        <h2
          id="replace-cart-title"
          style={{ margin: '0 0 16px', fontSize: '1.5rem' }}
        >
          Start a new order?
        </h2>
        <p style={{ margin: '0 0 12px', color: '#333', lineHeight: 1.5 }}>
          You can only order from <strong>one restaurant at a time</strong>.
        </p>
        <p style={{ margin: '0 0 24px', color: '#555', lineHeight: 1.5 }}>
          Your cart currently has items from{' '}
          <strong>{currentRestaurantName}</strong>. Adding this dish from{' '}
          <strong>{newRestaurantName}</strong> will clear your cart.
        </p>
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', flexWrap: 'wrap' }}>
          <button
            type="button"
            onClick={onCancel}
            style={{
              padding: '10px 20px',
              borderRadius: '24px',
              border: '1px solid #ccc',
              backgroundColor: 'white',
              color: '#333',
              fontWeight: 'bold',
              cursor: 'pointer',
            }}
          >
            Keep current cart
          </button>
          <button
            type="button"
            onClick={onConfirm}
            style={{
              padding: '10px 20px',
              borderRadius: '24px',
              border: 'none',
              backgroundColor: '#00cc66',
              color: 'white',
              fontWeight: 'bold',
              cursor: 'pointer',
            }}
          >
            Switch restaurants
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmReplaceCartModal;