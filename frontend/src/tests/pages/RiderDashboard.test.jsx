import { render, screen, fireEvent, waitFor, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import RiderDashboard from "../../pages/RiderDashboard";
import { BrowserRouter } from "react-router-dom";
import { fetchThroughNode } from "../utils/fetchThroughNode";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
  Link: ({ children, to }) => <a href={to}>{children}</a>,
  BrowserRouter: ({ children }) => <div>{children}</div>,
}), { virtual: true });

describe("RiderDashboard Live Backend Integration", () => {
  
  const RIDER_ID = 1; 
  const API_BASE = "http://localhost:8080/api";

  beforeEach(async () => {
    jest.clearAllMocks();
    global.fetch = jest.fn(fetchThroughNode);
    
    jest.spyOn(console, 'log').mockImplementation(() => {});
    jest.spyOn(console, 'warn').mockImplementation(() => {});
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test("full rider workflow: load, accept, advance status, and deliver with PIN", async () => {
    await act(async () => {
      render(
        <BrowserRouter>
          <RiderDashboard />
        </BrowserRouter>
      );
    });

    await waitFor(() => {
      const hasOrders = screen.queryAllByText(/Order #/).length > 0;
      const noOrdersMessage = screen.queryByText(/No new requests at the moment/i);
      const noAssignedMessage = screen.queryByText(/You have no assigned orders/i);
      expect(hasOrders || (noOrdersMessage && noAssignedMessage)).toBeTruthy();
    }, { timeout: 5000 });

    const acceptButtons = screen.queryAllByText(/Accept Order/i);
    if (acceptButtons.length > 0) {
      const firstOrderCard = acceptButtons[0].closest('.order-card');
      const firstOrderIdText = firstOrderCard.querySelector('h3').textContent;
      const orderId = firstOrderIdText.replace('Order #', '');

      fireEvent.click(acceptButtons[0]);

      await waitFor(() => {
        const assignedSection = screen.getByText(/My Assigned Orders/i).closest('div');
        expect(assignedSection).toHaveTextContent(`Order #${orderId}`);
      }, { timeout: 5000 });

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

      fireEvent.change(statusSelect, { target: { value: 'Delivered' } });
      expect(screen.getByText(/Verify Delivery PIN/i)).toBeInTheDocument();

      const pinInput = screen.getByPlaceholderText("000000");
      fireEvent.change(pinInput, { target: { value: "123456" } });

      const completeButton = screen.getByText(/Verify & Complete/i);
      fireEvent.click(completeButton);

      await waitFor(() => {
        expect(screen.queryByText(/Verify Delivery PIN/i)).not.toBeInTheDocument();
      });
    }

    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 100));
    });
  });

  test("double-claim prevention live check", async () => {
    const availableOrdersResponse = await fetchThroughNode(`${API_BASE}/riders/orders/available`);
    
    const text = await availableOrdersResponse.text();
    const availableOrders = text ? JSON.parse(text) : [];

    if (availableOrders.length > 0) {
        const targetOrder = availableOrders[0];
        
        await fetchThroughNode(`${API_BASE}/riders/1/orders/${targetOrder.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(targetOrder)
        });

        const response2 = await fetchThroughNode(`${API_BASE}/riders/2/orders/${targetOrder.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(targetOrder)
        });

        expect(response2.status).toBe(400);
    }
  });
});