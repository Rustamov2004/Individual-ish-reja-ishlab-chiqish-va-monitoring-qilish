package org.example.diplom_ishi_new.teacher;

public class TeacherProfileUpdateResult {

    private final boolean credentialsChanged;
    private final String username;

    public TeacherProfileUpdateResult(boolean credentialsChanged, String username) {
        this.credentialsChanged = credentialsChanged;
        this.username = username;
    }

    public boolean isCredentialsChanged() {
        return credentialsChanged;
    }

    public String getUsername() {
        return username;
    }
}
