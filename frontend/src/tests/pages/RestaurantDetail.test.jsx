import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import RestaurantDetail from '../../pages/RestaurantDetail';
import { CartProvider, useCart } from '../../store/CartContext';

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  useParams: () => ({ id: '5' }),
  useNavigate: () => mockNavigate,
  Link: ({ children, to }) => <a href={to}>{children}</a>,
}), { virtual: true });

const mockRestaurant = {
  id: 5,
  name: 'Taco Loco',
  description: 'Street-style Mexican tacos and burritos',
  averageRating: 3.2,
  cuisineCategoryNames: ['Mexican'],
};

const mockMenu = [
  {
    id: 8,
    name: 'Beef Taco',
    description: 'Spicy beef with salsa',
    price: 4.5,
    restaurantId: 5,
    restaurantName: 'Taco Loco',
  },
  {
    id: 9,
    name: 'Chicken Burrito',
    description: 'Grilled chicken',
    price: 7.0,
    restaurantId: 5,
    restaurantName: 'Taco Loco',
  },
];

const mockFetchResponses = () => {
  global.fetch = jest.fn((url) => {
    if (url.includes('/restaurants/search')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([mockRestaurant]),
      });
    }
    if (url.includes('/menu')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockMenu),
      });
    }
    return Promise.reject(new Error(`Unexpected URL: ${url}`));
  });
};

const CartSeeder = ({ dishToSeed }) => {
  const { addToCart } = useCart();
  React.useEffect(() => {
    if (dishToSeed) {
      addToCart(dishToSeed);
    }
  }, []);
  return null;
};

const renderComponent = async ({ seedDish } = {}) => {
  await act(async () => {
    render(
      <CartProvider>
        {seedDish && <CartSeeder dishToSeed={seedDish} />}
        <RestaurantDetail />
      </CartProvider>
    );
  });
};

describe('RestaurantDetail — add to cart flow', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponses();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test('loads the restaurant and its menu', async () => {
    await renderComponent();

    expect(await screen.findByText('Taco Loco')).toBeInTheDocument();
    expect(await screen.findByText('Beef Taco')).toBeInTheDocument();
    expect(await screen.findByText('Chicken Burrito')).toBeInTheDocument();
  });

  test('shows a confirmation toast when the + Add button is clicked', async () => {
    await renderComponent();

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });

    await act(async () => {
      fireEvent.click(addButtons[0]);
    });

    const toast = screen.getByRole('status');
    expect(toast).toHaveTextContent('"Beef Taco" added to cart');
  });

  test('toast disappears after 2 seconds', async () => {
    await renderComponent();

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });

    jest.useFakeTimers();

    fireEvent.click(addButtons[0]);
    expect(screen.getByRole('status')).toBeInTheDocument();

    act(() => {
      jest.advanceTimersByTime(2000);
    });

    expect(screen.queryByRole('status')).not.toBeInTheDocument();
  });

  test('rapid clicks reset the toast timer instead of cutting it short', async () => {
    await renderComponent();

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });

    jest.useFakeTimers();

    fireEvent.click(addButtons[0]);
    expect(screen.getByRole('status')).toHaveTextContent('Beef Taco');

    act(() => {
      jest.advanceTimersByTime(1500);
    });

    fireEvent.click(addButtons[1]);
    expect(screen.getByRole('status')).toHaveTextContent('Chicken Burrito');

    act(() => {
      jest.advanceTimersByTime(1900);
    });
    expect(screen.getByRole('status')).toBeInTheDocument();

    act(() => {
      jest.advanceTimersByTime(200);
    });
    expect(screen.queryByRole('status')).not.toBeInTheDocument();
  });
});

describe('RestaurantDetail — different restaurant modal', () => {
  const dishFromSushiTokyo = {
    id: 15,
    name: 'Nigiri Platter',
    price: 18.0,
    restaurantId: 3,
    restaurantName: 'Sushi Tokyo',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponses();
  });

  test('shows a confirmation modal when adding a dish from a different restaurant', async () => {
    await renderComponent({ seedDish: dishFromSushiTokyo });

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });

    await act(async () => {
      fireEvent.click(addButtons[0]); // try to add Beef Taco while Sushi Tokyo item is in the cart
    });

    const dialog = screen.getByRole('dialog');
    expect(dialog).toBeInTheDocument();
    expect(dialog).toHaveTextContent('Sushi Tokyo');
    expect(dialog).toHaveTextContent('Taco Loco');
    expect(dialog).toHaveTextContent('one restaurant at a time');
  });

  test('confirming the modal replaces the cart and shows a toast', async () => {
    await renderComponent({ seedDish: dishFromSushiTokyo });

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });
    await act(async () => {
      fireEvent.click(addButtons[0]);
    });

    const confirmButton = screen.getByRole('button', { name: 'Switch restaurants' });
    await act(async () => {
      fireEvent.click(confirmButton);
    });

    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    const toast = screen.getByRole('status');
    expect(toast).toHaveTextContent('Cart cleared');
    expect(toast).toHaveTextContent('Beef Taco');
  });

  test('cancelling the modal leaves the cart unchanged and shows no toast', async () => {
    await renderComponent({ seedDish: dishFromSushiTokyo });

    const addButtons = await screen.findAllByRole('button', { name: '+ Add' });
    await act(async () => {
      fireEvent.click(addButtons[0]);
    });

    const cancelButton = screen.getByRole('button', { name: 'Keep current cart' });
    await act(async () => {
      fireEvent.click(cancelButton);
    });

    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    expect(screen.queryByRole('status')).not.toBeInTheDocument();
  });
});