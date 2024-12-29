package controllers;

import java.util.List;
import models.Event;
import models.Guest;
import models.Invitation;
import models.User;
import utils.Auth;
import utils.Response;

public class GuestController {

    public Response<Void> acceptInvitation(String eventId) {
        User currentUser = Auth.get();
        
        if (currentUser == null) {
            return Response.error("User is not authenticated");
        }

        if (currentUser.getRole().equals("Guest")) {
            return ((Guest) currentUser).acceptInvitation(eventId);
        }

        return Response.error("Error accepting invitation: user is not a guest");
    }
    
    public Response<List<Event>> viewInvitations(String email) {
        User currentUser = Auth.get();
        
        if (currentUser == null) {
            return Response.error("User is not authenticated");
        }
        if (currentUser.getRole().equals("Guest")) {
            return Guest.viewInvitations(email);
        }
        return Response.error("Error viewing invitations: user is not a guest");
    }
    
    public Response<List<Event>> viewPendingInvitations(String email) {
        User currentUser = Auth.get();
        
        if (currentUser == null) {
            return Response.error("User is not authenticated");
        }
        if (currentUser.getRole().equals("Guest")) {
            return Guest.viewPendingInvitations(email);
        }
        return Response.error("Error viewing invitations: user is not a guest");
    }

    public static Response<List<Event>> viewAcceptedEvents(String email) {
        return Guest.viewAcceptedEvents(email);
    }

    public Response<List<User>> getEventGuests(String eventId) {
        return Invitation.getEventGuests(eventId);
    }
}
