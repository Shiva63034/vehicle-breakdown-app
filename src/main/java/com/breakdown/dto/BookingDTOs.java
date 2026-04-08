package com.breakdown.dto;

import java.time.LocalDateTime;
import com.breakdown.entity.Booking;
import jakarta.validation.constraints.NotNull;

public class BookingDTOs {

    public static class BookingRequest {
        @NotNull private Long vehicleId;
        @NotNull private Booking.IssueType issueType;
        private String issueDescription;
        @NotNull private Double userLatitude;
        @NotNull private Double userLongitude;
        private String userAddress;
      
        public Long getVehicleId() { return vehicleId; }
        public Booking.IssueType getIssueType() { return issueType; }
        public String getIssueDescription() { return issueDescription; }
        public Double getUserLatitude() { return userLatitude; }
        public Double getUserLongitude() { return userLongitude; }
        public String getUserAddress() { return userAddress; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public void setIssueType(Booking.IssueType issueType) { this.issueType = issueType; }
        public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
        public void setUserLatitude(Double userLatitude) { this.userLatitude = userLatitude; }
        public void setUserLongitude(Double userLongitude) { this.userLongitude = userLongitude; }
        public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
    }

    public static class BookingResponse {
    	private Boolean paymentDone;
        private Long id;
        private String issueType;
        private String issueDescription;
        private String issueImageUrl;
        private String status;
        private Double userLatitude;
        private Double userLongitude;
        private String userAddress;
        private Double estimatedCost;
        private Double finalAmount;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String mechanic;
        
        public Boolean getPaymentDone() { return paymentDone; }
        public Long getId() { return id; }
        public String getIssueType() { return issueType; }
        public String getIssueDescription() { return issueDescription; }
        public String getIssueImageUrl() { return issueImageUrl; }
        public String getStatus() { return status; }
        public Double getUserLatitude() { return userLatitude; }
        public Double getUserLongitude() { return userLongitude; }
        public String getUserAddress() { return userAddress; }
        public Double getEstimatedCost() { return estimatedCost; }
        public Double getFinalAmount() { return finalAmount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public String getMechanic() { return mechanic; }
        
        public void setPaymentDone(Boolean paymentDone) { this.paymentDone = paymentDone; }
        public void setId(Long id) { this.id = id; }
        public void setIssueType(String issueType) { this.issueType = issueType; }
        public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
        public void setIssueImageUrl(String issueImageUrl) { this.issueImageUrl = issueImageUrl; }
        public void setStatus(String status) { this.status = status; }
        public void setUserLatitude(Double userLatitude) { this.userLatitude = userLatitude; }
        public void setUserLongitude(Double userLongitude) { this.userLongitude = userLongitude; }
        public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
        public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
        public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        public void setMechanic(String mechanic) { this.mechanic = mechanic; }
    }

    public static class CompleteBookingRequest {
        private Double finalAmount;
        private String completionNotes;

        public Double getFinalAmount() { return finalAmount; }
        public String getCompletionNotes() { return completionNotes; }
        public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
        public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
    }

    public static class FeedbackRequest {
        @NotNull private Integer rating;
        private String comment;

        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public void setRating(Integer rating) { this.rating = rating; }
        public void setComment(String comment) { this.comment = comment; }
    }
}