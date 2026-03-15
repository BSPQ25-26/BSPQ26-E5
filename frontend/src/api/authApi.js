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