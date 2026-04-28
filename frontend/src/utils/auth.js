export const readLoggedInCustomer = () => {
    try {
        const userType = localStorage.getItem("userType");
        const rawUser = localStorage.getItem("user");
        if (userType !== "customer" || !rawUser) {
            return null;
        }
        const user = JSON.parse(rawUser);
        return user && user.id ? user : null;
    } catch {
        return null;
    }
};

export const readLoggedInRestaurant = () => {
    try {
        const userType = localStorage.getItem("userType");
        const rawUser = localStorage.getItem("user");
        if (userType !== "restaurant" || !rawUser) {
            return null;
        }
        const user = JSON.parse(rawUser);
        return user && user.id ? user : null;
    } catch {
        return null;
    }
};
