package org.example.diplom_ishi_new.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Login kiriting")
    @Size(min = 8, max = 30, message = "Login kamida 8 ta belgidan iborat bo'lsin")
    private String username;

    @NotBlank(message = "Parol kiriting")
    @Size(min = 8, max = 50, message = "Parol kamida 8 ta belgidan iborat bo'lsin")
    private String password;

    @NotBlank(message = "Parol tasdiqlansin")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
