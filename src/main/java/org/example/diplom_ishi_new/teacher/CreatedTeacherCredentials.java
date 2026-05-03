package org.example.diplom_ishi_new.teacher;

public class CreatedTeacherCredentials {

    private final String fullName;
    private final String username;
    private final String rawPassword;

    public CreatedTeacherCredentials(String fullName, String username, String rawPassword) {
        this.fullName = fullName;
        this.username = username;
        this.rawPassword = rawPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRawPassword() {
        return rawPassword;
    }
}
