import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    // ESTADOS PARA PESTAÑAS
    const [activeTab, setActiveTab] = useState('restaurants'); // 'restaurants' o 'riders'

    // ESTADOS PARA RESTAURANTES
    const [restaurants, setRestaurants] = useState([]);
    const [showRestForm, setShowRestForm] = useState(false);
    const [editingRestId, setEditingRestId] = useState(null);
    const [restFormData, setRestFormData] = useState({ name: '', description: '', email: '', phone: '', password: '' });

    // ESTADOS PARA REPARTIDORES (RIDERS)
    const [riders, setRiders] = useState([]);
    const [showRiderForm, setShowRiderForm] = useState(false);
    const [editingRiderId, setEditingRiderId] = useState(null);
    const [riderFormData, setRiderFormData] = useState({ name: '', email: '', phoneNumber: '', password: '' });

    const navigate = useNavigate();
    const token = localStorage.getItem('adminToken');

    // --- FUNCIONES DE CARGA ---
    const fetchRestaurants = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/restaurants/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setRestaurants(await response.json());
        } catch (error) { console.error("Error cargando restaurantes"); }
    };

    const fetchRiders = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/riders/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setRiders(await response.json());
        } catch (error) { console.error("Error cargando riders"); }
    };

    useEffect(() => {
        if (!token) { navigate('/admin/login'); return; }
        if (activeTab === 'restaurants') fetchRestaurants();
        if (activeTab === 'riders') fetchRiders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token, activeTab]);

    // --- FUNCIONES DE RESTAURANTES ---
    const handleRestSubmit = async (e) => {
        e.preventDefault();
        const url = editingRestId ? `http://localhost:8080/api/restaurants/update/${editingRestId}` : 'http://localhost:8080/api/restaurants/create';
        const method = editingRestId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(restFormData) });
            if (res.ok) {
                setShowRestForm(false); setEditingRestId(null);
                setRestFormData({ name: '', description: '', email: '', phone: '', password: '' });
                fetchRestaurants();
            } else alert("Error guardando restaurante");
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteRestaurant = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar este restaurante?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/restaurants/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchRestaurants();
        } catch (error) { alert("Fallo al eliminar"); }
    };

    // --- FUNCIONES DE RIDERS ---
    const handleRiderSubmit = async (e) => {
        e.preventDefault();
        const url = editingRiderId ? `http://localhost:8080/api/riders/update/${editingRiderId}` : 'http://localhost:8080/api/riders/create';
        const method = editingRiderId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(riderFormData) });
            if (res.ok) {
                setShowRiderForm(false); setEditingRiderId(null);
                setRiderFormData({ name: '', email: '', phoneNumber: '', password: '' });
                fetchRiders();
            } else alert("Error guardando repartidor");
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteRider = async (id) => {
        if (!window.confirm("¿Seguro que quieres despedir a este repartidor?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/riders/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchRiders();
        } catch (error) { alert("Fallo al eliminar"); }
    };

    const handleLogout = () => { localStorage.removeItem('adminToken'); navigate('/admin/login'); };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1>Panel de Control del Administrador 🚀</h1>
                <button onClick={handleLogout} style={{ padding: '10px 20px', background: 'red', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cerrar Sesión</button>
            </div>
            
            {/* MENÚ DE PESTAÑAS */}
            <div style={{ display: 'flex', borderBottom: '2px solid #ddd', marginBottom: '20px' }}>
                <button 
                    onClick={() => setActiveTab('restaurants')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'restaurants' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'restaurants' ? 'bold' : 'normal', color: activeTab === 'restaurants' ? '#007bff' : '#555' }}
                >
                    🍔 Restaurantes
                </button>
                <button 
                    onClick={() => setActiveTab('riders')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'riders' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'riders' ? 'bold' : 'normal', color: activeTab === 'riders' ? '#007bff' : '#555' }}
                >
                    🛵 Repartidores
                </button>
            </div>

            {/* SECCIÓN RESTAURANTES */}
            {activeTab === 'restaurants' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Restaurantes</h2>
                        <button onClick={() => { setEditingRestId(null); setRestFormData({ name: '', description: '', email: '', phone: '', password: '' }); setShowRestForm(!showRestForm); }} style={{ padding: '10px', background: showRestForm && !editingRestId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showRestForm && !editingRestId ? 'Cancelar' : '+ Añadir Restaurante'}
                        </button>
                    </div>

                    {showRestForm && (
                        <form onSubmit={handleRestSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre" required value={restFormData.name} onChange={(e) => setRestFormData({...restFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="email" placeholder="Email" required value={restFormData.email} onChange={(e) => setRestFormData({...restFormData, email: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Teléfono" required value={restFormData.phone} onChange={(e) => setRestFormData({...restFormData, phone: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="password" placeholder={editingRestId ? "Nueva Contraseña (Opcional)" : "Contraseña"} required={!editingRestId} value={restFormData.password} onChange={(e) => setRestFormData({...restFormData, password: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <input type="text" placeholder="Descripción breve" required value={restFormData.description} onChange={(e) => setRestFormData({...restFormData, description: e.target.value})} style={{ padding: '8px', width: '100%', marginBottom: '10px', boxSizing: 'border-box' }} />
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Restaurante</button>
                                {editingRestId && <button type="button" onClick={() => setShowRestForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Email</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {restaurants.map((rest, index) => (
                                <tr key={rest.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.name}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rest.email}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingRestId(rest.id); setRestFormData({ name: rest.name, description: rest.description, email: rest.email, phone: rest.phone, password: '' }); setShowRestForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteRestaurant(rest.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* SECCIÓN REPARTIDORES */}
            {activeTab === 'riders' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Repartidores</h2>
                        <button onClick={() => { setEditingRiderId(null); setRiderFormData({ name: '', email: '', phoneNumber: '', password: '' }); setShowRiderForm(!showRiderForm); }} style={{ padding: '10px', background: showRiderForm && !editingRiderId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showRiderForm && !editingRiderId ? 'Cancelar' : '+ Añadir Repartidor'}
                        </button>
                    </div>

                    {showRiderForm && (
                        <form onSubmit={handleRiderSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre Completo" required value={riderFormData.name} onChange={(e) => setRiderFormData({...riderFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="email" placeholder="Email" required value={riderFormData.email} onChange={(e) => setRiderFormData({...riderFormData, email: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Teléfono" required value={riderFormData.phoneNumber} onChange={(e) => setRiderFormData({...riderFormData, phoneNumber: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="password" placeholder={editingRiderId ? "Nueva Contraseña (Opcional)" : "Contraseña"} required={!editingRiderId} value={riderFormData.password} onChange={(e) => setRiderFormData({...riderFormData, password: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Repartidor</button>
                                {editingRiderId && <button type="button" onClick={() => setShowRiderForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Teléfono</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {riders.map((rider, index) => (
                                <tr key={rider.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rider.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rider.name}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{rider.phoneNumber}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingRiderId(rider.id); setRiderFormData({ name: rider.name, email: rider.email, phoneNumber: rider.phoneNumber, password: '' }); setShowRiderForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteRider(rider.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Despedir</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;