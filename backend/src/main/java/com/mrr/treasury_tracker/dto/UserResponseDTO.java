package com.mrr.treasury_tracker.dto;

public class UserResponseDTO {

    private String token;
    private String email;
    private String userName;

    public UserResponseDTO(){}

    public UserResponseDTO(String token, String email, String userName) {
        this.token = token;
        this.email = email;
        this.userName = userName;
    }

    public UserResponseDTO(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
