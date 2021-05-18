package com.example.beetalk.Models;

public class User {

    String uid, name, phoneNumber, profilePicture;

    public User() {
    }

    public User(String uid, String name, String phoneNumber, String profilePicture) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
