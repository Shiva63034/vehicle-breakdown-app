package com.breakdown.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mechanic_id", nullable = false)
    private Mechanic mechanic;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters ──
    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public User getUser() { return user; }
    public Mechanic getMechanic() { return mechanic; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public void setUser(User user) { this.user = user; }
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}