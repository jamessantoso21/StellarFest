package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utils.Response;
import utils.Connect;

public class Guest extends User {

    public Guest(String userId, String userEmail, String username, String userPassword) {
        super(userId, userEmail, username, userPassword, "Guest");
    }
    
    // menerima invitation berdasarkan eventId
    public Response<Void> acceptInvitation(String eventId) {
        return Invitation.acceptInvitation(this.userId, eventId);
    }

    // melihat daftar event yang sudah diaccept oleh user
    public static Response<List<Event>> viewAcceptedEvents(String email) {
        String query = 
            "SELECT e.event_id, e.event_name, e.event_date, e.event_location, e.event_description, e.organizer_id " +
            "FROM events e " +
            "JOIN invitations i ON e.event_id = i.event_id " +
            "JOIN users u ON u.user_id = i.user_id " +
            "WHERE u.user_email = ? AND i.invitation_role = 'Guest' AND i.invitation_status = 1";

        Connect db = Connect.getInstance();
        
        try (PreparedStatement stmt = db.preparedStatement(query)) {
            stmt.setString(1, email);  

            ResultSet rs = stmt.executeQuery();
            ArrayList<Event> events = new ArrayList<>();
            
            while (rs.next()) {
                String eventId = rs.getString("event_id");
                String eventName = rs.getString("event_name");
                String eventDate = rs.getString("event_date");
                String eventLocation = rs.getString("event_location");
                String eventDescription = rs.getString("event_description");
                String organizerId = rs.getString("organizer_id");
                
                events.add(new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId));
            }
            
            // jika tidak ada event yang diaccept user
            if (events.isEmpty()) {
                return Response.error("No accepted events found for the given email.");
            }
            
            return Response.success("Fetch accepted events success", events);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching accepted events: " + e.getMessage());
        }
    }

    // daftar event invitation yang belum diterima
	public static Response<List<Event>> viewPendingInvitations(String email) {
		 String query = 
			        "SELECT e.event_id, e.event_name, e.event_date, e.event_location, e.event_description, e.organizer_id " +
			        "FROM events e " +
			        "JOIN invitations i ON e.event_id = i.event_id " +
			        "JOIN users u ON u.user_id = i.user_id " +
			        "WHERE u.user_email = ? AND i.invitation_role = 'Guest' AND i.invitation_status = 0";
			    
			    Connect db = Connect.getInstance();
			    
			    try (PreparedStatement stmt = db.preparedStatement(query)) {
			        stmt.setString(1, email);
			        ResultSet rs = stmt.executeQuery();
			        ArrayList<Event> events = new ArrayList<>();
			        
			        while (rs.next()) {
			            String eventId = rs.getString("event_id");
			            String eventName = rs.getString("event_name");
			            String eventDate = rs.getString("event_date");
			            String eventLocation = rs.getString("event_location");
			            String eventDescription = rs.getString("event_description");
			            String organizerId = rs.getString("organizer_id");
			            
			            events.add(new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId));
			        }
			        
			        if (events.isEmpty()) {
			            return Response.error("No pending invitations found for the given email.");
			        }
			        
			        return Response.success("Fetch pending invitations success", events);
			    } catch (SQLException e) {
			        e.printStackTrace();
			        return Response.error("Error fetching pending invitations: " + e.getMessage());
			    }
	}


		public static Response<List<Event>> viewInvitations(String email) {
		    String query = 
		        "SELECT e.event_id, e.event_name, e.event_date, e.event_location, e.event_description, e.organizer_id " +
		        "FROM events e " +
		        "JOIN invitations i ON e.event_id = i.event_id " +
		        "JOIN users u ON u.user_id = i.user_id " +
		        "WHERE u.user_email = ? AND i.invitation_role = 'Guest' AND i.invitation_status = 0";
		    
		    Connect db = Connect.getInstance();
		    
		    try (PreparedStatement stmt = db.preparedStatement(query)) {
		        stmt.setString(1, email);
		        ResultSet rs = stmt.executeQuery();
		        ArrayList<Event> events = new ArrayList<>();
		        
		        while (rs.next()) {
		            String eventId = rs.getString("event_id");
		            String eventName = rs.getString("event_name");
		            String eventDate = rs.getString("event_date");
		            String eventLocation = rs.getString("event_location");
		            String eventDescription = rs.getString("event_description");
		            String organizerId = rs.getString("organizer_id");
		            
		            events.add(new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId));
		        }
		        
		        if (events.isEmpty()) {
		            return Response.error("No pending invitations found.");
		        }
		        
		        return Response.success("Fetch invitations success", events);
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return Response.error("Error fetching invitations: " + e.getMessage());
		    }
		}
}
