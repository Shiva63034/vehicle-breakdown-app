package com.breakdown.service;

import com.breakdown.entity.Booking;
import com.breakdown.repository.BookingRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    private final BookingRepository bookingRepo;

    // Brand colors
    private static final DeviceRgb ORANGE      = new DeviceRgb(249, 115, 22);
    private static final DeviceRgb DARK        = new DeviceRgb(26, 26, 46);
    private static final DeviceRgb LIGHT_GRAY  = new DeviceRgb(248, 248, 248);
    private static final DeviceRgb TEXT_GRAY   = new DeviceRgb(100, 100, 100);
    private static final DeviceRgb GREEN       = new DeviceRgb(22, 163, 74);

    public InvoiceService(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public byte[] generateInvoice(Long bookingId) throws Exception {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!Boolean.TRUE.equals(booking.getPaymentDone())) {
            throw new RuntimeException("Payment not completed for this booking");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(40, 50, 40, 50);

        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ── HEADER ───────────────────────────────────────────────────────────
        Table header = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));

        // Left: Brand
        Cell brandCell = new Cell().setBorder(Border.NO_BORDER);
        brandCell.add(new Paragraph("BreakFree")
                .setFont(bold).setFontSize(28).setFontColor(ORANGE));
        brandCell.add(new Paragraph("Vehicle Breakdown Service")
                .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
        brandCell.add(new Paragraph("support@breakfree.in  |  +91 98765 43210")
                .setFont(regular).setFontSize(9).setFontColor(TEXT_GRAY));
        header.addCell(brandCell);

        // Right: Invoice info
        Cell invoiceCell = new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        invoiceCell.add(new Paragraph("INVOICE")
                .setFont(bold).setFontSize(22).setFontColor(DARK));
        invoiceCell.add(new Paragraph("#BF-" + bookingId)
                .setFont(bold).setFontSize(14).setFontColor(ORANGE));
        invoiceCell.add(new Paragraph("Date: " +
                (booking.getCompletedAt() != null
                    ? booking.getCompletedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    : booking.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))))
                .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
        header.addCell(invoiceCell);
        doc.add(header);

        // Divider
        doc.add(new LineSeparator(new SolidLine(1.5f))
                .setStrokeColor(ORANGE).setMarginTop(10).setMarginBottom(20));

        // ── BILLED TO + SERVICE INFO ──────────────────────────────────────────
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));

        // Billed To
        Cell billedTo = new Cell().setBorder(Border.NO_BORDER)
                .setBackgroundColor(LIGHT_GRAY).setPadding(15)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));
        billedTo.add(new Paragraph("BILLED TO")
                .setFont(bold).setFontSize(9).setFontColor(TEXT_GRAY));
        if (booking.getUser() != null) {
            billedTo.add(new Paragraph(booking.getUser().getName())
                    .setFont(bold).setFontSize(13).setFontColor(DARK).setMarginTop(4));
            billedTo.add(new Paragraph(booking.getUser().getEmail())
                    .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
            billedTo.add(new Paragraph(booking.getUser().getPhone())
                    .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
        }
        infoTable.addCell(billedTo);

        // Service Info
        Cell serviceInfo = new Cell().setBorder(Border.NO_BORDER)
                .setBackgroundColor(LIGHT_GRAY).setPadding(15).setPaddingLeft(20)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));
        serviceInfo.add(new Paragraph("SERVICE INFO")
                .setFont(bold).setFontSize(9).setFontColor(TEXT_GRAY));
        serviceInfo.add(new Paragraph(booking.getIssueType().name().replace("_", " "))
                .setFont(bold).setFontSize(13).setFontColor(DARK).setMarginTop(4));
        if (booking.getUserAddress() != null) {
            serviceInfo.add(new Paragraph("📍 " + booking.getUserAddress())
                    .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
        }
        if (booking.getMechanic() != null) {
            serviceInfo.add(new Paragraph("🔧 Mechanic: " + booking.getMechanic().getUser().getName())
                    .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
            serviceInfo.add(new Paragraph("⭐ Rating: " +
                    String.format("%.1f", booking.getMechanic().getRating()))
                    .setFont(regular).setFontSize(10).setFontColor(TEXT_GRAY));
        }
        infoTable.addCell(serviceInfo);
        doc.add(infoTable);
        doc.add(new Paragraph("").setMarginBottom(20));

        // ── LINE ITEMS TABLE ─────────────────────────────────────────────────
        Table lineItems = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20}))
                .setWidth(UnitValue.createPercentValue(100));

        // Table header row
        String[] headers = {"DESCRIPTION", "HSN/SAC", "AMOUNT"};
        for (String h : headers) {
            lineItems.addHeaderCell(
                new Cell().add(new Paragraph(h).setFont(bold).setFontSize(10)
                        .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(DARK).setPadding(10)
                        .setBorder(Border.NO_BORDER)
            );
        }

        // Service row
        String issueDesc = booking.getIssueType().name().replace("_", " ");
        if (booking.getIssueDescription() != null && !booking.getIssueDescription().isEmpty()) {
            issueDesc += " - " + booking.getIssueDescription();
        }
        addLineItem(lineItems, regular, issueDesc, "998714",
                String.format("₹%.2f", booking.getFinalAmount()), false);

        doc.add(lineItems);

        // ── AMOUNT SUMMARY ───────────────────────────────────────────────────
        double serviceAmount = booking.getFinalAmount();
        double gstRate       = 0.18;
        // Back-calculate base amount if GST inclusive, or add GST on top
        double baseAmount    = serviceAmount / (1 + gstRate);
        double gstAmount     = serviceAmount - baseAmount;
        double totalAmount   = serviceAmount;

        Table summary = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0);

        addSummaryRow(summary, regular, bold, "Subtotal (before GST)",
                String.format("₹%.2f", baseAmount), false);
        addSummaryRow(summary, regular, bold, "CGST (9%)",
                String.format("₹%.2f", gstAmount / 2), false);
        addSummaryRow(summary, regular, bold, "SGST (9%)",
                String.format("₹%.2f", gstAmount / 2), false);

        // Total row
        Cell totalLabel = new Cell().setBorder(Border.NO_BORDER)
                .setBackgroundColor(ORANGE).setPadding(12);
        totalLabel.add(new Paragraph("TOTAL AMOUNT")
                .setFont(bold).setFontSize(13).setFontColor(ColorConstants.WHITE));
        Cell totalValue = new Cell().setBorder(Border.NO_BORDER)
                .setBackgroundColor(ORANGE).setPadding(12)
                .setTextAlignment(TextAlignment.RIGHT);
        totalValue.add(new Paragraph(String.format("₹%.2f", totalAmount))
                .setFont(bold).setFontSize(13).setFontColor(ColorConstants.WHITE));
        summary.addCell(totalLabel);
        summary.addCell(totalValue);
        doc.add(summary);

        // ── PAYMENT STATUS ───────────────────────────────────────────────────
        doc.add(new Paragraph("").setMarginBottom(16));
        Table payStatus = new Table(UnitValue.createPercentArray(new float[]{100}))
                .setWidth(UnitValue.createPercentValue(100));
        Cell statusCell = new Cell().setBorder(new SolidBorder(GREEN, 1.5f))
                .setBackgroundColor(new DeviceRgb(240, 253, 244)).setPadding(12);
        statusCell.add(new Paragraph("✅  PAYMENT COMPLETED  |  Paid via Razorpay  |  Amount: ₹" +
                String.format("%.2f", totalAmount))
                .setFont(bold).setFontSize(11).setFontColor(GREEN)
                .setTextAlignment(TextAlignment.CENTER));
        payStatus.addCell(statusCell);
        doc.add(payStatus);

        // ── FOOTER ───────────────────────────────────────────────────────────
        doc.add(new Paragraph("").setMarginBottom(20));
        doc.add(new LineSeparator(new SolidLine(0.5f))
                .setStrokeColor(TEXT_GRAY).setMarginBottom(10));
        doc.add(new Paragraph(
                "Thank you for choosing BreakFree! For any queries contact support@breakfree.in")
                .setFont(regular).setFontSize(9).setFontColor(TEXT_GRAY)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("This is a computer generated invoice and does not require a signature.")
                .setFont(regular).setFontSize(8).setFontColor(TEXT_GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        doc.close();
        return baos.toByteArray();
    }

    private void addLineItem(Table table, PdfFont font,
            String desc, String hsn, String amount, boolean shade) {
    	DeviceRgb bg = shade ? LIGHT_GRAY : new DeviceRgb(255, 255, 255);
        table.addCell(new Cell().add(new Paragraph(desc).setFont(font).setFontSize(10))
                .setBackgroundColor(bg).setPadding(10).setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 1)));
        table.addCell(new Cell().add(new Paragraph(hsn).setFont(font).setFontSize(10))
                .setBackgroundColor(bg).setPadding(10).setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 1)));
        table.addCell(new Cell().add(new Paragraph(amount).setFont(font).setFontSize(10))
                .setBackgroundColor(bg).setPadding(10).setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 1)));
    }

    private void addSummaryRow(Table table, PdfFont regular, PdfFont bold,
            String label, String value, boolean isTotal) {
        Cell lCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 0.5f));
        lCell.add(new Paragraph(label).setFont(isTotal ? bold : regular)
                .setFontSize(isTotal ? 12 : 10).setFontColor(isTotal ? DARK : TEXT_GRAY));
        Cell vCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 0.5f));
        vCell.add(new Paragraph(value).setFont(isTotal ? bold : regular)
                .setFontSize(isTotal ? 12 : 10).setFontColor(isTotal ? DARK : TEXT_GRAY));
        table.addCell(lCell);
        table.addCell(vCell);
    }
}
