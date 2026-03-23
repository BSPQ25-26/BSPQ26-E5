import React, {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import { registerCustomer } from '../api/authApi';  

const RegisterCustomer = () => {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        phone: "",
        age: "",
        dni: "",
        city: "",
        province: "",
        country: "",
        postalCode: "",
        number: "",
        longitude: "",
        latitude: "",
    });
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const onFieldChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const payload = {
            name: formData.name,
            email: formData.email,
            password: formData.password,
            phone: formData.phone || null,
            age: formData.age ? Number(formData.age) : null,
            dni: formData.dni || null,
            localizations: [
                {
                city: formData.city,
                province: formData.province,
                country: formData.country,
                postalCode: formData.postalCode,
                number: formData.number,
                longitude: Number(formData.longitude),
                latitude: Number(formData.latitude),
                },
            ],
            preferenceNames: [],
            allergenNames: [],
        };

        try {
            await registerCustomer(payload);
            setMessage("Customer registered successfully");
            setTimeout(() => {
                navigate("/");
            }, 1500);
        } catch (error) {
            setMessage(error.message || "Error registering customer");
        }
    };

    return (
        <div className="register-container">
            <h2>Register Customer</h2>
            <form onSubmit={handleSubmit}>
                <input name="name" type="text" placeholder="Full Name" value={formData.name} onChange={onFieldChange} required />

                <input name="email" type="email" placeholder="Email" value={formData.email} onChange={onFieldChange} required />

                <input name="password" type="password" placeholder="Password (minimum 18 characters)" value={formData.password} onChange={onFieldChange} minLength={18} required />

                <input name="phone" type="text" placeholder="Phone" value={formData.phone} onChange={onFieldChange} />

                <input name="age" type="number" placeholder="Age" value={formData.age} onChange={onFieldChange} min={1} />

                <input name="dni" type="text" placeholder="DNI" value={formData.dni} onChange={onFieldChange} />

                <input name="city" type="text" placeholder="City" value={formData.city} onChange={onFieldChange} required />

                <input name="province" type="text" placeholder="Province" value={formData.province} onChange={onFieldChange} required />

                <input name="country" type="text" placeholder="Country" value={formData.country} onChange={onFieldChange} required />

                <input name="postalCode" type="text" placeholder="Postal Code" value={formData.postalCode} onChange={onFieldChange} required />

                <input name="number" type="text" placeholder="Street Number" value={formData.number} onChange={onFieldChange} required />

                <input name="longitude" type="number" step="any" placeholder="Longitude" value={formData.longitude} onChange={onFieldChange} required />

                <input name="latitude" type="number" step="any" placeholder="Latitude" value={formData.latitude} onChange={onFieldChange} required />

                <button type="submit">Register</button>
            </form>

            {message && <p role="alert">{message}</p>}
        </div>
    );
};

export default RegisterCustomer;