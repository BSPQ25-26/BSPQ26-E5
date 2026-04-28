import React from 'react';
import { render, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import { CartProvider, useCart } from '../../store/CartContext';

const tacoLocoBeefTaco = {
  id: 8,
  name: 'Beef Taco',
  price: 4.5,
  restaurantId: 5,
  restaurantName: 'Taco Loco',
};

const tacoLocoChickenBurrito = {
  id: 9,
  name: 'Chicken Burrito',
  price: 7.0,
  restaurantId: 5,
  restaurantName: 'Taco Loco',
};

const sushiTokyoNigiri = {
  id: 15,
  name: 'Nigiri Platter',
  price: 18.0,
  restaurantId: 3,
  restaurantName: 'Sushi Tokyo',
};

let cart;

const CartCapture = () => {
  cart = useCart();
  return null;
};

const renderCart = () => {
  render(
    <CartProvider>
      <CartCapture />
    </CartProvider>
  );
};

describe('CartContext', () => {
  beforeEach(() => {
    cart = null;
    renderCart();
  });

  test('starts empty with no restaurant', () => {
    expect(cart.items).toHaveLength(0);
    expect(cart.restaurantId).toBeNull();
    expect(cart.restaurantName).toBeNull();
  });

  test('adds a dish from an empty cart and records the restaurant', () => {
    let result;
    act(() => {
      result = cart.addToCart(tacoLocoBeefTaco);
    });

    expect(result).toEqual({ success: true });
    expect(cart.items).toHaveLength(1);
    expect(cart.items[0].name).toBe('Beef Taco');
    expect(cart.restaurantId).toBe(5);
    expect(cart.restaurantName).toBe('Taco Loco');
  });

  test('adds a second dish from the same restaurant', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    let result;
    act(() => {
      result = cart.addToCart(tacoLocoChickenBurrito);
    });

    expect(result).toEqual({ success: true });
    expect(cart.items).toHaveLength(2);
    expect(cart.restaurantId).toBe(5);
  });

  test('increments quantity when adding the same dish twice', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    expect(cart.items).toHaveLength(1);
    expect(cart.items[0].quantity).toBe(2);
  });

  test('refuses to add a dish from a different restaurant', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    let result;
    act(() => {
      result = cart.addToCart(sushiTokyoNigiri);
    });

    expect(result).toEqual({
      success: false,
      reason: 'different_restaurant',
      currentRestaurantName: 'Taco Loco',
      attemptedRestaurantName: 'Sushi Tokyo',
    });
    expect(cart.items).toHaveLength(1);
    expect(cart.items[0].name).toBe('Beef Taco');
    expect(cart.restaurantId).toBe(5);
  });

  test('replaceCartWith clears the cart and switches restaurant', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });
    act(() => {
      cart.addToCart(tacoLocoChickenBurrito);
    });

    let result;
    act(() => {
      result = cart.replaceCartWith(sushiTokyoNigiri);
    });

    expect(result).toEqual({ success: true });
    expect(cart.items).toHaveLength(1);
    expect(cart.items[0].name).toBe('Nigiri Platter');
    expect(cart.restaurantId).toBe(3);
    expect(cart.restaurantName).toBe('Sushi Tokyo');
  });

  test('clearCart resets restaurant info', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    act(() => {
      cart.clearCart();
    });

    expect(cart.items).toHaveLength(0);
    expect(cart.restaurantId).toBeNull();
    expect(cart.restaurantName).toBeNull();
  });

  test('removeFromCart resets restaurant info when cart becomes empty', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    act(() => {
      cart.removeFromCart(tacoLocoBeefTaco.id);
    });

    expect(cart.items).toHaveLength(0);
    expect(cart.restaurantId).toBeNull();
    expect(cart.restaurantName).toBeNull();
  });

  test('removeFromCart keeps restaurant info when other items remain', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });
    act(() => {
      cart.addToCart(tacoLocoChickenBurrito);
    });

    act(() => {
      cart.removeFromCart(tacoLocoBeefTaco.id);
    });

    expect(cart.items).toHaveLength(1);
    expect(cart.restaurantId).toBe(5);
    expect(cart.restaurantName).toBe('Taco Loco');
  });

  test('decreaseQuantity resets restaurant info when cart becomes empty', () => {
    act(() => {
      cart.addToCart(tacoLocoBeefTaco);
    });

    act(() => {
      cart.decreaseQuantity(tacoLocoBeefTaco.id);
    });

    expect(cart.items).toHaveLength(0);
    expect(cart.restaurantId).toBeNull();
    expect(cart.restaurantName).toBeNull();
  });
});