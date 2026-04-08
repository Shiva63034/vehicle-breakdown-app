package com.breakdown.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum IssueType {
        TYRE_PUNCTURE, BATTERY_DEAD, FUEL_EMPTY,
        ENGINE_ISSUE, TOWING_NEEDED, ACCIDENT_SUPPORT, OTHER
    }

    public enum BookingStatus {
        PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_type", nullable = false)
    private IssueType issueType;

    @Column(name = "issue_description")
    private String issueDescription;

    @Column(name = "issue_image_url")
    private String issueImageUrl;

    @Column(name = "user_latitude", nullable = false)
    private Double userLatitude;

    @Column(name = "user_longitude", nullable = false)
    private Double userLongitude;

    @Column(name = "user_address")
    private String userAddress;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "final_amount")
    private Double finalAmount;

    @Column(name = "completion_image_url")
    private String completionImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private Boolean paymentDone = false;

    @JsonIgnore
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Feedback feedback;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = BookingStatus.PENDING;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Vehicle getVehicle() { return vehicle; }
    public Mechanic getMechanic() { return mechanic; }
    public IssueType getIssueType() { return issueType; }
    public String getIssueDescription() { return issueDescription; }
    public String getIssueImageUrl() { return issueImageUrl; }
    public Double getUserLatitude() { return userLatitude; }
    public Double getUserLongitude() { return userLongitude; }
    public String getUserAddress() { return userAddress; }
    public BookingStatus getStatus() { return status; }
    public Double getEstimatedCost() { return estimatedCost; }
    public Double getFinalAmount() { return finalAmount; }
    public String getCompletionImageUrl() { return completionImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Boolean getPaymentDone() { return paymentDone; }
    public Feedback getFeedback() { return feedback; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    public void setIssueImageUrl(String issueImageUrl) { this.issueImageUrl = issueImageUrl; }
    public void setUserLatitude(Double userLatitude) { this.userLatitude = userLatitude; }
    public void setUserLongitude(Double userLongitude) { this.userLongitude = userLongitude; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
    public void setCompletionImageUrl(String completionImageUrl) { this.completionImageUrl = completionImageUrl; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setPaymentDone(Boolean paymentDone) { this.paymentDone = paymentDone; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }
}