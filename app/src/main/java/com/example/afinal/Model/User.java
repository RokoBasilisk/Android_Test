package com.example.afinal.Model;

public class User {
    private String ID, Password, Email, Birthday;

    public User() {
    }

    public User(String ID, String password, String email, String birthday) {
        this.ID = ID;
        Password = password;
        Email = email;
        Birthday = birthday;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }
}
