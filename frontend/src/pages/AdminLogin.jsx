import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginAdmin } from '../api/authService';

const AdminLogin = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const token = await loginAdmin(email, password);
            localStorage.setItem('adminToken', token);
            
            navigate('/admin/dashboard'); 

        } catch (err) {
            setError("Email o contraseña incorrectos");
        }
    };

    const containerStyle = { display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '80vh', fontFamily: 'Arial, sans-serif' };
    const formStyle = { padding: '30px', border: '1px solid #ddd', borderRadius: '8px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)', width: '300px' };

    return (
        <div style={containerStyle}>
            <form onSubmit={handleSubmit} style={formStyle}>
                <h2 style={{ textAlign: 'center' }}>JustOrder Admin</h2>
                {error && <p style={{ color: 'red', fontSize: '14px' }}>{error}</p>}
                <div style={{ marginBottom: '15px' }}>
                    <label>Email</label>
                    <input type="email" style={{ width: '100%', padding: '8px', marginTop: '5px' }} value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label>Contraseña</label>
                    <input type="password" style={{ width: '100%', padding: '8px', marginTop: '5px' }} value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <button type="submit" style={{ width: '100%', padding: '10px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Entrar
                </button>
            </form>
        </div>
    );
};

export default AdminLogin;