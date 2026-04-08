package com.breakdown.controller;

import com.breakdown.service.InvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user/payments")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoice/{bookingId}")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long bookingId,
            Principal principal) {
        try {
            byte[] pdf = invoiceService.generateInvoice(bookingId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment", "BreakFree-Invoice-" + bookingId + ".pdf");
            headers.setContentLength(pdf.length);

            return ResponseEntity.ok().headers(headers).body(pdf);

        } catch (Exception e) {
            System.out.println("❌ Invoice error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
