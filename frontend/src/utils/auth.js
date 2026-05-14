/**
 * Reads the logged-in user from localStorage, but only if their userType
 * matches the expected role. Returns null otherwise.
 */
const readLoggedInUserOfType = (expectedType) => {
    try {
        const userType = localStorage.getItem("userType");
        if (userType !== expectedType) {
            return null;
        }
        const rawUser = localStorage.getItem("user");
        if (!rawUser) {
            return null;
        }
        const user = JSON.parse(rawUser);
        if (!user || user.id == null) {
            return null;
        }
        return user;
    } catch {
        return null;
    }
};

export const readLoggedInCustomer = () => readLoggedInUserOfType("customer");
export const readLoggedInRider = () => readLoggedInUserOfType("rider");
export const readLoggedInRestaurant = () => readLoggedInUserOfType("restaurant");