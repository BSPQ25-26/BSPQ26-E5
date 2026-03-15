package com.justorder.backend.controller;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RiderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RegisterService registerService;
    private final RiderRepository riderRepository;
    private final RiderService riderService;

    /**
     * Constructor injection of all dependencies.
     *
     * @param registerService  Handles rider registration (IAM-3).
     * @param riderRepository  Direct DB access for delete-all utility.
     * @param riderService     Handles rider business logic (CO3).
     */
    public RiderController(RegisterService registerService,
                           RiderRepository riderRepository,
                           RiderService riderService) {
        this.registerService = registerService;
        this.riderRepository = riderRepository;
        this.riderService = riderService;
    }

    /**
     * Health-check endpoint.
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Registers a new rider account (IAM-3).
     *
     * @param request Rider registration data from the request body.
     * @return {@code 200 OK} on success, {@code 500} on failure.
     */
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRider(@RequestBody RiderDTO request) {
        try {
            this.registerService.registerRider(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<HttpStatus> updateStatus(@RequestBody String request) {
        // TODO: implement order status update
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{riderId}")
    public ResponseEntity<RiderDTO> getRider(@PathVariable String riderId) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    /**
     * Returns all orders assigned to a specific rider.
     *
     * <p>Example: {@code GET /api/riders/1/orders}</p>
     *
     * @param riderId The ID of the rider.
     * @return {@code 200 OK} with list of orders, or {@code 404} if rider not found.
     */
    @GetMapping("/{riderId}/orders")
    public ResponseEntity<List<OrderDTO>> getRiderOrders(@PathVariable Long riderId) {
        try {
            List<OrderDTO> orders = riderService.getRiderOrders(riderId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Allows a rider to reject an assigned order (CO3 — Driver Claim System).
     * <p><b>Example:</b> {@code POST /api/riders/1/orders/5/reject}</p>
     *
     * @param riderId  The ID of the rider rejecting the order.
     * @param orderId  The ID of the order being rejected.
     * @param body     JSON object with a {@code "reason"} field.
     * @return {@code 200 OK} with the updated {@link OrderDTO},
     *         {@code 404} if the order or rider is not found,
     *         {@code 403} if this order does not belong to the requesting rider,
     *         {@code 500} on unexpected errors.
     */
    @PostMapping("/{riderId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long riderId,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {

        String reason = body.get("reason");
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder = riderService.rejectOrder(riderId, orderId, reason);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes all riders from the database.
     *
     * @return {@code 200 OK} after deletion.
     */
    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}