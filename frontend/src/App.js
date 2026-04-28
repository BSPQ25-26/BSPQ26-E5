import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import RegisterCustomer from "./pages/RegisterCustomer";
import RegisterRestaurant from "./pages/RegisterRestaurant";
import RegisterRider from "./pages/RegisterRider";
import CheckoutPage from "./pages/CheckoutPage";
import AdminLogin from "./pages/AdminLogin";
import AdminDashboard from "./pages/AdminDashboard";
import MenuEditor from "./pages/MenuEditor";
import OrderStatusPage from "./pages/OrderStatusPage";
import RiderDashboard from "./pages/RiderDashboard";
import CustomerMarketplace from "./pages/CustomerMarketplace";
import CustomerInformationPage from "./pages/CustomerInformationPage";
import RestaurantDetail from './pages/RestaurantDetail';
import RestaurantOrderConfirmationView from "./pages/RestaurantOrderConfirmationView";
import RestaurantInformationPage from "./pages/RestaurantInformationPage";
import RiderInformationPage from "./pages/RiderInformationPage";
import { CartProvider } from "./store/CartContext";
import "./assets/css/Home.css";
import "./assets/css/Register.css";
import "./assets/css/MenuEditor.css";

function App() {
  return (
    <CartProvider>
      <BrowserRouter>
        <Routes>
          {/* General Application Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/register-customer" element={<RegisterCustomer />} />
          <Route path="/register-restaurant" element={<RegisterRestaurant />} />
          <Route path="/register-rider" element={<RegisterRider />} />
          <Route path="/checkout" element={<CheckoutPage />} />

          {/* Administrator Routes */}
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin/dashboard" element={<AdminDashboard />} />

          {/* Restaurant Routes */}
          <Route path="/restaurants/:restaurantId/menu-editor" element={<MenuEditor />} />
          <Route path="/restaurants/:id" element={<RestaurantDetail />} />

          {/* Order Routes */}
          <Route path="/orders" element={<OrderStatusPage />} />
          <Route path="/orders/confirmation" element={<RestaurantOrderConfirmationView />} />

          {/* User Profile and Dashboard Routes */}
          <Route path="/rider-dashboard" element={<RiderDashboard />} />
          <Route path="/customer-marketplace" element={<CustomerMarketplace />} />
          <Route path="/customer/profile" element={<CustomerInformationPage />} />
          <Route path="/restaurant/profile" element={<RestaurantInformationPage />} />
          <Route path="/rider/profile" element={<RiderInformationPage />} />

          {/* Fallback: Redirect to home if the URL does not exist */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;