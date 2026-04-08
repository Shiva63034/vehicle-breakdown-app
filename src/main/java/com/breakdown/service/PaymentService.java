package com.breakdown.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.breakdown.entity.Booking;
import com.breakdown.repository.BookingRepository;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BookingRepository bookingRepo;

    public PaymentService(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public JSONObject createOrder(Long bookingId) throws RazorpayException {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        System.out.println("💳 Creating order for booking: " + bookingId);

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(booking.getFinalAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking_" + bookingId);

        Order order = client.orders.create(orderRequest);
        System.out.println("✅ Order created: " + order);

        // Parse the order as JSON string to avoid Razorpay type issues
        JSONObject orderJson = new JSONObject(order.toString());

        JSONObject response = new JSONObject();
        response.put("orderId",     orderJson.getString("id"));
        response.put("amount",      orderJson.getInt("amount"));
        response.put("currency",    orderJson.getString("currency"));
        response.put("keyId",       keyId);
        response.put("bookingId",   bookingId);
        response.put("finalAmount", booking.getFinalAmount());

        System.out.println("✅ Response: " + response);
        return response;
    }

    public boolean verifyAndMarkPaid(Long bookingId, String razorpayOrderId,
            String razorpayPaymentId, String razorpaySignature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id",  razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature",  razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);
            if (isValid) {
                Booking booking = bookingRepo.findById(bookingId)
                        .orElseThrow(() -> new RuntimeException("Booking not found"));
                booking.setPaymentDone(true);
                bookingRepo.save(booking);
                System.out.println("✅ Payment verified for booking: " + bookingId);
                return true;
            }
            return false;
        } catch (RazorpayException e) {
            System.out.println("❌ Verify error: " + e.getMessage());
            return false;
        }
    }
}