import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import RestaurantInformationPage from "../../pages/RestaurantInformationPage";
import {
    getRestaurantDashboard,
    getRestaurantProfile,
    updateRestaurantProfile,
} from "../../api/authApi";
import { readLoggedInRestaurant } from "../../utils/auth";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

jest.mock("../../api/authApi", () => ({
    getRestaurantDashboard: jest.fn(),
    getRestaurantProfile: jest.fn(),
    updateRestaurantProfile: jest.fn(),
}));

jest.mock("../../utils/auth", () => ({
    readLoggedInRestaurant: jest.fn(),
}));

describe("RestaurantInformationPage", () => {
    const restaurantProfile = {
        id: 1,
        name: "La Marina Bistro",
        description: "Mediterranean restaurant near the seafront",
        phone: "+34 900 000 001",
        mondayWorkingHours: "09:00-22:00",
        tuesdayWorkingHours: "09:00-22:00",
        wednesdayWorkingHours: "09:00-22:00",
        thursdayWorkingHours: "09:00-22:00",
        fridayWorkingHours: "09:00-23:00",
        saturdayWorkingHours: "10:00-23:00",
        sundayWorkingHours: "10:00-22:00",
        cuisineCategoryNames: ["Italian"],
        localizations: [
            {
                city: "Bilbao",
                province: "Bizkaia",
                country: "Spain",
                postalCode: "48001",
                number: "12",
                longitude: "2,5",
                latitude: "43,2",
            },
        ],
    };

    const dashboard = {
        restaurantId: 1,
        totalOrders: 12,
        activeOrders: 3,
        cancelledOrders: 2,
        deliveredOrders: 7,
        totalRevenue: 123.45,
        totalRefunded: 9.99,
        recentOrders: [
            {
                id: 77,
                status: "Delivered",
                createdAt: "2026-04-20T18:30:00Z",
                totalPrice: 25.5,
            },
        ],
    };

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem("token", "restaurant-token-123");
        localStorage.setItem("userType", "restaurant");
        localStorage.setItem("user", JSON.stringify({ id: 1, name: "La Marina Bistro" }));
        readLoggedInRestaurant.mockReturnValue({ id: 1, name: "La Marina Bistro" });
        getRestaurantProfile.mockResolvedValue(restaurantProfile);
        getRestaurantDashboard.mockResolvedValue(dashboard);
        updateRestaurantProfile.mockResolvedValue(restaurantProfile);
    });

    test("renders dashboard data and normalizes coordinates with dot decimals", async () => {
        render(<RestaurantInformationPage />);

        expect(await screen.findByRole("heading", { name: /Restaurant Dashboard/i })).toBeInTheDocument();
        await waitFor(() => {
            expect(screen.queryByText(/Loading your restaurant data/i)).not.toBeInTheDocument();
        });
        expect(screen.getByText("Total orders")).toBeInTheDocument();
        expect(screen.getByText("12")).toBeInTheDocument();
        expect(screen.getByText("123.45 EUR")).toBeInTheDocument();
        expect(screen.getByText(/Order #77/i)).toBeInTheDocument();

        expect(screen.getByLabelText(/Italian/i)).toBeChecked();
        expect(screen.getByLabelText(/Japanese/i)).not.toBeChecked();
        expect(screen.getByLabelText(/Longitude/i)).toHaveValue("2.5");
        expect(screen.getByLabelText(/Latitude/i)).toHaveValue("43.2");
    });

    test("pads working hours on blur and submits edited profile payload", async () => {
        render(<RestaurantInformationPage />);

        await screen.findByRole("heading", { name: /Restaurant Dashboard/i });
        await waitFor(() => {
            expect(screen.queryByText(/Loading your restaurant data/i)).not.toBeInTheDocument();
        });

        fireEvent.change(screen.getByLabelText(/Monday hours/i), { target: { value: "9-18" } });
        fireEvent.blur(screen.getByLabelText(/Monday hours/i));

        expect(screen.getByLabelText(/Monday hours/i)).toHaveValue("09:00-18:00");

        fireEvent.click(screen.getByLabelText(/Japanese/i));
        fireEvent.change(screen.getByLabelText(/Longitude/i), { target: { value: "2,75" } });
        fireEvent.change(screen.getByLabelText(/Latitude/i), { target: { value: "43,11" } });

        fireEvent.click(screen.getByRole("button", { name: /Save changes/i }));

        await waitFor(() => {
            expect(updateRestaurantProfile).toHaveBeenCalledTimes(1);
        });

        const [payload, token] = updateRestaurantProfile.mock.calls[0];

        expect(token).toBe("restaurant-token-123");
        expect(payload.mondayWorkingHours).toBe("09:00-18:00");
        expect(payload.cuisineCategoryNames).toEqual(["Italian", "Japanese"]);
        expect(payload.localizations).toEqual([
            expect.objectContaining({
                city: "Bilbao",
                province: "Bizkaia",
                country: "Spain",
                postalCode: "48001",
                number: "12",
                longitude: 2.75,
                latitude: 43.11,
            }),
        ]);

        expect(await screen.findByText(/Profile updated successfully/i)).toBeInTheDocument();
    });

    test("shows an access error when the restaurant session is missing", async () => {
        readLoggedInRestaurant.mockReturnValue(null);

        render(<RestaurantInformationPage />);

        expect(await screen.findByText(/You must be logged in as a restaurant/i)).toBeInTheDocument();
        expect(getRestaurantProfile).not.toHaveBeenCalled();
        expect(getRestaurantDashboard).not.toHaveBeenCalled();
    });

    test("shows a load error when profile data cannot be fetched", async () => {
        getRestaurantProfile.mockRejectedValueOnce(new Error("load failed"));

        render(<RestaurantInformationPage />);

        expect(await screen.findByText(/Could not load restaurant data/i)).toBeInTheDocument();
        expect(getRestaurantDashboard).toHaveBeenCalledWith("restaurant-token-123");
    });

    test("shows a save error when update fails", async () => {
        updateRestaurantProfile.mockRejectedValueOnce(new Error("save failed"));

        render(<RestaurantInformationPage />);

        await screen.findByRole("heading", { name: /Restaurant Dashboard/i });
        await waitFor(() => {
            expect(screen.queryByText(/Loading your restaurant data/i)).not.toBeInTheDocument();
        });

        fireEvent.click(screen.getByRole("button", { name: /Save changes/i }));

        expect(await screen.findByText(/save failed/i)).toBeInTheDocument();
    });
});