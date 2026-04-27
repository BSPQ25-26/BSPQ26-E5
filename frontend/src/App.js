import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import RegisterCustomer from "./pages/RegisterCustomer";
import RegisterRestaurant from "./pages/RegisterRestaurant";
import RegisterRider from "./pages/RegisterRider";
import CheckoutPage from "./pages/CheckoutPage";
import MenuEditor from "./pages/MenuEditor";
import OrderStatusPage from "./pages/OrderStatusPage";
import RiderDashboard from "./pages/RiderDashboard";
import CustomerMarketplace from "./pages/CustomerMarketplace";
import CustomerInformationPage from "./pages/CustomerInformationPage";
import RestaurantDetail from './pages/RestaurantDetail';
import RestaurantOrderConfirmationView from "./pages/RestaurantOrderConfirmationView";
import RestaurantDashboard from "./pages/RestaurantDashboard";
import { CartProvider } from "./store/CartContext";
import "./assets/css/Home.css";
import "./assets/css/Register.css";
import "./assets/css/MenuEditor.css";

function App() {
  return (
    <CartProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register-customer" element={<RegisterCustomer />} />
          <Route path="/register-restaurant" element={<RegisterRestaurant />} />
          <Route path="/register-rider" element={<RegisterRider />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/restaurants/:restaurantId/menu-editor" element={<MenuEditor />} />
          <Route path="/orders" element={<OrderStatusPage />} />
          <Route path="/orders/confirmation" element={<RestaurantOrderConfirmationView />} />
          <Route path="/rider-dashboard" element={<RiderDashboard />} />
          <Route path="/customer-marketplace" element={<CustomerMarketplace />} />
          <Route path="/customer/profile" element={<CustomerInformationPage />} />
          <Route path="/restaurants/:id" element={<RestaurantDetail />} />
          <Route path="/restaurant-dashboard" element={<RestaurantDashboard />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;