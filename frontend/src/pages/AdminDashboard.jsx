import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {

    const [activeTab, setActiveTab] = useState('restaurants');

    const [restaurants, setRestaurants] = useState([]);
    const [showRestForm, setShowRestForm] = useState(false);
    const [editingRestId, setEditingRestId] = useState(null);
    const [restFormData, setRestFormData] = useState({ name: '', description: '', email: '', phone: '', password: '' });

    const [riders, setRiders] = useState([]);
    const [showRiderForm, setShowRiderForm] = useState(false);
    const [editingRiderId, setEditingRiderId] = useState(null);
    const [riderFormData, setRiderFormData] = useState({ name: '', email: '', phoneNumber: '', password: '' });

    const [customers, setCustomers] = useState([]);
    const [showCustForm, setShowCustForm] = useState(false);
    const [editingCustId, setEditingCustId] = useState(null);
    const [custFormData, setCustFormData] = useState({ name: '', email: '', phone: '', password: '', age: '', dni: '' });
 
    const [alergens, setAlergens] = useState([]);
    const [showAlergenForm, setShowAlergenForm] = useState(false);
    const [editingAlergenId, setEditingAlergenId] = useState(null);
    const [alergenFormData, setAlergenFormData] = useState({ name: '', description: '' });

    // ESTADOS PARA CATEGORÍAS 
    const [categories, setCategories] = useState([]);
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const [editingCategoryId, setEditingCategoryId] = useState(null);
    const [categoryFormData, setCategoryFormData] = useState({ name: '', description: '' });

    const [orders, setOrders] = useState([]);
    const [showOrderForm, setShowOrderForm] = useState(false);
    const [editingOrderId, setEditingOrderId] = useState(null);
    const [orderFormData, setOrderFormData] = useState({ customerId: '', riderId: '', statusId: '', totalPrice: '', secretCode: '', dishIds: [] });

    const [statuses, setStatuses] = useState([]);
    const [showStatusForm, setShowStatusForm] = useState(false);
    const [editingStatusId, setEditingStatusId] = useState(null);
    const [statusFormData, setStatusFormData] = useState({ name: '' });

    const [dishes, setDishes] = useState([]);
    const [showDishForm, setShowDishForm] = useState(false);
    const [editingDishId, setEditingDishId] = useState(null);
    const [dishFormData, setDishFormData] = useState({ name: '', description: '', price: '', restaurantId: '', alergenIds: [] });
    
    const navigate = useNavigate();
    const token = localStorage.getItem('adminToken');

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

    const fetchCustomers = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/customers/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setCustomers(await response.json());
        } catch (error) { console.error("Error cargando clientes"); }
    };

    const fetchAlergens = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/alergens/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setAlergens(await response.json());
        } catch (error) { console.error("Error cargando alérgenos"); }
    };

    const fetchCategories = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/categories/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setCategories(await response.json());
        } catch (error) { console.error("Error cargando categorías"); }
    };

    const fetchOrders = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/orders/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setOrders(await response.json());
        } catch (error) { console.error("Error cargando pedidos"); }
    };

    const fetchStatuses = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/order-statuses/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setStatuses(await response.json());
        } catch (error) { console.error("Error cargando estados de pedido"); }
    };

    const fetchDishes = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/dishes/all', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) setDishes(await response.json());
        } catch (error) { console.error("Error cargando platos"); }
    };

    useEffect(() => {
        if (!token) { navigate('/admin/login'); return; }
        
        fetchRestaurants();
        fetchRiders();
        fetchCustomers();
        fetchAlergens();
        fetchCategories();
        fetchStatuses();
        fetchDishes();
        fetchOrders();
        
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token, activeTab]);

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

    const handleCustSubmit = async (e) => {
        e.preventDefault();
        const url = editingCustId ? `http://localhost:8080/api/customers/update/${editingCustId}` : 'http://localhost:8080/api/customers/create';
        const method = editingCustId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(custFormData) });
            if (res.ok) {
                setShowCustForm(false); setEditingCustId(null);
                setCustFormData({ name: '', email: '', phone: '', password: '', age: '', dni: '' });
                fetchCustomers();
            } else alert("Error guardando cliente");
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteCustomer = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar este cliente?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/customers/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchCustomers();
        } catch (error) { alert("Fallo al eliminar"); }
    };

    const handleAlergenSubmit = async (e) => {
        e.preventDefault();
        const url = editingAlergenId ? `http://localhost:8080/api/alergens/update/${editingAlergenId}` : 'http://localhost:8080/api/alergens/create';
        const method = editingAlergenId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(alergenFormData) });
            if (res.ok) { setShowAlergenForm(false); setEditingAlergenId(null); setAlergenFormData({ name: '', description: '' }); fetchAlergens(); }
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteAlergen = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar este alérgeno?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/alergens/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchAlergens();
        } catch (error) { alert("Fallo al eliminar"); }
    };

    const handleCategorySubmit = async (e) => {
        e.preventDefault();
        const url = editingCategoryId ? `http://localhost:8080/api/categories/update/${editingCategoryId}` : 'http://localhost:8080/api/categories/create';
        const method = editingCategoryId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(categoryFormData) });
            if (res.ok) { setShowCategoryForm(false); setEditingCategoryId(null); setCategoryFormData({ name: '', description: '' }); fetchCategories(); }
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteCategory = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar esta categoría?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/categories/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchCategories();
        } catch (error) { alert("Fallo al eliminar"); }
    };

    const handleOrderSubmit = async (e) => {
        e.preventDefault();
        const url = editingOrderId ? `http://localhost:8080/api/orders/update/${editingOrderId}` : 'http://localhost:8080/api/orders/create';
        const method = editingOrderId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(orderFormData) });
            if (res.ok) { 
                setShowOrderForm(false); setEditingOrderId(null); 
                setOrderFormData({ customerId: '', riderId: '', statusId: '', totalPrice: '', secretCode: '', dishIds: [] }); 
                fetchOrders(); 
            } else alert("Error guardando pedido");
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteOrder = async (id) => {
        if (!window.confirm("¿Seguro que quieres cancelar y eliminar este pedido del sistema?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/orders/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchOrders();
        } catch (error) { alert("Fallo al eliminar pedido"); }
    };

    const handleStatusSubmit = async (e) => {
        e.preventDefault();
        const url = editingStatusId ? `http://localhost:8080/api/order-statuses/update/${editingStatusId}` : 'http://localhost:8080/api/order-statuses/create';
        const method = editingStatusId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(statusFormData) });
            if (res.ok) { setShowStatusForm(false); setEditingStatusId(null); setStatusFormData({ name: '' }); fetchStatuses(); }
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteStatus = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar este estado de pedido?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/order-statuses/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchStatuses();
        } catch (error) { alert("Fallo al eliminar estado"); }
    };

    const handleDishSubmit = async (e) => {
        e.preventDefault();
        const url = editingDishId ? `http://localhost:8080/api/dishes/update/${editingDishId}` : 'http://localhost:8080/api/dishes/create';
        const method = editingDishId ? 'PUT' : 'POST';
        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(dishFormData) });
            if (res.ok) { 
                setShowDishForm(false); setEditingDishId(null); 
                setDishFormData({ name: '', description: '', price: '', image: '', restaurantId: '', alergenIds: [], categoryIds: [] }); 
                fetchDishes(); 
            } else alert("Error guardando plato");
        } catch (error) { alert("Fallo de conexión"); }
    };

    const deleteDish = async (id) => {
        if (!window.confirm("¿Seguro que quieres eliminar este plato?")) return;
        try {
            const res = await fetch(`http://localhost:8080/api/dishes/delete/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) fetchDishes();
        } catch (error) { alert("Fallo al eliminar plato"); }
    };

    const handleLogout = () => { localStorage.removeItem('adminToken'); navigate('/admin/login'); };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1>Panel de Control del Administrador 🚀</h1>
                <button onClick={handleLogout} style={{ padding: '10px 20px', background: 'red', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cerrar Sesión</button>
            </div>
            
            {/* MENU */}
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
                <button 
                    onClick={() => setActiveTab('customers')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'customers' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'customers' ? 'bold' : 'normal', color: activeTab === 'customers' ? '#007bff' : '#555' }}
                >
                    👥 Clientes
                </button>
                <button 
                    onClick={() => setActiveTab('alergens')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'alergens' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'alergens' ? 'bold' : 'normal', color: activeTab === 'alergens' ? '#007bff' : '#555' }}
                >
                    ⚠️ Alérgenos
                </button>
                <button 
                    onClick={() => setActiveTab('categories')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'categories' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'categories' ? 'bold' : 'normal', color: activeTab === 'categories' ? '#007bff' : '#555' }}
                >
                    🍽️ Categorías
                </button>
                <button 
                    onClick={() => setActiveTab('dishes')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'dishes' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'dishes' ? 'bold' : 'normal', color: activeTab === 'dishes' ? '#007bff' : '#555' }}
                >
                    🍲 Platos
                </button>
                <button 
                    onClick={() => setActiveTab('orders')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'orders' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'orders' ? 'bold' : 'normal', color: activeTab === 'orders' ? '#007bff' : '#555' }}
                >
                    📦 Pedidos
                </button>
                <button 
                    onClick={() => setActiveTab('statuses')}
                    style={{ padding: '10px 20px', fontSize: '16px', background: 'none', border: 'none', cursor: 'pointer', borderBottom: activeTab === 'statuses' ? '3px solid #007bff' : 'none', fontWeight: activeTab === 'statuses' ? 'bold' : 'normal', color: activeTab === 'statuses' ? '#007bff' : '#555' }}
                >
                    🏷️ Estados
                </button>
            </div>

            {/* RESTAURANTS SECTION */}
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

            {/* RIDERS SECTION */}
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

            {/* CLIENTS SECTION */}
            {activeTab === 'customers' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Clientes</h2>
                        <button onClick={() => { setEditingCustId(null); setCustFormData({ name: '', email: '', phone: '', password: '', age: '', dni: '' }); setShowCustForm(!showCustForm); }} style={{ padding: '10px', background: showCustForm && !editingCustId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showCustForm && !editingCustId ? 'Cancelar' : '+ Añadir Cliente'}
                        </button>
                    </div>

                    {showCustForm && (
                        <form onSubmit={handleCustSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre Completo" required value={custFormData.name} onChange={(e) => setCustFormData({...custFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="email" placeholder="Email" required value={custFormData.email} onChange={(e) => setCustFormData({...custFormData, email: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Teléfono" required value={custFormData.phone} onChange={(e) => setCustFormData({...custFormData, phone: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="password" placeholder={editingCustId ? "Nueva Contraseña (Opcional)" : "Contraseña"} required={!editingCustId} value={custFormData.password} onChange={(e) => setCustFormData({...custFormData, password: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="number" placeholder="Edad" required value={custFormData.age} onChange={(e) => setCustFormData({...custFormData, age: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="text" placeholder="DNI" required value={custFormData.dni} onChange={(e) => setCustFormData({...custFormData, dni: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Cliente</button>
                                {editingCustId && <button type="button" onClick={() => setShowCustForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Email</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>DNI</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {customers.map((cust, index) => (
                                <tr key={cust.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cust.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cust.name}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cust.email}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cust.dni}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingCustId(cust.id); setCustFormData({ name: cust.name, email: cust.email, phone: cust.phone, password: '', age: cust.age, dni: cust.dni }); setShowCustForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteCustomer(cust.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* ALERGENS SECTION */}
            {activeTab === 'alergens' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Alérgenos</h2>
                        <button onClick={() => { setEditingAlergenId(null); setAlergenFormData({ name: '', description: '' }); setShowAlergenForm(!showAlergenForm); }} style={{ padding: '10px', background: showAlergenForm && !editingAlergenId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showAlergenForm && !editingAlergenId ? 'Cancelar' : '+ Añadir Alérgeno'}
                        </button>
                    </div>

                    {showAlergenForm && (
                        <form onSubmit={handleAlergenSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre (Ej: Gluten)" required value={alergenFormData.name} onChange={(e) => setAlergenFormData({...alergenFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <input type="text" placeholder="Descripción breve" value={alergenFormData.description} onChange={(e) => setAlergenFormData({...alergenFormData, description: e.target.value})} style={{ padding: '8px', width: '100%', marginBottom: '10px', boxSizing: 'border-box' }} />
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Alérgeno</button>
                                {editingAlergenId && <button type="button" onClick={() => setShowAlergenForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Descripción</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {alergens.map((alergen, index) => (
                                <tr key={alergen.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{alergen.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{alergen.name}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{alergen.description}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingAlergenId(alergen.id); setAlergenFormData({ name: alergen.name, description: alergen.description }); setShowAlergenForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteAlergen(alergen.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* CATEGORY SECTION */}
            {activeTab === 'categories' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Categorías de Cocina</h2>
                        <button onClick={() => { setEditingCategoryId(null); setCategoryFormData({ name: '', description: '' }); setShowCategoryForm(!showCategoryForm); }} style={{ padding: '10px', background: showCategoryForm && !editingCategoryId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showCategoryForm && !editingCategoryId ? 'Cancelar' : '+ Añadir Categoría'}
                        </button>
                    </div>

                    {showCategoryForm && (
                        <form onSubmit={handleCategorySubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre (Ej: Italiana)" required value={categoryFormData.name} onChange={(e) => setCategoryFormData({...categoryFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <input type="text" placeholder="Descripción breve" value={categoryFormData.description} onChange={(e) => setCategoryFormData({...categoryFormData, description: e.target.value})} style={{ padding: '8px', width: '100%', marginBottom: '10px', boxSizing: 'border-box' }} />
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Categoría</button>
                                {editingCategoryId && <button type="button" onClick={() => setShowCategoryForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Descripción</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories.map((cat, index) => (
                                <tr key={cat.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cat.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cat.name}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{cat.description}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingCategoryId(cat.id); setCategoryFormData({ name: cat.name, description: cat.description }); setShowCategoryForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteCategory(cat.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* ORDERS SECTION */}
            {activeTab === 'orders' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Monitor Global de Pedidos</h2>
                        <button onClick={() => { setEditingOrderId(null); setOrderFormData({ customerId: '', riderId: '', statusId: '', totalPrice: '', secretCode: '', dishIds: [] }); setShowOrderForm(!showOrderForm); }} style={{ padding: '10px', background: showOrderForm && !editingOrderId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showOrderForm && !editingOrderId ? 'Cancelar' : '+ Añadir Pedido'}
                        </button>
                    </div>

                    {showOrderForm && (
                        <form onSubmit={handleOrderSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <select required value={orderFormData.customerId} onChange={(e) => setOrderFormData({...orderFormData, customerId: e.target.value})} style={{ padding: '8px', flex: 1 }}>
                                    <option value="">Seleccionar Cliente...</option>
                                    {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                                </select>
                                <select required value={orderFormData.riderId} onChange={(e) => setOrderFormData({...orderFormData, riderId: e.target.value})} style={{ padding: '8px', flex: 1 }}>
                                    <option value="">Seleccionar Repartidor...</option>
                                    {riders.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                                </select>
                                <select required value={orderFormData.statusId} onChange={(e) => setOrderFormData({...orderFormData, statusId: e.target.value})} style={{ padding: '8px', flex: 1 }}>
                                    <option value="">Seleccionar Estado...</option>
                                    {statuses.map(s => <option key={s.id} value={s.id}>{s.status}</option>)}
                                </select>
                            </div>
                            
                            <div style={{ marginBottom: '15px' }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>Platos del pedido (Ctrl+Click para varios):</label>
                                <select multiple value={orderFormData.dishIds} onChange={(e) => setOrderFormData({...orderFormData, dishIds: Array.from(e.target.selectedOptions, option => parseInt(option.value))})} style={{ width: '100%', padding: '8px', height: '100px' }}>
                                    {dishes.map(d => <option key={d.id} value={d.id}>{d.name} - ({d.restaurant ? d.restaurant.name : 'Sin Restaurante'})</option>)}
                                </select>
                            </div>

                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="number" step="0.01" placeholder="Precio Total (€)" required value={orderFormData.totalPrice} onChange={(e) => setOrderFormData({...orderFormData, totalPrice: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="text" placeholder="Código Secreto" required value={orderFormData.secretCode} onChange={(e) => setOrderFormData({...orderFormData, secretCode: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Pedido</button>
                                {editingOrderId && <button type="button" onClick={() => setShowOrderForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID Pedido</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Cliente</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Repartidor</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Estado</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Platos</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Total (€)</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.length === 0 ? (
                                <tr><td colSpan="7" style={{ padding: '20px', textAlign: 'center', border: '1px solid #ddd' }}>No hay pedidos en el sistema.</td></tr>
                            ) : (
                                orders.map((order, index) => (
                                    <tr key={order.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>#{order.id}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{order.customer ? order.customer.name : 'N/A'}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{order.rider ? order.rider.name : 'Sin asignar'}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{order.status ? order.status.status : 'Desconocido'}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{order.dishes ? order.dishes.length + ' platos' : 'Ninguno'}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd', fontWeight: 'bold' }}>{order.totalPrice} €</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                            <button onClick={() => { setEditingOrderId(order.id); setOrderFormData({ customerId: order.customer?.id || '', riderId: order.rider?.id || '', statusId: order.status?.id || '', totalPrice: order.totalPrice, secretCode: order.secretCode, dishIds: order.dishes ? order.dishes.map(d => d.id) : [] }); setShowOrderForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                            <button onClick={() => deleteOrder(order.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {/* STATUSES SECTION */}
            {activeTab === 'statuses' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Estados de Pedido</h2>
                        <button onClick={() => { setEditingStatusId(null); setStatusFormData({ name: '' }); setShowStatusForm(!showStatusForm); }} style={{ padding: '10px', background: showStatusForm && !editingStatusId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showStatusForm && !editingStatusId ? 'Cancelar' : '+ Añadir Estado'}
                        </button>
                    </div>

                    {showStatusForm && (
                        <form onSubmit={handleStatusSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre (Ej: En cocina, Entregado)" required value={statusFormData.name} onChange={(e) => setStatusFormData({...statusFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                            </div>
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Estado</button>
                                {editingStatusId && <button type="button" onClick={() => setShowStatusForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre del Estado</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {statuses.map((status, index) => (
                                <tr key={status.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{status.id}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd' }}>{status.status}</td>
                                    <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                        <button onClick={() => { setEditingStatusId(status.id); setStatusFormData({ name: status.status }); setShowStatusForm(true); }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                        <button onClick={() => deleteStatus(status.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* DISHES SECTION */}
            {activeTab === 'dishes' && (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h2>Gestión de Platos</h2>
                        <button onClick={() => { setEditingDishId(null); setDishFormData({ name: '', description: '', price: '', restaurantId: '', alergenIds: [] }); setShowDishForm(!showDishForm); }} style={{ padding: '10px', background: showDishForm && !editingDishId ? '#6c757d' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {showDishForm && !editingDishId ? 'Cancelar' : '+ Añadir Plato'}
                        </button>
                    </div>

                    {showDishForm && (
                        <form onSubmit={handleDishSubmit} style={{ background: '#f8f9fa', padding: '20px', marginTop: '15px', borderRadius: '8px', border: '1px solid #ddd' }}>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Nombre del Plato" required value={dishFormData.name} onChange={(e) => setDishFormData({...dishFormData, name: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <input type="number" step="0.01" placeholder="Precio (€)" required value={dishFormData.price} onChange={(e) => setDishFormData({...dishFormData, price: e.target.value})} style={{ padding: '8px', flex: 1 }} />
                                <select required value={dishFormData.restaurantId} onChange={(e) => setDishFormData({...dishFormData, restaurantId: e.target.value})} style={{ padding: '8px', flex: 1 }}>
                                    <option value="">Seleccionar Restaurante...</option>
                                    {restaurants.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                                </select>
                            </div>
                            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                                <input type="text" placeholder="Descripción breve" required value={dishFormData.description} onChange={(e) => setDishFormData({...dishFormData, description: e.target.value})} style={{ padding: '8px', width: '100%', boxSizing: 'border-box' }} />
                            </div>
                            
                            <div style={{ marginBottom: '15px' }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>Alérgenos (Ctrl+Click para varios):</label>
                                <select multiple value={dishFormData.alergenIds} onChange={(e) => setDishFormData({...dishFormData, alergenIds: Array.from(e.target.selectedOptions, option => parseInt(option.value))})} style={{ width: '100%', padding: '8px', height: '80px' }}>
                                    {alergens.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
                                </select>
                            </div>

                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Guardar Plato</button>
                                {editingDishId && <button type="button" onClick={() => setShowDishForm(false)} style={{ padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Cancelar</button>}
                            </div>
                        </form>
                    )}

                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#343a40', color: 'white', textAlign: 'left' }}>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Nombre</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Restaurante</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd' }}>Precio</th>
                                <th style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {dishes.length === 0 ? (
                                <tr><td colSpan="5" style={{ padding: '20px', textAlign: 'center', border: '1px solid #ddd' }}>No hay platos en el sistema.</td></tr>
                            ) : (
                                dishes.map((dish, index) => (
                                    <tr key={dish.id} style={{ backgroundColor: index % 2 === 0 ? '#f9f9f9' : 'white' }}>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{dish.id}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{dish.name}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd' }}>{dish.restaurant ? dish.restaurant.name : 'N/A'}</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd', fontWeight: 'bold' }}>{dish.price} €</td>
                                        <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                                            <button onClick={() => { 
                                                setEditingDishId(dish.id); 
                                                setDishFormData({ 
                                                    name: dish.name, 
                                                    description: dish.description, 
                                                    price: dish.price, 
                                                    restaurantId: dish.restaurant?.id || '', 
                                                    alergenIds: dish.alergens ? dish.alergens.map(a => a.id) : []
                                                }); 
                                                setShowDishForm(true); 
                                            }} style={{ padding: '6px 12px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '5px' }}>Editar</button>
                                            <button onClick={() => deleteDish(dish.id)} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Eliminar</button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;