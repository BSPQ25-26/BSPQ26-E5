import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
    getRestaurantDashboard,
    getRestaurantProfile,
    updateRestaurantProfile,
} from "../api/authApi";
import { readLoggedInRestaurant } from "../utils/auth";
import "../assets/css/Home.css";
import "../assets/css/RestaurantInformationPage.css";

const formatPrice = (value) => `${Number(value || 0).toFixed(2)} EUR`;

const WORKING_HOURS_PATTERN = "^\\d{2}:\\d{2}-\\d{2}:\\d{2}$";
const CUISINES = ["Italian", "Chinese", "Mexican", "Indian", "Japanese", "Mediterranean"];

const isBlank = (value) => String(value ?? "").trim() === "";

const normalizeCoordinateValue = (value) => {
    if (value === null || value === undefined || value === "") return "";
    return String(value).replace(",", ".");
};

const formatWorkingHoursValue = (value) => {
    const rawValue = String(value ?? "");
    if (!rawValue) return "";

    const normalizeTimePart = (part) => {
        const digits = part.replace(/\D/g, "");
        if (digits.length === 0) return "00:00";
        if (digits.length === 1) return `0${digits}:00`;
        if (digits.length === 2) return `${digits}:00`;
        if (digits.length === 3) return `0${digits[0]}:${digits.slice(1)}`;
        return `${digits.slice(0, 2)}:${digits.slice(2, 4).padEnd(2, "0")}`;
    };

    const [firstPart = "", secondPart = ""] = rawValue.split("-");
    return `${normalizeTimePart(firstPart)}-${secondPart ? normalizeTimePart(secondPart) : "00:00"}`;
};

const buildInitialForm = (profile) => {
    const firstLocation = profile?.localizations?.[0] || {};
    return {
        name: profile?.name || "", description: profile?.description || "", phone: profile?.phone || "",
        mondayWorkingHours: profile?.mondayWorkingHours || "", tuesdayWorkingHours: profile?.tuesdayWorkingHours || "",
        wednesdayWorkingHours: profile?.wednesdayWorkingHours || "", thursdayWorkingHours: profile?.thursdayWorkingHours || "",
        fridayWorkingHours: profile?.fridayWorkingHours || "", saturdayWorkingHours: profile?.saturdayWorkingHours || "",
        sundayWorkingHours: profile?.sundayWorkingHours || "", cuisineCategoryNames: (profile?.cuisineCategoryNames || []).join(", "),
        city: firstLocation.city || "", province: firstLocation.province || "", country: firstLocation.country || "",
        postalCode: firstLocation.postalCode || "", number: firstLocation.number || "",
        longitude: normalizeCoordinateValue(firstLocation.longitude), latitude: normalizeCoordinateValue(firstLocation.latitude),
    };
};

