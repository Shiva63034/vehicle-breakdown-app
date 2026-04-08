package com.breakdown.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.breakdown.service.PaymentService;

@RestController
@RequestMapping("/api/user/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ── CREATE ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<?> createOrder(
            @PathVariable Long bookingId,
            Principal principal) {
        try {
            JSONObject order = paymentService.createOrder(bookingId);

            // Use opt() methods to safely extract values regardless of type
            Map<String, Object> response = new HashMap<>();
            response.put("orderId",     order.opt("orderId").toString());
            response.put("amount",      order.opt("amount").toString());
            response.put("currency",    order.opt("currency").toString());
            response.put("keyId",       order.opt("keyId").toString());
            response.put("bookingId",   order.opt("bookingId").toString());
            response.put("finalAmount", order.opt("finalAmount").toString());

            System.out.println("✅ Sending payment response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ createOrder error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create order: " + e.getMessage());
        }
    }

    // ── VERIFY PAYMENT ────────────────────────────────────────────────────────
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> payload,
            Principal principal) {
        try {
            Long bookingId           = Long.parseLong(payload.get("bookingId"));
            String razorpayOrderId   = payload.get("razorpayOrderId");
            String razorpayPaymentId = payload.get("razorpayPaymentId");
            String razorpaySignature = payload.get("razorpaySignature");

            boolean success = paymentService.verifyAndMarkPaid(
                    bookingId, razorpayOrderId, razorpayPaymentId, razorpaySignature);

            HashMap<String, Object> result = new HashMap<>();
            if (success) {
                result.put("success", true);
                result.put("message", "Payment successful!");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "Payment verification failed!");
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            System.out.println("❌ verifyPayment error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}