package com.breakdown.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.breakdown.dto.MechanicDTOs;
import com.breakdown.entity.Mechanic;
import com.breakdown.entity.User;
import com.breakdown.repository.MechanicRepository;
import com.breakdown.repository.UserRepository;

@Service
public class MechanicService {

    private final MechanicRepository mechanicRepo;
    private final UserRepository userRepo;

    public MechanicService(MechanicRepository mechanicRepo,
                           UserRepository userRepo) {
        this.mechanicRepo = mechanicRepo;
        this.userRepo = userRepo;
    }

    // ================= CREATE / UPDATE PROFILE =================
    public Mechanic createOrUpdateProfile(
            MechanicDTOs.MechanicProfileRequest dto,
            String email
    ) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Mechanic profile = mechanicRepo.findByUserId(user.getId())
                .orElse(null);

        if (profile == null) {
            profile = new Mechanic();
            profile.setUser(user);
            profile.setIsAvailable(true);
            profile.setRating(0.0);
            profile.setTotalJobs(0);
        }

        profile.setSkills(dto.getSkills());
        profile.setExperienceYears(dto.getExperienceYears());

        return mechanicRepo.save(profile);
    }

    // ================= GET MY PROFILE =================
    public Mechanic getMyProfile(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mechanicRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mechanic profile not found"));
    }

    // ================= GET PROFILE BY USER ID =================
    public Mechanic getMechanicProfile(Long userId) {
        return mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic profile not found"));
    }

    // ================= UPDATE PROFILE =================
    public Mechanic updateProfile(
            Long userId,
            MechanicDTOs.MechanicProfileRequest request
    ) {
        Mechanic mechanic = mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic profile not found"));

        mechanic.setSkills(request.getSkills());
        mechanic.setExperienceYears(request.getExperienceYears());

        return mechanicRepo.save(mechanic);
    }

    // ================= UPDATE LOCATION =================
    public void updateLocation(
            Long userId,
            MechanicDTOs.UpdateLocationRequest request
    ) {
        Mechanic mechanic = mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        mechanic.setCurrentLatitude(request.getLatitude());
        mechanic.setCurrentLongitude(request.getLongitude());

        mechanicRepo.save(mechanic);
    }

    // ================= TOGGLE AVAILABILITY =================
    public void toggleAvailability(Long userId) {
        Mechanic mechanic = mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        mechanic.setIsAvailable(!mechanic.getIsAvailable());

        mechanicRepo.save(mechanic);
    }

    // ================= NEARBY MECHANICS =================
    public List<Mechanic> getNearbyMechanics(Double lat, Double lng) {
        return mechanicRepo.findNearbyAvailableMechanics(lat, lng, 10.0);
    }

    // ================= GET ALL =================
    public List<Mechanic> getAllMechanics() {
        return mechanicRepo.findAll();
    }
}