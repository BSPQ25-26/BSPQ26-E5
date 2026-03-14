import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const [restaurants, setRestaurants] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('adminToken');
        if (!token) {
            navigate('/admin/login');
            return;
        }

        // Llamamos a la nueva ruta que acabas de crear en Java
        const fetchRestaurants = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/restaurants/all', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    setRestaurants(data); // Guardamos los datos
                } else {
                    setError('Acceso denegado o error en el servidor');
                }
            } catch (error) {
                setError("Error conectando con el servidor");
            }
        };

        fetchRestaurants();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('adminToken');
        navigate('/admin/login');
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Panel de Control del Administrador 🚀</h1>
                <button 
                    onClick={handleLogout} 
                    style={{ padding: '10px 20px', background: 'red', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    Cerrar Sesión
                </button>
            </div>
            
            <h2 style={{ marginTop: '30px' }}>Gestión de Restaurantes</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}

            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                <thead>
                    <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre del Local</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {restaurants.length === 0 ? (
                        <tr>
                            <td colSpan="3" style={{ padding: '20px', textAlign: 'center', border: '1px solid #ddd' }}>
                                No hay restaurantes registrados actualmente.
                            </td>
                        </tr>
                    ) : (
                        restaurants.map((rest, index) => (
                            <tr key={index} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.id || index + 1}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.name || 'Sin nombre'}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>
                                    <button style={{ padding: '5px 10px', background: '#007bff', color: 'white', border: 'none', borderRadius: '3px', cursor: 'pointer' }}>
                                        Ver detalles
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;