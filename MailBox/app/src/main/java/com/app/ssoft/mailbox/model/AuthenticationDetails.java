package com.app.ssoft.mailbox.model;

public class AuthenticationDetails {

    private static AuthenticationDetails authDetails;
    private String emailID;
    private String password;

    public AuthenticationDetails() {
    }

    public static AuthenticationDetails getInstance() {
        if (authDetails == null) {
            authDetails = new AuthenticationDetails();
        }
        return authDetails;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
