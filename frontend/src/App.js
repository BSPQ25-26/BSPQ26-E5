import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import RegisterCustomer from './pages/RegisterCustomer';
import RegisterRestaurant from './pages/RegisterRestaurant';

import './styles/Register.css'; 
import './styles/Home.css';


function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/register-customer" element={<RegisterCustomer />} />
                <Route path="/register-restaurant" element={<RegisterRestaurant />} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </Router>
    );
}

export default App;