import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AdminLogin from './pages/AdminLogin';
import AdminDashboard from './pages/AdminDashboard'; // Importamos la nueva pantalla

function App() {
  return (
    <Router>
      <Routes>
        {/* La pantalla de Login */}
        <Route path="/admin/login" element={<AdminLogin />} />
        
        {/* La pantalla segura del Dashboard (RBAC) */}
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        
        {/* Si alguien entra a la raíz "/", lo mandamos al login */}
        <Route path="/" element={<Navigate to="/admin/login" />} />
      </Routes>
    </Router>
  );
}

export default App;