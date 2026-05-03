package org.example.diplom_ishi_new.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeacherForm {

    @NotBlank(message = "O'qituvchi ism-familiyasini kiriting")
    @Size(min = 8, message = "Ism kamida 8 ta belgidan iborat bo'lsin")
    private String fullName;

    @NotBlank(message = "Kafedrani kiriting")
    private String department;

    @NotBlank(message = "Lavozimni kiriting")
    private String position;

    @NotBlank(message = "Telefon raqamini kiriting")
    private String phoneNumber;

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
}
