import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import RegisterCustomer from "./pages/RegisterCustomer";
import RegisterRestaurant from "./pages/RegisterRestaurant";
import RegisterRider from "./pages/RegisterRider";
import CheckoutPage from "./pages/CheckoutPage";
import { CartProvider } from "./store/CartContext";
import "./assets/css/Home.css";
import "./assets/css/Register.css";

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
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;
