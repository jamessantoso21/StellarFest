package models;

import utils.Connect;
import utils.Response;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Invitation {
    private String invitationId;
    private String eventId;
    private String userId;
    private String invitationStatus;
    private String invitationRole;
	private String eventName;

    private static Connect db = Connect.getInstance();

    public Invitation(String invitationId, String eventId, String userId, String invitationStatus, String invitationRole, String eventName) {
        this.invitationId = invitationId;
        this.eventId = eventId;
        this.userId = userId;
        this.invitationStatus = invitationStatus;
        this.invitationRole = invitationRole;
        this.eventName = eventName;
    }
    
    // Mengecek apakah undangan sudah diterima oleh user
    public static boolean hasAccepted(String userId, String eventId) {
        String query = String.format(
            "SELECT invitation_status FROM invitations WHERE user_id = '%s' AND event_id = '%s'",
            userId, eventId
        );
        
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                int status = rs.getInt("invitation_status");
                return status == 1; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }

    // method untuk membuat invitation baru
    public static Response<Void> sendInvitation(String email, String eventId) {
        String invitationId = getNextIncrementalId();

        if (invitationId == null) return Response.error("Failed to generate invitation ID.");

        String query = String.format(
                "INSERT INTO invitations (invitation_id, event_id, user_id, invitation_status, invitation_role) " +
                        "SELECT '%s', '%s', user_id, 0, user_role " +
                        "FROM users WHERE user_email = '%s'", 
                invitationId, eventId, email
        );

        int rowsAffected = db.execUpdate(query);

        if (rowsAffected == 0) {
            return Response.error("No such user with the provided email.");
        } else if (rowsAffected < 0) {
            return Response.error("Error sending invitation.");
        }

        return Response.success("Invitation sent successfully.", null);
    }

    // method untuk menerima suatu invitation
    public static Response<Void> acceptInvitation(String userId, String eventId) {
        String query = String.format(
                "UPDATE invitations SET invitation_status = 1 WHERE user_id = '%s' AND event_id = '%s'", 
                userId, eventId
        );

        int rowsAffected = db.execUpdate(query);

        if (rowsAffected == 0) {
            return Response.error("No such invitation found.");
        } else if (rowsAffected < 0) {
            return Response.error("Error accepting invitation.");
        }

        return Response.success("Invitation accepted.", null);
    }

    // Mendapatkan daftar undangan yang belum diterima berdasarkan email pengguna
    public static Response<List<Invitation>> getInvitations(String email) {
        String query = String.format(
                "SELECT i.invitation_id, i.event_id, i.user_id, i.invitation_role, e.event_name " +
                        "FROM invitations i " + 
                        "JOIN users u ON i.user_id = u.user_id " +
                        "JOIN events e ON i.event_id = e.event_id " +
                        "WHERE u.user_email = '%s' AND i.invitation_status = 0", email
        );

        ResultSet rs = db.execQuery(query);
        List<Invitation> invitations = new ArrayList<>();

        try {
            while (rs.next()) {
                String invitationId = rs.getString("invitation_id");
                String eventId = rs.getString("event_id");
                String userId = rs.getString("user_id");
                String invitationRole = rs.getString("invitation_role");
                String eventName = rs.getString("event_name");

                invitations.add(new Invitation(invitationId, eventId, userId, "Not Accepted", invitationRole, eventName));
            }

            if (invitations.isEmpty()) {
                return Response.error("No invitations found.");
            }

            return Response.success("Fetched invitations successfully.", invitations);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching invitations: " + e.getMessage());
        }
    }

    // membuat ID untuk invitation
    private static String getNextIncrementalId() {
        String query = "SELECT invitation_id FROM invitations ORDER BY invitation_id DESC LIMIT 1";
        ResultSet rs = db.execQuery(query);

        // format "IN001" -> Membuat ID event baru dengan format "IN" diikuti dengan 3 digit angka yang increment.
        try {
            if (rs.next()) {
                String largestId = rs.getString("invitation_id");
                int nextIdNumber = Integer.parseInt(largestId.substring(2)) + 1;
                return String.format("IN%03d", nextIdNumber);
            } else {
                return "IN001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getInvitationId() {
        return invitationId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public String getInvitationRole() {
        return invitationRole;
    }
    
    public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

    public static Response<List<User>> getEventGuests(String eventId) {
        String query = String.format(
                "SELECT u.user_id, u.user_email, u.user_name, u.user_role " +
                "FROM users u JOIN invitations i ON u.user_id = i.user_id " +
                "WHERE i.event_id = '%s' AND i.invitation_status = 1 AND i.invitation_role = 'Guest'", eventId
        );

        ResultSet rs = db.execQuery(query);
        List<User> guests = new ArrayList<>();

        try {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String userEmail = rs.getString("user_email");
                String userName = rs.getString("user_name");
                String userRole = rs.getString("user_role");

                User guest = new User(userId, userEmail, userName, null, userRole);
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching event guests: " + e.getMessage());
        }

        return Response.success("Fetched event guests successfully.", guests);
    }

	


}
