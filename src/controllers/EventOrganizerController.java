package controllers;

import java.time.LocalDate;
import java.util.List;

import models.Event;
import models.EventOrganizer;
import models.User;
import utils.Response;

public class EventOrganizerController {

    public static Response<Void> createEvent(String eventName, LocalDate date, String location, String description, String organizerId) {
        Response<Void> checkResponse = EventOrganizer.checkCreateEventInput(eventName, date, location, description);
        if (!checkResponse.isSuccess()) {
            return Response.error(checkResponse.getMessage());
        }

        return EventOrganizer.createEvent(eventName, date.toString(), location, description, organizerId);
    }

    public static Response<List<Event>> viewOrganizedEvents(String userId) {
        return EventOrganizer.viewOrganizedEvents(userId);
    }

    public static Response<Event> viewOrganizedEventDetails(String eventId) {
        return EventOrganizer.viewOrganizedEventDetails(eventId);
    }

    public static Response<List<User>> getGuestsByTransactionID(String eventId) {
        return EventOrganizer.getGuestsByTransactionID(eventId);
    }

    public static Response<List<User>> getVendorsByTransactionID(String eventId) {
        return EventOrganizer.getVendorsByTransactionID(eventId);
    }
    
    public static Response<List<User>> getGuests(String eventId) {
        return EventOrganizer.getGuests(eventId);
    }

    public static Response<List<User>> getVendors(String eventId) {
        return EventOrganizer.getVendors(eventId);
    }
    
    public static Response<Void> checkAddVendorInput(String vendorId) {
		return EventOrganizer.checkAddVendorInput(vendorId);
	}
	
	public static Response<Void> checkAddGuestInput(String guestId) {
		return EventOrganizer.checkAddGuestInput(guestId);
	}

    public static Response<Void> checkCreateEventInput(String eventName, LocalDate date, String location, String description) {
        return EventOrganizer.checkCreateEventInput(eventName, date, location, description);
    }

    public static Response<Void> editEventName(String eventId, String eventName) {
        if (eventName.isEmpty()) {
            return Response.error("Event name must be filled");
        }

        return EventOrganizer.editEventName(eventId, eventName);
    }
}
