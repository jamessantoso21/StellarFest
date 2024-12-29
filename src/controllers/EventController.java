package controllers;

import java.time.LocalDate;

import models.Event;
import utils.Response;

public class EventController {

    public static Response<Void> createEvent(String eventName, LocalDate date, String location, String description, String organizerId) {
        String dateString = date.toString();
        return Event.createEvent(eventName, dateString, location, description, organizerId);
    }

    public static Response<Event> viewEventDetails(String eventId) {
        return Event.viewEventDetails(eventId);
    }
}
