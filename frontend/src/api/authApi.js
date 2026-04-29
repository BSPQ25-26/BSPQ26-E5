const API_URL = "http://localhost:8080/api";

const resolveToken = (token) => token || localStorage.getItem("token") || "";

const buildAuthHeaders = (token) => ({
    "Content-Type": "application/json",
    Authorization: `Bearer ${resolveToken(token)}`,
});

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
    if (!response.ok) throw new Error(text || "Error registering rider");
    return text ? JSON.parse(text) : null;
};

export const getAllergens = async () => {
    const response = await fetch(`${API_URL}/allergens`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching allergens");
    const data = text ? JSON.parse(text) : [];
    return Array.isArray(data) ? data.map(item => item.name) : [];
};

export const getMenuByRestaurantId = async (restaurantId) => {
    const response = await fetch(`${API_URL}/restaurants/${restaurantId}/menu`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching menu");
    return text ? JSON.parse(text) : [];
};

export const createDish = async (restaurantId, dishData) => {
    const response = await fetch(`${API_URL}/dishes/${restaurantId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(dishData),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error creating dish");
    return text ? JSON.parse(text) : null;
};

export const updateDish = async (dishId, dishData) => {
    const response = await fetch(`${API_URL}/dishes/${dishId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(dishData),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error updating dish");
    return text ? JSON.parse(text) : null;
};

export const deleteDish = async (dishId) => {
    const response = await fetch(`${API_URL}/dishes/${dishId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        },
    });

    if (!response.ok) throw new Error("Error deleting dish");
    return true;
};

export const loginUser = async (loginType, payload) => {

    const BASE_URL = "http://localhost:8080";
    let endpoint = "";

    if (loginType === "customer") endpoint = `${BASE_URL}/sessions/users`;
    else if (loginType === "rider") endpoint = `${BASE_URL}/sessions/riders`;
    else if (loginType === "restaurant") endpoint = `${BASE_URL}/sessions/restaurants`;

    const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...payload, type: loginType })
    });

    if (response.status === 501 || response.status === 403) {
        return { isBypass: true, token: "dummy-dev-token", user: null };
    }

    if (!response.ok) {
        throw new Error("Login failed. Please check your credentials.");
    }

    const data = await response.json();

    // The backend returns { token, customer or rider or restaurant }.
    const user =
        (loginType === "customer" && data.customer) ||
        (loginType === "rider" && data.rider) ||
        (loginType === "restaurant" && data.restaurant) ||
        null;

    return { isBypass: false, token: data.token, user };
};

export const loginAdmin = async (email, password) => {
    const response = await fetch(`${API_URL}/auth/admin/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
        throw new Error("Admin login failed. Please check your credentials.");
    }

    const data = await response.json();
    return data.token;
};
  
export const getCustomerOrders = async (customerId) => {
    const response = await fetch(`${API_URL}/customers/${customerId}/orders`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching customer orders");
    return text ? JSON.parse(text) : [];
};

export const getCustomerDashboard = async (customerId) => {
    const response = await fetch(`${API_URL}/customers/${customerId}/dashboard`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching customer dashboard");
    return text ? JSON.parse(text) : null;
};

export const getRestaurantProfile = async (token) => {
    const response = await fetch(`${API_URL}/restaurants/profile`, {
        method: "GET",
        headers: buildAuthHeaders(token),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching restaurant profile");
    return text ? JSON.parse(text) : null;
};

export const updateRestaurantProfile = async (profileData, token) => {
    const response = await fetch(`${API_URL}/restaurants/profile`, {
        method: "PUT",
        headers: buildAuthHeaders(token),
        body: JSON.stringify(profileData),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error updating restaurant profile");
    return text ? JSON.parse(text) : null;
};

export const getRestaurantDashboard = async (token) => {
    const response = await fetch(`${API_URL}/restaurants/dashboard`, {
        method: "GET",
        headers: buildAuthHeaders(token),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching restaurant dashboard");
    return text ? JSON.parse(text) : null;
};

export const getRiderById = async (riderId, token) => {
    const response = await fetch(`${API_URL}/riders/${riderId}`, {
        method: "GET",
        headers: buildAuthHeaders(token),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching rider profile");
    return text ? JSON.parse(text) : null;
};

export const getRiderDashboard = async (token) => {
    const response = await fetch(`${API_URL}/riders/dashboard`, {
        method: "GET",
        headers: buildAuthHeaders(token),
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error fetching rider dashboard");
    return text ? JSON.parse(text) : null;
};

export const rejectRestaurantOrder = async (restaurantId, orderId, reason, token) => {
    const response = await fetch(`${API_URL}/restaurants/${restaurantId}/orders/${orderId}/reject`, {
        method: "POST",
        
        headers: buildAuthHeaders(token), 
        body: JSON.stringify({ reason }), 
    });

    const text = await response.text();
    if (!response.ok) throw new Error(text || "Error rejecting order");
    return text ? JSON.parse(text) : null;
};