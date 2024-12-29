package controllers;

import java.util.List;

import models.Invitation;
import utils.Response;

public class InvitationController {

    public static Response<Void> sendInvitation(String email, String eventId) {
        return Invitation.sendInvitation(email, eventId);
    }

    public static Response<Void> acceptInvitation(String userId, String eventId) {
        return Invitation.acceptInvitation(userId, eventId);
    }

    public static Response<List<Invitation>> getInvitations(String email) {
        return Invitation.getInvitations(email);
    }
}
