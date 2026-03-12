import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        // 1. Al cargar la página, buscamos la "llave" (Token) que guardamos en el Login
        const token = localStorage.getItem('adminToken');

        // Si no hay token, lo echamos de vuelta al Login (¡Seguridad al poder!)
        if (!token) {
            navigate('/admin/login');
            return;
        }

        // 2. Si hay token, llamamos a la ruta protegida de tu backend de Java
        const fetchDashboardData = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/admin/dashboard', {
                    headers: {
                        'Authorization': `Bearer ${token}` // Enseñamos el token al "portero" de Spring Boot
                    }
                });

                if (response.ok) {
                    const data = await response.text();
                    setMessage(data); // El mensaje de éxito que pusimos en el AdminController de Java
                } else {
                    // Si el token es inválido o caducado
                    localStorage.removeItem('adminToken');
                    navigate('/admin/login');
                }
            } catch (error) {
                console.error("Error conectando con el servidor", error);
            }
        };

        fetchDashboardData();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('adminToken'); // Tiramos la llave a la basura
        navigate('/admin/login'); // Volvemos al login
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <h1>Panel de Control del Administrador 🚀</h1>
            <div style={{ padding: '20px', background: '#d4edda', color: '#155724', borderRadius: '5px', marginTop: '20px' }}>
                <strong>Mensaje del Servidor Seguro: </strong> {message ? message : 'Cargando...'}
            </div>
            
            <button 
                onClick={handleLogout} 
                style={{ marginTop: '30px', padding: '10px 20px', background: 'red', color: 'white', border: 'none', cursor: 'pointer' }}
            >
                Cerrar Sesión
            </button>
        </div>
    );
};

export default AdminDashboard;