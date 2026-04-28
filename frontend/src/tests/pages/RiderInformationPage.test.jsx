import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { within } from "@testing-library/react";
import RiderInformationPage from "../../pages/RiderInformationPage";
import { getRiderById, getRiderDashboard } from "../../api/authApi";
import { readLoggedInRider } from "../../utils/auth";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    Link: ({ children, to }) => <a href={to}>{children}</a>,
    useNavigate: () => mockNavigate,
}), { virtual: true });

jest.mock("../../api/authApi", () => ({
    getRiderById: jest.fn(),
    getRiderDashboard: jest.fn(),
}));

jest.mock("../../utils/auth", () => ({
    readLoggedInRider: jest.fn(),
}));

describe("RiderInformationPage", () => {
    const riderProfile = {
        id: 1,
        name: "Carlos Rider",
        email: "carlos.rider@test.com",
        phoneNumber: "+34 611 111 111",
        dni: "12345678A",
        starterPoint: {
            city: "Bilbao",
            province: "Bizkaia",
            country: "Spain",
            postalCode: "48001",
            number: "1",
        },
    };

    const dashboard = {
        riderId: 1,
        totalOrders: 2,
        pendingOrders: 2,
        inProgressOrders: 0,
        deliveredOrders: 0,
        cancelledOrders: 0,
        assignedOrders: [
            {
                id: 77,
                status: "Pending",
                createdAt: "2026-04-20T18:30:00Z",
                totalPrice: 25.5,
            },
        ],
    };

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem("token", "rider-token-123");
        localStorage.setItem("userType", "rider");
        localStorage.setItem("user", JSON.stringify({ id: 1, name: "Carlos Rider" }));
        readLoggedInRider.mockReturnValue({ id: 1, name: "Carlos Rider" });
        getRiderById.mockResolvedValue(riderProfile);
        getRiderDashboard.mockResolvedValue(dashboard);
    });

    test("renders rider profile and dashboard data", async () => {
        render(<RiderInformationPage />);

        expect(await screen.findByRole("heading", { name: /Information dashboard/i })).toBeInTheDocument();
        await waitFor(() => {
            expect(screen.queryByText(/Loading your rider data/i)).not.toBeInTheDocument();
        });

        expect(screen.getByRole("heading", { name: "Carlos Rider" })).toBeInTheDocument();
        expect(screen.getAllByText(/carlos\.rider@test\.com/i).length).toBeGreaterThan(0);
        expect(screen.getByText("Total orders")).toBeInTheDocument();
        expect(screen.getByText("25.50 EUR")).toBeInTheDocument();
        expect(screen.getByText(/Order #77/i)).toBeInTheDocument();
        expect(screen.getByText(/Bilbao/i)).toBeInTheDocument();
        expect(screen.getByText(/Bizkaia/i)).toBeInTheDocument();

        const statCards = screen.getAllByRole("article");

        const totalOrdersCard = statCards.find((card) => within(card).queryByText("Total orders"));
        const pendingOrdersCard = statCards.find((card) => within(card).queryByText("Pending orders"));
        const inProgressOrdersCard = statCards.find((card) => within(card).queryByText("In progress"));
        const deliveredOrdersCard = statCards.find((card) => within(card).queryByText("Delivered"));

        expect(totalOrdersCard).toBeTruthy();
        expect(pendingOrdersCard).toBeTruthy();
        expect(inProgressOrdersCard).toBeTruthy();
        expect(deliveredOrdersCard).toBeTruthy();

        expect(within(totalOrdersCard).getByText(String(dashboard.totalOrders))).toBeInTheDocument();
        expect(within(pendingOrdersCard).getByText(String(dashboard.pendingOrders))).toBeInTheDocument();
        expect(within(inProgressOrdersCard).getByText(String(dashboard.inProgressOrders))).toBeInTheDocument();
        expect(within(deliveredOrdersCard).getByText(String(dashboard.deliveredOrders))).toBeInTheDocument();
    });

    test("shows an access error when the rider session is missing", async () => {
        readLoggedInRider.mockReturnValue(null);

        render(<RiderInformationPage />);

        expect(await screen.findByText(/You must be logged in as a rider/i)).toBeInTheDocument();
        expect(getRiderById).not.toHaveBeenCalled();
        expect(getRiderDashboard).not.toHaveBeenCalled();
    });

    test("shows a load error when rider data cannot be fetched", async () => {
        getRiderById.mockRejectedValueOnce(new Error("load failed"));

        render(<RiderInformationPage />);

        expect(await screen.findByText(/Could not load rider data/i)).toBeInTheDocument();
        expect(getRiderDashboard).toHaveBeenCalledWith("rider-token-123");
    });
});
