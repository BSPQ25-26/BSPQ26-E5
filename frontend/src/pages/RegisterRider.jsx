import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerRider } from "../api/authApi";

const RegisterRider = () => {
    const [formData, setFormData] = useState({
        name: "",
        dni: "",
        phoneNumber: "",
        email: "",
        password: "",
        city: "",
        province: "",
        country: "",
        postalCode: "",
        number: "",
        longitude: "",
        latitude: "",
    });

    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const navigate = useNavigate();

    const onFieldChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (submitting) return;

        setMessage("");
        setSubmitting(true);

        const payload = {
            name: formData.name.trim(),
            dni: formData.dni.trim(),
            phoneNumber: formData.phoneNumber.trim(),
            email: formData.email.trim(),
            password: formData.password,
            starterPoint: {
                city: formData.city.trim(),
                province: formData.province.trim(),
                country: formData.country.trim(),
                postalCode: formData.postalCode.trim(),
                number: formData.number.trim(),
                longitude: Number(formData.longitude),
                latitude: Number(formData.latitude),
            },
        };

        try {
            await registerRider(payload);
            setMessage("Rider registered successfully");
            setTimeout(() => {
                navigate("/");
            }, 1500);
        } catch (error) {
            setMessage(error.message || "Error registering rider");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="register-container">
            <h2>Register Rider</h2>
            <form onSubmit={handleSubmit}>
                <input
                    name="name"
                    type="text"
                    placeholder="Full Name"
                    value={formData.name}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="dni"
                    type="text"
                    placeholder="DNI"
                    value={formData.dni}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="phoneNumber"
                    type="text"
                    placeholder="Phone Number"
                    value={formData.phoneNumber}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="email"
                    type="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="password"
                    type="password"
                    placeholder="Password (minimum 18 characters)"
                    value={formData.password}
                    onChange={onFieldChange}
                    minLength={18}
                    required
                />

                <input
                    name="city"
                    type="text"
                    placeholder="City"
                    value={formData.city}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="province"
                    type="text"
                    placeholder="Province"
                    value={formData.province}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="country"
                    type="text"
                    placeholder="Country"
                    value={formData.country}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="postalCode"
                    type="text"
                    placeholder="Postal Code"
                    value={formData.postalCode}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="number"
                    type="text"
                    placeholder="Street Number"
                    value={formData.number}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="longitude"
                    type="number"
                    step="any"
                    placeholder="Longitude"
                    value={formData.longitude}
                    onChange={onFieldChange}
                    required
                />

                <input
                    name="latitude"
                    type="number"
                    step="any"
                    placeholder="Latitude"
                    value={formData.latitude}
                    onChange={onFieldChange}
                    required
                />

                <button type="submit" disabled={submitting}>
                    {submitting ? "Registering..." : "Register Rider"}
                </button>
            </form>

            {message && <p role="alert">{message}</p>}
        </div>
    );
};

export default RegisterRider;