import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const [restaurants, setRestaurants] = useState([]);
    const [showForm, setShowForm] = useState(false);
    
    // NUEVO: Variable para saber si estamos creando (null) o editando (un ID)
    const [editingId, setEditingId] = useState(null);
    
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
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token]);

    // NUEVA FUNCIÓN: Unificamos Crear y Modificar en el mismo botón de "Guardar"
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Decidimos la ruta y el método dependiendo de si estamos editando o creando
        const url = editingId 
            ? `http://localhost:8080/api/restaurants/update/${editingId}`
            : 'http://localhost:8080/api/restaurants/create';
            
        const method = editingId ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert(editingId ? "¡Restaurante modificado con éxito!" : "¡Restaurante creado con éxito!");
                setShowForm(false); 
                setEditingId(null);
                setFormData({ name: '', description: '', email: '', phone: '', password: '' }); 
                fetchRestaurants(); 
            } else {
                alert("Error al guardar el restaurante");
            }
        } catch (error) {
            alert("Fallo de conexión");
        }
    };

    // NUEVA FUNCIÓN: Al pulsar "Editar", rellenamos el formulario con sus datos
    const handleEditClick = (restaurant) => {
        setEditingId(restaurant.id);
        setFormData({
            name: restaurant.name || '',
            description: restaurant.description || '',
            email: restaurant.email || '',
            phone: restaurant.phone || '',
            password: '' // La dejamos vacía por seguridad
        });
        setShowForm(true);
    };

    const handleDeleteRestaurant = async (id) => {
        if (!window.confirm("¿Estás seguro de que quieres eliminar este restaurante de la plataforma?")) return;

        try {
            const response = await fetch(`http://localhost:8080/api/restaurants/delete/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) fetchRestaurants();
            else alert("Error al eliminar el restaurante");
        } catch (error) {
            alert("Fallo de conexión al intentar eliminar");
        }
    };

    // Botón principal de "+ Añadir Restaurante" (limpia el formulario)
    const handleAddNewClick = () => {
        setEditingId(null);
        setFormData({ name: '', description: '', email: '', phone: '', password: '' });
        setShowForm(!showForm);
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
                    onClick={handleAddNewClick} 
                    style={{ padding: '10px', background: showForm && !editingId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    {showForm && !editingId ? 'Cancelar Creación' : '+ Añadir Restaurante'}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                    <h3>{editingId ? 'Modificar Restaurante' : 'Crear Nuevo Restaurante'}</h3>
                    <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                        <input type="text" placeholder="Nombre" required value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                        <input type="email" placeholder="Email" required value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                    </div>
                    <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                        <input type="text" placeholder="Teléfono" required value={formData.phone} onChange={(e) => setFormData({...formData, phone: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                        <input type="password" placeholder={editingId ? "Nueva Contraseña (Opcional)" : "Contraseña"} required={!editingId} value={formData.password} onChange={(e) => setFormData({...formData, password: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                    </div>
                    <input type="text" placeholder="Descripción breve" required value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} style={{ padding: '8px', width: '100%', marginBottom: '10px', boxSizing: 'border-box' }} />
                    
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>
                            {editingId ? 'Guardar Cambios' : 'Guardar Restaurante'}
                        </button>
                        {editingId && (
                            <button type="button" onClick={() => setShowForm(false)} style={{ padding: '10px 20px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                Cancelar
                            </button>
                        )}
                    </div>
                </form>
            )}

            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                <thead>
                    <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Email</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd' }}>Teléfono</th>
                        <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {restaurants.length === 0 ? (
                        <tr><td colSpan="5" style={{ padding: '20px', textAlign: 'center', border: '1px solid #ddd' }}>No hay restaurantes.</td></tr>
                    ) : (
                        restaurants.map((rest, index) => (
                            <tr key={index} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.id}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.name}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.email}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.phone}</td>
                                <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                    <button 
                                        onClick={() => handleEditClick(rest)}
                                        style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}
                                    >
                                        Editar
                                    </button>
                                    <button 
                                        onClick={() => handleDeleteRestaurant(rest.id)}
                                        style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                                    >
                                        Eliminar
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