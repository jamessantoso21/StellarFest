package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utils.Response;

public class Admin extends User {
	
	// mengambil semua events dari database
    public static Response<List<Event>> viewAllEvents() {
        return getAllEvents();
    }
    
    // mengambil detail informasi dari sebuah event berdasarkan eventIdnya
    public static Response<Event> viewEventDetails(String eventId) {
        return Event.viewEventDetails(eventId);
    }

    // menghapus event berdasarkan eventIdnya
    public static Response<Void> deleteEvent(String eventId) {
        String query = "DELETE FROM events WHERE event_id = ?";
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, eventId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.success("Event deleted successfully.", null);
            } else {
                return Response.error("No event found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error deleting event: " + e.getMessage());
        }
    }

    // menghapus user berdasarkan userIdnya
    public static Response<Void> deleteUser(String userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, userId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.success("User deleted successfully.", null);
            } else {
                return Response.error("No user found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error deleting user: " + e.getMessage());
        }
    }
    
    // mengambil semua pengguna dari database
    public static Response<List<User>> getAllUsers() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = db.preparedStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String username = rs.getString("user_name");
                String userRole = rs.getString("user_role");

                users.add(new User(userId, userEmail, username, null, userRole));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching users: " + e.getMessage());
        }
        return Response.success("Fetch users success", users);
    }

    // mengambil semua event dari database
    public static Response<List<Event>> getAllEvents() {
        String query = "SELECT * FROM events";
        List<Event> events = new ArrayList<>();
        try (PreparedStatement ps = db.preparedStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String eventId = rs.getString("event_id");
                String eventName = rs.getString("event_name");
                String eventDate = rs.getString("event_date");
                String eventLocation = rs.getString("event_location");
                String eventDescription = rs.getString("event_description");
                String organizerId = rs.getString("organizer_id");

                events.add(new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching events: " + e.getMessage());
        }
        return Response.success("Fetch events success", events);
    }

    // mengambil guest list dari suatu event berdasarkan eventIdnya
    public static Response<List<Guest>> getGuestsByTransactionID(String eventId) {
        String query = "SELECT DISTINCT u.user_id, u.user_email, u.user_name " +
                "FROM users u JOIN invitations i ON u.user_id = i.user_id " +
                "WHERE i.event_id = ? AND i.invitation_role = 'Guest' AND i.invitation_status = 1";
        List<Guest> guests = new ArrayList<>();
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String username = rs.getString("user_name");

                guests.add(new Guest(userId, userEmail, username, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching event guests: " + e.getMessage());
        }
        return Response.success("Fetch event guests success", guests);
    }

    // mengambil siapa saja vendor (vendor list) dari suatu event berdasarkan eventId nya
    public static Response<List<Vendor>> getVendorsByTransactionID(String eventId) {
        String query = "SELECT DISTINCT u.user_id, u.user_email, u.user_name " +
                "FROM users u JOIN invitations i ON u.user_id = i.user_id " +
                "WHERE i.event_id = ? AND i.invitation_role = 'Vendor' AND i.invitation_status = 1";
        List<Vendor> vendors = new ArrayList<>();
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String username = rs.getString("user_name");

                vendors.add(new Vendor(userId, userEmail, username, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching event vendors: " + e.getMessage());
        }
        return Response.success("Fetch event vendors success", vendors);
    }
}
