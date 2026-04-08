package com.breakdown.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mechanics")
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "skills")
    private String skills;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    private Double latitude;
    private Double longitude;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "total_jobs")
    private Integer totalJobs = 0;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "mechanic", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters ──
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getMechanicName() { return user != null ? user.getName() : null; }
    public String getMechanicPhone() { return user != null ? user.getPhone() : null; }
    public String getSkills() { return skills; }
    public Integer getExperienceYears() { return experienceYears; }
    public Boolean getIsAvailable() { return isAvailable; }
    public Double getCurrentLatitude() { return currentLatitude; }
    public Double getCurrentLongitude() { return currentLongitude; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getRating() { return rating; }
    public Integer getTotalJobs() { return totalJobs; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Booking> getBookings() { return bookings; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setTotalJobs(Integer totalJobs) { this.totalJobs = totalJobs; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}