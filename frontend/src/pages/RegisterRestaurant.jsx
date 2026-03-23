import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerRestaurant } from "../api/authApi";

const CUISINES = [
    "Italian",
    "Chinese",
    "Mexican",
    "Indian",
    "Japanese",
    "Mediterranean",
];

const RegisterRestaurant = () => {
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        phone: "",
        email: "",
        password: "",
        mondayWorkingHours: "",
        tuesdayWorkingHours: "",
        wednesdayWorkingHours: "",
        thursdayWorkingHours: "",
        fridayWorkingHours: "",
        saturdayWorkingHours: "",
        sundayWorkingHours: "",
        city: "",
        province: "",
        country: "",
        postalCode: "",
        number: "",
        longitude: "",
        latitude: "",
        cuisineCategoryNames: [],
    });
    
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const onFieldChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const onCuisineChange = (e) => {
        const { value, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            cuisineCategoryNames: checked
            ? [...prev.cuisineCategoryNames, value]
            : prev.cuisineCategoryNames.filter((c) => c !== value),
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const payload = {
            name: formData.name,
            description: formData.description,
            phone: formData.phone,
            email: formData.email,
            password: formData.password,
            mondayWorkingHours: formData.mondayWorkingHours,
            tuesdayWorkingHours: formData.tuesdayWorkingHours,
            wednesdayWorkingHours: formData.wednesdayWorkingHours,
            thursdayWorkingHours: formData.thursdayWorkingHours,
            fridayWorkingHours: formData.fridayWorkingHours,
            saturdayWorkingHours: formData.saturdayWorkingHours,
            sundayWorkingHours: formData.sundayWorkingHours,
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
            cuisineCategoryNames: formData.cuisineCategoryNames,
        };

        try {
            await registerRestaurant(payload);
            setMessage("Restaurant registered successfully");
            setTimeout(() => {
                navigate("/");
            }, 1500);
        } catch (error) {
            setMessage(error.message || "Error registering restaurant");
        }
    };

    return (
        <div className="register-container">
            <h2>Register Restaurant</h2>
            <form onSubmit={handleSubmit}>
            <input name="name" type="text" placeholder="Restaurant Name" value={formData.name} onChange={onFieldChange} required />
            <input name="description" type="text" placeholder="Description" value={formData.description} onChange={onFieldChange} required />
            <input name="phone" type="text" placeholder="Phone" value={formData.phone} onChange={onFieldChange} required />
            <input name="email" type="email" placeholder="Email" value={formData.email} onChange={onFieldChange} required />

            <input
                name="password"
                type="password"
                placeholder="Password (minimum 18 characters)"
                value={formData.password}
                onChange={onFieldChange}
                minLength={18}
                required
            />

            <input name="mondayWorkingHours" type="text" placeholder="Monday (HH:mm-HH:mm)" value={formData.mondayWorkingHours} onChange={onFieldChange} required />
            <input name="tuesdayWorkingHours" type="text" placeholder="Tuesday (HH:mm-HH:mm)" value={formData.tuesdayWorkingHours} onChange={onFieldChange} required />
            <input name="wednesdayWorkingHours" type="text" placeholder="Wednesday (HH:mm-HH:mm)" value={formData.wednesdayWorkingHours} onChange={onFieldChange} required />
            <input name="thursdayWorkingHours" type="text" placeholder="Thursday (HH:mm-HH:mm)" value={formData.thursdayWorkingHours} onChange={onFieldChange} required />
            <input name="fridayWorkingHours" type="text" placeholder="Friday (HH:mm-HH:mm)" value={formData.fridayWorkingHours} onChange={onFieldChange} required />
            <input name="saturdayWorkingHours" type="text" placeholder="Saturday (HH:mm-HH:mm)" value={formData.saturdayWorkingHours} onChange={onFieldChange} required />
            <input name="sundayWorkingHours" type="text" placeholder="Sunday (HH:mm-HH:mm)" value={formData.sundayWorkingHours} onChange={onFieldChange} required />

            <input name="city" type="text" placeholder="City" value={formData.city} onChange={onFieldChange} required />
            <input name="province" type="text" placeholder="Province" value={formData.province} onChange={onFieldChange} required />
            <input name="country" type="text" placeholder="Country" value={formData.country} onChange={onFieldChange} required />
            <input name="postalCode" type="text" placeholder="Postal Code" value={formData.postalCode} onChange={onFieldChange} required />
            <input name="number" type="text" placeholder="Street Number" value={formData.number} onChange={onFieldChange} required />
            <input name="longitude" type="number" step="any" placeholder="Longitude" value={formData.longitude} onChange={onFieldChange} required />
            <input name="latitude" type="number" step="any" placeholder="Latitude" value={formData.latitude} onChange={onFieldChange} required />

            <fieldset>
                <legend>Categorías de cocina</legend>
                {CUISINES.map((cuisine) => (
                <label key={cuisine} style={{ display: "block" }}>
                    <input
                    type="checkbox"
                    value={cuisine}
                    checked={formData.cuisineCategoryNames.includes(cuisine)}
                    onChange={onCuisineChange}
                    />
                    {cuisine}
                </label>
                ))}
            </fieldset>

            <button type="submit">Register restaurant</button>
            </form>

            {message && <p role="alert">{message}</p>}
        </div>
    );
};

export default RegisterRestaurant;