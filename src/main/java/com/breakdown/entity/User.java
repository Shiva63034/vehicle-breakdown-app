package com.breakdown.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public enum Role {
        USER, MECHANIC, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters ──
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public List<Booking> getBookings() { return bookings; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(Role role) { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String phone;
        private Role role;
        private Boolean isActive = true;

        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public User build() {
            User user = new User();
            user.setName(this.name);
            user.setEmail(this.email);
            user.setPassword(this.password);
            user.setPhone(this.phone);
            user.setRole(this.role);
            user.setIsActive(this.isActive);
            return user;
        }
    }
}