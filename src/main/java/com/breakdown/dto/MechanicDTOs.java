package com.breakdown.dto;

public class MechanicDTOs {

    // ✅ Mechanic Profile
    public static class MechanicProfileRequest {
        private String skills;
        private Integer experienceYears;

        public String getSkills() { return skills; }
        public Integer getExperienceYears() { return experienceYears; }

        public void setSkills(String skills) { this.skills = skills; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    }

    // ✅ Update Location
    public static class UpdateLocationRequest {
        private Double latitude;
        private Double longitude;

        public Double getLatitude() { return latitude; }
        public Double getLongitude() { return longitude; }

        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }

    // ✅ Nearby Mechanic Response
    public static class NearbyMechanicResponse {
        private Long id;
        private String name;
        private String phone;
        private String skills;
        private Integer experienceYears;
        private Double rating;
        private Integer totalJobs;
        private Double latitude;
        private Double longitude;
        private Double distanceKm;

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getSkills() { return skills; }
        public Integer getExperienceYears() { return experienceYears; }
        public Double getRating() { return rating; }
        public Integer getTotalJobs() { return totalJobs; }
        public Double getLatitude() { return latitude; }
        public Double getLongitude() { return longitude; }
        public Double getDistanceKm() { return distanceKm; }

        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setSkills(String skills) { this.skills = skills; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public void setRating(Double rating) { this.rating = rating; }
        public void setTotalJobs(Integer totalJobs) { this.totalJobs = totalJobs; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    }

    // ✅ NEW: Complete Job Request (IMPORTANT)
    public static class CompleteJobRequest {
        private Double finalAmount;
        private String completionNotes;

        public Double getFinalAmount() { return finalAmount; }
        public String getCompletionNotes() { return completionNotes; }

        public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
        public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
    }
}