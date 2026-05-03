package org.example.diplom_ishi_new.report;

import jakarta.validation.constraints.NotBlank;

public class HeadResponseForm {

    @NotBlank(message = "Javob yozing")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
