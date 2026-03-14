import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    // 1. Hemos eliminado las variables 'error' y 'setError' que no usábamos
    const [restaurants, setRestaurants] = useState([]);
    const [showForm, setShowForm] = useState(false);
    
    const [formData, setFormData] = useState({
        name: '', description: '', email: '', phone: '', password: ''
    });

    const navigate = useNavigate();
    const token = localStorage.getItem('adminToken');

    const fetchRestaurants = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/restaurants/all', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setRestaurants(data);
            }
        } catch (error) {
            console.error("Error al cargar restaurantes", error);
        }
    };

    useEffect(() => {
        if (!token) {
            navigate('/admin/login');
            return;
        }
        fetchRestaurants();
        // 2. Este comentario silencia la advertencia del terminal
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token]);

    const handleCreateRestaurant = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/api/restaurants/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert("¡Restaurante creado con éxito!");
                setShowForm(false); 
                setFormData({ name: '', description: '', email: '', phone: '', password: '' }); 
                fetchRestaurants(); 
            } else {
                alert("Error al crear el restaurante");
            }
        } catch (error) {
            alert("Fallo de conexión");
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('adminToken');
        navigate('/admin/login');
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Panel de Control del Administrador 🚀</h1>
                <button onClick={handleLogout} style={{ padding: '10px 20px', background: 'red', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Cerrar Sesión
                </button>
            </div>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '30px' }}>
                <h2>Gestión de Restaurantes</h2>
                <button 
                    onClick={() => setShowForm(!showForm)} 
                    style={{ padding: '10px', background: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    {showForm ? 'Cancelar' : '+ Añadir Restaurante'}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleCreateRestaurant} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                    <h3>Crear Nuevo Restaurante</h3>
                    <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                        <input type="text" placeholder="Nombre" required value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                        <input type="email" placeholder="Email" required value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                    </div>
                    <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                        <input type="text" placeholder="Teléfono" required value={formData.phone} onChange={(e) => setFormData({...formData, phone: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                        <input type="password" placeholder="Contraseña" required value={formData.password} onChange={(e) => setFormData({...formData, password: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                    </div>
                    <input type="text" placeholder="Descripción breve" required value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} style={{ padding: '8px', width: '100%', marginBottom: '10px', boxSizing: 'border-box' }} />
                    
                    <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%' }}>
                        Guardar Restaurante
                    </button>
                </form>
            )}

            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                <thead>
                    <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Email</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Teléfono</th>
                    </tr>
                </thead>
                <tbody>
                    {restaurants.length === 0 ? (
                        <tr><td colSpan="4" style={{ padding: '20px', textAlign: 'center', border: '1px solid #ddd' }}>No hay restaurantes.</td></tr>
                    ) : (
                        restaurants.map((rest, index) => (
                            <tr key={index} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.id}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.name}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.email}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.phone}</td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;