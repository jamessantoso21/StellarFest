package controllers;

import models.User;
import utils.Auth;
import utils.Response;

public class UserController {
    public static Response<Void> register(String email, String name, String password, String role) {
        Response<Void> validationResponse = User.checkRegisterInput(email, name, password);
        if (!validationResponse.isSuccess()) {
            return validationResponse;
        }

        if (getUserByEmail(email).getData() != null) {
            return Response.error("Email is already in use.");
        }

        if (getUserByUsername(name).getData() != null) {
            return Response.error("Username is already taken.");
        }

        return User.register(email, name, password, role);
    }

    public static Response<Void> login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return Response.error("Email and Password must be filled.");
        }

        Response<Void> loginResponse = User.login(email, password);
        if (loginResponse.isSuccess()) {
            Auth.set(User.getUserByEmail(email).getData()); 
        }

        return loginResponse;
    }

    public static Response<Void> changeProfile(String email, String name, String oldPassword, String newPassword) {
        User currentUser = Auth.get();

        if (currentUser == null) {
            return Response.error("No authenticated user found.");
        }

        Response<Void> validationResponse = currentUser.checkChangeProfileInput(email, name, oldPassword, newPassword);
        if (!validationResponse.isSuccess()) {
            return validationResponse;
        }

        if (!email.equals(currentUser.getEmail()) && getUserByEmail(email).getData() != null) {
            return Response.error("Email is already in use.");
        }

        if (!name.equals(currentUser.getUsername()) && getUserByUsername(name).getData() != null) {
            return Response.error("Username is already taken.");
        }

        return currentUser.changeProfile(email, name, oldPassword, newPassword);
    }

    public static void logout() {
        Auth.clear(); 
    }

    public static User getAuthenticatedUser() {
        return Auth.get();
    }

    public static String getAuthenticatedUserRole() {
        User currentUser = Auth.get();
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static String getAuthenticatedUserId() {
        User currentUser = Auth.get();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public static Response<User> getUserByEmail(String email) {
        if (email.isEmpty()) {
            return Response.error("Email must be filled.");
        }
        return User.getUserByEmail(email);
    }

    public static Response<User> getUserByUsername(String name) {
        if (name.isEmpty()) {
            return Response.error("Username must be filled.");
        }
        return User.getUserByUsername(name);
    }
}
