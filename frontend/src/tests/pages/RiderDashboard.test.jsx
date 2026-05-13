import { render, screen, fireEvent, waitFor, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import RiderDashboard from "../../pages/RiderDashboard";
import { BrowserRouter } from "react-router-dom";
import { fetchThroughNode } from "../utils/fetchThroughNode";

// Mocking useNavigate
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
  Link: ({ children, to }) => <a href={to}>{children}</a>,
  BrowserRouter: ({ children }) => <div>{children}</div>,
}), { virtual: true });

describe("RiderDashboard Live Backend Integration", () => {
  
  const RIDER_ID = 1; // carlos.rider@test.com
  const API_BASE = "http://localhost:8080/api";

  beforeEach(async () => {
    jest.clearAllMocks();
    // Using live backend fetch
    global.fetch = jest.fn(fetchThroughNode);
    
    // Suppress logs that happen after tests finish due to async useEffect in component
    jest.spyOn(console, 'log').mockImplementation(() => {});
    jest.spyOn(console, 'warn').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test("full rider workflow: load, accept, advance status, and deliver with PIN", async () => {
    // 1. Render dashboard
    await act(async () => {
      render(
        <BrowserRouter>
          <RiderDashboard />
        </BrowserRouter>
      );
    });

    // 2. Wait for data to load (either orders appear or the empty message appears)
    await waitFor(() => {
      const hasOrders = screen.queryAllByText(/Order #/).length > 0;
      const noOrdersMessage = screen.queryByText(/No new requests at the moment/i);
      const noAssignedMessage = screen.queryByText(/You have no assigned orders/i);
      // Wait until we have either data or a confirmation message that data was checked
      expect(hasOrders || (noOrdersMessage && noAssignedMessage)).toBeTruthy();
    }, { timeout: 5000 });

    // 3. Perform acceptance test if an available order exists
    const acceptButtons = screen.queryAllByText(/Accept Order/i);
    if (acceptButtons.length > 0) {
      const firstOrderCard = acceptButtons[0].closest('.order-card');
      const firstOrderIdText = firstOrderCard.querySelector('h3').textContent;
      const orderId = firstOrderIdText.replace('Order #', '');

      // Accept order
      fireEvent.click(acceptButtons[0]);

      // Verify it moved to Assigned Orders
      await waitFor(() => {
        const assignedSection = screen.getByText(/My Assigned Orders/i).closest('div');
        expect(assignedSection).toHaveTextContent(`Order #${orderId}`);
      }, { timeout: 5000 });

      // 4. Advance status
      const assignedOrderCard = screen.getByText(`Order #${orderId}`).closest('.order-card');
      const statusSelect = assignedOrderCard.querySelector('select');
      
      fireEvent.change(statusSelect, { target: { value: 'Preparing' } });
      await waitFor(() => {
        expect(statusSelect.value).toBe('Preparing');
      });

      fireEvent.change(statusSelect, { target: { value: 'Out for Delivery' } });
      await waitFor(() => {
        expect(statusSelect.value).toBe('Out for Delivery');
      });

      // 5. Complete delivery with PIN (modal check)
      fireEvent.change(statusSelect, { target: { value: 'Delivered' } });
      expect(screen.getByText(/Verify Delivery PIN/i)).toBeInTheDocument();

      const pinInput = screen.getByPlaceholderText("000000");
      fireEvent.change(pinInput, { target: { value: "123456" } });

      const completeButton = screen.getByText(/Verify & Complete/i);
      fireEvent.click(completeButton);

      // We wait for the modal to close or an alert
      await waitFor(() => {
        expect(screen.queryByText(/Verify Delivery PIN/i)).not.toBeInTheDocument();
      });
    }

    // Flush any pending async logs from useEffect
    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 100));
    });
  });

  test("double-claim prevention live check", async () => {
    // To test this live, we'd need to try assigning the same order to another rider.
    // We can simulate this by manually calling the API for another rider ID.
    
    const availableOrdersResponse = await fetchThroughNode(`${API_BASE}/riders/orders/available`);
    const availableOrders = await availableOrdersResponse.json();

    if (availableOrders.length > 0) {
        const targetOrder = availableOrders[0];
        
        // 1. Rider 1 accepts the order
        await fetchThroughNode(`${API_BASE}/riders/1/orders/${targetOrder.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(targetOrder)
        });

        // 2. Try to accept it with Rider 2 (if exists, or just a different ID)
        const response2 = await fetchThroughNode(`${API_BASE}/riders/2/orders/${targetOrder.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(targetOrder)
        });

        // 3. Verify it fails with 400 Bad Request
        expect(response2.status).toBe(400);
    }
  });
});
