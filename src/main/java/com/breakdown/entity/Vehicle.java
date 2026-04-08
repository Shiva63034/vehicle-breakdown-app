package com.breakdown.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private String brand;

    private String model;

    @Column(nullable = false)
    private String vehicleNumber;       // was registrationNumber

    private String color;

    private String yearOfManufacture;   // was year

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getVehicleType() { return vehicleType; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getColor() { return color; }
    public String getYearOfManufacture() { return yearOfManufacture; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public void setColor(String color) { this.color = color; }
    public void setYearOfManufacture(String yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}