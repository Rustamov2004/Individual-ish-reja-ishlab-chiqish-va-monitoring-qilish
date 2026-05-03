package org.example.diplom_ishi_new.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeacherProfileForm {

    @NotBlank(message = "F.I.Sh ni kiriting")
    @Size(min = 8, message = "F.I.Sh kamida 8 ta belgidan iborat bo'lsin")
    private String fullName;

    @NotBlank(message = "Kafedrani kiriting")
    private String department;

    @NotBlank(message = "Lavozimni kiriting")
    private String position;

    @NotBlank(message = "Telefon raqamini kiriting")
    private String phoneNumber;

    @NotBlank(message = "Loginni kiriting")
    @Size(min = 8, message = "Login kamida 8 ta belgidan iborat bo'lsin")
    private String username;

    private String newPassword;

    private String confirmPassword;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
