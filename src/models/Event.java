package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.Connect;
import utils.Response;

public class Event {
    private String eventId;
    private String eventName;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;
    private String organizerId;
    
    private static Connect db = Connect.getInstance();
    
    //constructor
    public Event(String eventId, String eventName, String eventDate, String eventLocation, String eventDescription,
            String organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.organizerId = organizerId;
    }

    // membuat event baru
    public static Response<Void> createEvent(String eventName, String date, String location, String description, String organizerId) {
        String eventId = getNextIncrementalId();
        
        if (eventId == null) {
            return Response.error("Failed to generate event ID");
        }

        String query = "INSERT INTO events (event_id, event_name, event_date, event_location, event_description, organizer_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, eventId);
            ps.setString(2, eventName);
            ps.setString(3, date);
            ps.setString(4, location);
            ps.setString(5, description);  
            ps.setString(6, organizerId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                return Response.error("Event creation failed");
            }
            
            return Response.success("Event created successfully", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Create event failed: " + e.getMessage());
        }
    }

    //mengambil detail event berdasarkan eventId nya
    public static Response<Event> viewEventDetails(String eventId) {
        String query = "SELECT * FROM events WHERE event_id = ?";
        
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String eventName = rs.getString("event_name");
                String eventDate = rs.getString("event_date");
                String eventLocation = rs.getString("event_location");
                String eventDescription = rs.getString("event_description");
                String organizerId = rs.getString("organizer_id");
                
                Event event = new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId);
                return Response.success("Event details fetched successfully", event);
            } else {
                return Response.error("Event not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching event details: " + e.getMessage());
        }
    }

    private static String getNextIncrementalId() {
    	// method untuk membuat eventId 
        String query = "SELECT event_id FROM events ORDER BY event_id DESC LIMIT 1";
        
        // format "EV001" -> Membuat ID event baru dengan format "EV" diikuti dengan 3 digit angka yang increment.
        try (ResultSet rs = db.execQuery(query)) {
            if (rs.next()) {
                String largestId = rs.getString("event_id");
                int nextIdNumber = Integer.parseInt(largestId.substring(2)) + 1;
                return String.format("EV%03d", nextIdNumber);
            } else {
                return "EV001"; // ID pertama jika belum ada event
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // getter untuk atribut-atribut Event
    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getOrganizerId() {
        return organizerId;
    }
}
