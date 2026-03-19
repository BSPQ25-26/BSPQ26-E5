const API_URL = "http://localhost:8080/api";

export const registerCustomer = async (CustomerData) => {
    const response = await fetch(`${API_URL}/customers/create`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(CustomerData),
    });
    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error registrating customer");
    return text ? JSON.parse(text) : null;
};

export const registerRestaurant = async (restaurantData) => {
    const response = await fetch(`${API_URL}/restaurants/create`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(restaurantData),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error en el registro del restaurante");
    return text ? JSON.parse(text) : null;
};

export const registerRider = async (riderData) => {
    const response = await fetch(`${API_URL}/riders/create`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(riderData),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error registrating rider");
    return text ? JSON.parse(text) : null;
};