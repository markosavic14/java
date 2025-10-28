package com.example.z4;

import com.example.z4.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}