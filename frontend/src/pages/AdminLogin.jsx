import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginAdmin } from '../api/authApi';
import '../styles/Register.css';

const AdminLogin = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        
        try {
            
            const token = await loginAdmin({ email, password });
            
            localStorage.setItem('token', token);
            navigate('/admin-dashboard');
        } catch (err) {
            
            setError(err.message || "Network Error");
        }
    };

    return (
        <div className="register-container">
            <div className="register-card">
                <h1>JustOrder Admin</h1>
                <p>Administrator Login</p>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    
                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}
                    
                    <button type="submit">Entrar</button>
                </form>
            </div>
        </div>
    );
};

export default AdminLogin;