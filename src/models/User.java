package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.Auth;
import utils.Connect;
import utils.Response;

public class User {
    protected String userId;
    protected String email;
    protected String username;
    protected String password;
    protected String role;

    protected static Connect db = Connect.getInstance();

    protected User() {}

    protected User(String userId, String email, String username, String password, String role) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // method untuk mendaftarkan user baru
    public static Response<Void> register(String email, String name, String password, String role) {
        String userId = getNextId();

        if (userId == null) return Response.error("Register failed: ID generation failed");

        PreparedStatement ps = db.preparedStatement(
                "INSERT INTO users(user_id, user_email, user_name, user_password, user_role) "
                        + "VALUES (?, ?, ?, ?, ?)"
        );

        try {
            ps.setString(1, userId);
            ps.setString(2, email);
            ps.setString(3, name);
            ps.setString(4, password); 
            ps.setString(5, role);
            ps.executeUpdate();
            return Response.success("Register success", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Register failed: " + e.getMessage());
        }
    }

    // validasi untuk masuk bagi user yang sudha terdaftar dengan email dan passwordnya
    public static Response<Void> login(String email, String password) {
        Response<User> userResponse = getUserByEmail(email);
        User user = userResponse.getData();

        if (!userResponse.isSuccess()) {
            return Response.error("Login failed: " + userResponse.getMessage());
        }

        if (user == null || !user.password.equals(password)) {
            return Response.error("Invalid credentials");
        }

        Auth.set(user);
        return Response.success("Login success", null);
    }

    // mengubah/mengupdate profile masing-masing user
    public Response<Void> changeProfile(String email, String name, String oldPassword, String newPassword) {
        PreparedStatement ps = db.preparedStatement(
                "UPDATE users "
                        + "SET user_email = ?, user_name = ?, user_password = ? "
                        + "WHERE user_id = ?"
        );

        try {
            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, newPassword); 
            ps.setString(4, this.userId);
            ps.executeUpdate();
            return Response.success("Profile updated successfully", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Profile update failed: " + e.getMessage());
        }
    }

    //mengambil user berdasarkan email
    public static Response<User> getUserByEmail(String email) {
        PreparedStatement ps = db.preparedStatement("SELECT * FROM users WHERE user_email = ?");
        ResultSet rs;

        try {
            ps.setString(1, email);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching user by email: " + e.getMessage());
        }

        try {
            if (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String username = rs.getString("user_name");
                String userPassword = rs.getString("user_password");
                String userRole = rs.getString("user_role");

                User user = new User(userId, userEmail, username, userPassword, userRole);
                return Response.success("User fetched successfully", user);
            } else {
                return Response.error("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error processing user data: " + e.getMessage());
        }
    }

    //mengambil user berdasarkan namanya
    public static Response<User> getUserByUsername(String name) {
        PreparedStatement ps = db.preparedStatement("SELECT * FROM users WHERE user_name = ?");
        ResultSet rs;

        try {
            ps.setString(1, name);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching user by username: " + e.getMessage());
        }

        try {
            if (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String username = rs.getString("user_name");
                String userPassword = rs.getString("user_password");
                String userRole = rs.getString("user_role");

                User user = new User(userId, userEmail, username, userPassword, userRole);
                return Response.success("User fetched successfully", user);
            } else {
                return Response.error("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error processing user data: " + e.getMessage());
        }
    }

    // validasi untuk register
    public static Response<Void> checkRegisterInput(String email, String name, String password) {
        if (email.isEmpty()) {
            return Response.error("Email must be filled");
        }
        if (name.isEmpty()) {
            return Response.error("Username must be filled");
        }
        if (password.isEmpty()) {
            return Response.error("Password must be filled");
        }
        if (password.length() < 5) {
            return Response.error("Password must be more than 5 characters");
        }

        return Response.success("Validation success", null);
    }

    //validasi input untuk change profile
    //asumsi semua elemen nama, email, password baru harus berbeda dengan yang sebelumnya
    public Response<Void> checkChangeProfileInput(String email, String name, String oldPassword, String newPassword) {
        if (email.isEmpty()) {
            return Response.error("Email must be filled");
        }
        if (name.isEmpty()) {
            return Response.error("Username must be filled");
        }
        if (newPassword.length() < 5) {
            return Response.error("Password must be at least 5 characters");
        }
        // jika email dan username ingin tetap bisa sama, bagian ini bisa di comment
        if (email.equals(this.email) || name.equals(this.username)) {
            return Response.error("Email and username must be different from the old one");
        }
        if (oldPassword.equals(newPassword)) {
            return Response.error("Password must be different from the old password");
        }
        if (!oldPassword.equals(this.password)) {
            return Response.error("Old Password is wrong");
        }

        return Response.success("Validation success", null);
    }

    private static String getNextId() {
        ResultSet rs = db.execQuery(
                "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1"
        );

        if (rs == null) {
            return null;
        }

        String nextId;
        try {
            if (rs.next()) {
                String largestId = rs.getString("user_id");
                int nextIdNumber = Integer.valueOf(largestId.substring(2)) + 1;
                nextId = String.format("US%03d", nextIdNumber);
            } else {
                nextId = "US001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            nextId = null;
        }
        return nextId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
