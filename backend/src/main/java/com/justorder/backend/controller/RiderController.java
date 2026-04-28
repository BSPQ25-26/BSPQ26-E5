package com.justorder.backend.controller;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RiderDashboardDTO;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.dto.VerifyOrderPinRequestDTO;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RiderService;
import com.justorder.backend.service.SessionService;
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
    private final SessionService sessionService;

    public RiderController(RegisterService registerService,
                           RiderRepository riderRepository,
                           RiderService riderService,
                           SessionService sessionService) {
        this.registerService = registerService;
        this.riderRepository = riderRepository;
        this.riderService = riderService;
        this.sessionService = sessionService;
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }


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
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{riderId}")
    public ResponseEntity<RiderDTO> getRider(@PathVariable Long riderId) {
        try {
            return ResponseEntity.ok(riderService.getRider(riderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{riderId}/orders")
    public ResponseEntity<List<OrderDTO>> getRiderOrders(@PathVariable Long riderId) {
        try {
            List<OrderDTO> orders = riderService.getRiderOrders(riderId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RiderDashboardDTO> getRiderDashboard(@RequestHeader("Authorization") String authorization) {
        try {
            Long riderId = sessionService.getActiveRiderId(authorization);
            return ResponseEntity.ok(riderService.getRiderDashboard(riderId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


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

    @PostMapping("/{riderId}/orders/{orderId}/verify-pin")
    public ResponseEntity<OrderDTO> verifyOrderPin(
            @PathVariable Long riderId,
            @PathVariable Long orderId,
            @RequestBody VerifyOrderPinRequestDTO request) {

        if (request == null || request.getPin() == null || request.getPin().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder = riderService.verifyOrderPin(riderId, orderId, request.getPin());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}