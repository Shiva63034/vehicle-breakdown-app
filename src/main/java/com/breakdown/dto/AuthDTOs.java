package com.breakdown.dto;

import com.breakdown.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTOs {

    public static class RegisterRequest {
        @NotBlank
        private String name;
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        private String phone;
        private User.Role role = User.Role.USER;

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
        public User.Role getRole() { return role; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setRole(User.Role role) { this.role = role; }
    }

    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String email;
        private String name;
        private String role;
        private Long userId;

        public AuthResponse(String token, String email,
                            String name, String role, Long userId) {
            this.token = token;
            this.email = email;
            this.name = name;
            this.role = role;
            this.userId = userId;
        }

        public String getToken() { return token; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public Long getUserId() { return userId; }
    }
}