function RestaurantInformationPage() {
    const navigate = useNavigate();
    const [restaurant, setRestaurant] = useState(null);
    const [dashboard, setDashboard] = useState(null);
    const [formData, setFormData] = useState(buildInitialForm(null));
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const token = useMemo(() => localStorage.getItem("token") || "", []);

    useEffect(() => {
        const loggedInRestaurant = readLoggedInRestaurant();
        if (!loggedInRestaurant || !token) {
            setErrorMessage("You must be logged in as a restaurant to access this page.");
            setIsLoading(false);
            return;
        }

        Promise.all([getRestaurantProfile(token), getRestaurantDashboard(token)])
            .then(([profileData, dashboardData]) => {
                setRestaurant(profileData);
                setDashboard(dashboardData);
                setFormData(buildInitialForm(profileData));
            })
            .catch(() => setErrorMessage("Could not load restaurant data."))
            .finally(() => setIsLoading(false));
    }, [token]);

    const handleSignOut = () => {
        localStorage.clear();
        navigate("/");
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        let nextValue = value;
        if (name === "longitude" || name === "latitude") nextValue = value.replace(",", ".");
        if (name === "number") nextValue = String(value || "").replace(/\D/g, "");
        setFormData((current) => ({ ...current, [name]: nextValue }));
    };

    const handleHourBlur = (event) => {
        const { name, value } = event.target;
        setFormData((current) => ({ ...current, [name]: formatWorkingHoursValue(value) }));
    };

    const buildUpdatePayload = () => {
        const cuisineCategoryNames = formData.cuisineCategoryNames.split(",").map(i => i.trim()).filter(Boolean);
        const updatePayload = {
            name: formData.name, description: formData.description, phone: formData.phone,
            mondayWorkingHours: formatWorkingHoursValue(formData.mondayWorkingHours),
            tuesdayWorkingHours: formatWorkingHoursValue(formData.tuesdayWorkingHours),
            wednesdayWorkingHours: formatWorkingHoursValue(formData.wednesdayWorkingHours),
            thursdayWorkingHours: formatWorkingHoursValue(formData.thursdayWorkingHours),
            fridayWorkingHours: formatWorkingHoursValue(formData.fridayWorkingHours),
            saturdayWorkingHours: formatWorkingHoursValue(formData.saturdayWorkingHours),
            sundayWorkingHours: formatWorkingHoursValue(formData.sundayWorkingHours),
        };
        if (cuisineCategoryNames.length > 0) updatePayload.cuisineCategoryNames = cuisineCategoryNames;

        const hasAnyLocationValue = [formData.city, formData.province, formData.country, formData.postalCode, formData.number, formData.longitude, formData.latitude].some(v => !isBlank(v));
        if (hasAnyLocationValue) {
            const rawLng = String(formData.longitude || "").replace(",", ".").trim();
            const rawLat = String(formData.latitude || "").replace(",", ".").trim();
            const longitude = rawLng === "" ? null : Number(rawLng);
            const latitude = rawLat === "" ? null : Number(rawLat);
            
            const localization = {
                city: isBlank(formData.city) ? undefined : formData.city.trim(),
                province: isBlank(formData.province) ? undefined : formData.province.trim(),
                country: isBlank(formData.country) ? undefined : formData.country.trim(),
                postalCode: isBlank(formData.postalCode) ? undefined : formData.postalCode.trim(),
                number: String(formData.number || "").replace(/\D/g, "").trim() || undefined,
            };
            if (longitude !== null && latitude !== null && Number.isFinite(longitude) && Number.isFinite(latitude)) {
                localization.longitude = longitude; localization.latitude = latitude;
            }
            if (Object.values(localization).some(v => v !== undefined && v !== "")) {
                updatePayload.localizations = [localization];
            }
        }
        return updatePayload;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage(""); setSuccessMessage(""); setIsSaving(true);
        try {
            const updatedProfile = await updateRestaurantProfile(buildUpdatePayload(), token);
            setRestaurant(updatedProfile);
            setFormData(buildInitialForm(updatedProfile));
            setSuccessMessage("Profile updated successfully.");
        } catch (error) {
            setErrorMessage(error?.message || "Could not save profile changes.");
        } finally {
            setIsSaving(false);
        }
    };

    const hourInputProps = { inputMode: "numeric", pattern: WORKING_HOURS_PATTERN, title: "Use HH:mm-HH:mm", maxLength: 11 };

    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <h1 className="brand-title">JustOrder</h1>
                    </div>
                    <div className="home-header-right">
                        <nav className="home-nav-links" aria-label="Main navigation">
                            <button className="nav-link nav-link-button" onClick={() => navigate(-1)} type="button">
                                Back to Dashboard
                            </button>
                            <button className="nav-link nav-link-button" onClick={handleSignOut} type="button">
                                Sign out
                            </button>
                        </nav>
                    </div>
                </header>

                <div className="restaurant-info-content">
                    <div className="restaurant-info-header">
                        <h2>Restaurant Profile</h2>
                        <p>Manage your restaurant's public information.</p>
                    </div>
                    {isLoading && <p className="restaurant-info-loading">Loading your restaurant data...</p>}
                    {errorMessage && <p className="restaurant-info-error">{errorMessage}</p>}
                    {successMessage && <p className="restaurant-info-success">{successMessage}</p>}
                    
                    {!isLoading && !errorMessage && (
                        <>
                            
                            <div className="restaurant-info-stats-grid">
                                <article className="restaurant-info-stat-card"><h3>Total orders</h3><p>{dashboard?.totalOrders ?? 0}</p></article>
                                <article className="restaurant-info-stat-card"><h3>Active orders</h3><p>{dashboard?.activeOrders ?? 0}</p></article>
                                <article className="restaurant-info-stat-card"><h3>Delivered orders</h3><p>{dashboard?.deliveredOrders ?? 0}</p></article>
                                <article className="restaurant-info-stat-card"><h3>Cancelled orders</h3><p>{dashboard?.cancelledOrders ?? 0}</p></article>
                                <article className="restaurant-info-stat-card"><h3>Total revenue</h3><p>{formatPrice(dashboard?.totalRevenue)}</p></article>
                                <article className="restaurant-info-stat-card"><h3>Total refunded</h3><p>{formatPrice(dashboard?.totalRefunded)}</p></article>
                            </div>

                            <section className="restaurant-info-section" aria-label="Restaurant profile editor">
                                <h3>Edit profile</h3>
                                <form className="restaurant-profile-form" onSubmit={handleSubmit} noValidate>
                                    <div className="restaurant-profile-grid">
                                        <label>Name<input name="name" value={formData.name} onChange={handleInputChange} required /></label>
                                        <label>Phone<input name="phone" value={formData.phone} onChange={handleInputChange} required /></label>
                                        <label className="full-width">Description<textarea name="description" value={formData.description} onChange={handleInputChange} rows={3} required /></label>
                                        <label>Monday hours<input name="mondayWorkingHours" value={formData.mondayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Tuesday hours<input name="tuesdayWorkingHours" value={formData.tuesdayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Wednesday hours<input name="wednesdayWorkingHours" value={formData.wednesdayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Thursday hours<input name="thursdayWorkingHours" value={formData.thursdayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Friday hours<input name="fridayWorkingHours" value={formData.fridayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Saturday hours<input name="saturdayWorkingHours" value={formData.saturdayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <label>Sunday hours<input name="sundayWorkingHours" value={formData.sundayWorkingHours} onChange={handleInputChange} onBlur={handleHourBlur} placeholder="HH:mm-HH:mm" required {...hourInputProps} /></label>
                                        <fieldset className="full-width cuisine-checklist">
                                            <legend>Cuisine categories</legend>
                                            <div className="cuisine-checklist-grid">
                                                {CUISINES.map((cuisine) => (
                                                    <label key={cuisine} className="cuisine-checklist-option">
                                                        <input type="checkbox" value={cuisine} checked={formData.cuisineCategoryNames.split(",").map(i => i.trim()).filter(Boolean).includes(cuisine)}
                                                            onChange={(event) => {
                                                                const { value, checked } = event.target;
                                                                setFormData((current) => {
                                                                    const cur = current.cuisineCategoryNames.split(",").map(i => i.trim()).filter(Boolean);
                                                                    const next = checked ? [...new Set([...cur, value])] : cur.filter(i => i !== value);
                                                                    return { ...current, cuisineCategoryNames: next.join(", ") };
                                                                });
                                                            }}
                                                        />
                                                        {cuisine}
                                                    </label>
                                                ))}
                                            </div>
                                        </fieldset>
                                        <label>City<input name="city" value={formData.city} onChange={handleInputChange} /></label>
                                        <label>Province<input name="province" value={formData.province} onChange={handleInputChange} /></label>
                                        <label>Country<input name="country" value={formData.country} onChange={handleInputChange} /></label>
                                        <label>Postal code<input name="postalCode" value={formData.postalCode} onChange={handleInputChange} /></label>
                                        <label>Street number<input name="number" value={formData.number} onChange={handleInputChange} inputMode="numeric" maxLength={8} /></label>
                                        <label>Longitude<input name="longitude" type="text" inputMode="decimal" value={formData.longitude} onChange={handleInputChange} /></label>
                                        <label>Latitude<input name="latitude" type="text" inputMode="decimal" value={formData.latitude} onChange={handleInputChange} /></label>
                                    </div>
                                    <div className="restaurant-profile-actions">
                                        <button type="submit" disabled={isSaving}>{isSaving ? "Saving..." : "Save changes"}</button>
                                    </div>
                                </form>
                            </section>
                        </>
                    )}
                </div>
            </section>
        </main>
    );
}

export default RestaurantInformationPage;