package com.mvm.auth.dto;

//DTO for registration application
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String userName;
    private String password2;

    public RegisterRequestDTO() {
    }

    public RegisterRequestDTO(String email, String password, String userName,String password2) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
