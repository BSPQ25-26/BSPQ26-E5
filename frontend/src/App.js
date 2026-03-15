import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import RegisterCustomer from './pages/RegisterCustomer';

import './styles/Register.css'; 

function App() {
    return (
    <Router>
        <Routes>
            {/* Main page */}
            <Route path="/" element={<Navigate to="/register-customer" />} />
            {/* URL for customer registration */}
            <Route path="/register-customer" element={<RegisterCustomer />} />
        </Routes>
    </Router>
  );
}

export default App;