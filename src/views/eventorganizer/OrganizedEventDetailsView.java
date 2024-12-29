package views.eventorganizer;

import controllers.EventOrganizerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;
import models.User;
import utils.Response;

import java.util.List;

public class OrganizedEventDetailsView {
    private VBox root;
    private Label titleLabel, eventNameLabel, eventDateLabel, eventLocationLabel, eventDescriptionLabel;
    private Label vendorListLabel, guestListLabel;
    private TextField eventNameTextField; 
    private Button saveEventNameButton;
    private ListView<String> vendorListView, guestListView;
    private Button backButton, addVendorButton, addGuestButton;
    private Scene scene;

    public void init() {
        root = new VBox(20);

        titleLabel = new Label("Event Details");
        eventNameLabel = new Label();
        eventNameTextField = new TextField();
        eventDateLabel = new Label();
        eventLocationLabel = new Label();
        eventDescriptionLabel = new Label();

        eventNameTextField.setEditable(true);

        saveEventNameButton = new Button("Save");

        vendorListLabel = new Label("Vendor List");
        guestListLabel = new Label("Guest List");

        vendorListView = new ListView<>();
        guestListView = new ListView<>();

        addVendorButton = new Button("Add Vendor");
        addGuestButton = new Button("Add Guest");

        backButton = new Button("Back");
    }

    public void layout() {
        HBox eventNameHBox = new HBox(10);
        eventNameHBox.getChildren().addAll(eventNameLabel, eventNameTextField, saveEventNameButton);
        eventNameHBox.setAlignment(Pos.CENTER);

        VBox vendorVBox = new VBox(10);
        vendorVBox.getChildren().addAll(vendorListLabel, vendorListView, addVendorButton);

        VBox guestVBox = new VBox(10);
        guestVBox.getChildren().addAll(guestListLabel, guestListView, addGuestButton);

        HBox listsHBox = new HBox(20);
        listsHBox.getChildren().addAll(vendorVBox, guestVBox);
        listsHBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel,
                eventNameHBox,
//                eventNameLabel,
                eventDateLabel,
                eventLocationLabel,
                eventDescriptionLabel,
                listsHBox,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(15);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        vendorListView.setPrefHeight(150);
        guestListView.setPrefHeight(150);
    }

    public void setEventHandlers(Stage stage, String eventId, String userId) {
        Response<Event> eventResponse = EventOrganizerController.viewOrganizedEventDetails(eventId);

        if (eventResponse.isSuccess()) {
            Event event = eventResponse.getData();
            eventNameLabel.setText("Event Name: ");
            eventNameTextField.setText(event.getEventName());  
            eventDateLabel.setText("Event Date: " + event.getEventDate());
            eventLocationLabel.setText("Event Location: " + event.getEventLocation());
            eventDescriptionLabel.setText("Event Description: " + event.getEventDescription());

            Response<List<User>> vendorsResponse = EventOrganizerController.getVendorsByTransactionID(eventId);
            if (vendorsResponse.isSuccess()) {
                populateVendors(vendorsResponse.getData());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, vendorsResponse.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }

            Response<List<User>> guestsResponse = EventOrganizerController.getGuestsByTransactionID(eventId);
            if (guestsResponse.isSuccess()) {
                populateGuests(guestsResponse.getData());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, guestsResponse.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, eventResponse.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }

        saveEventNameButton.setOnAction(e -> {
            String newEventName = eventNameTextField.getText().trim();

            if (newEventName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Event name cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            Response<Void> updateResponse = EventOrganizerController.editEventName(eventId, newEventName);

            if (updateResponse.isSuccess()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event name updated successfully!", ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, updateResponse.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

        backButton.setOnAction(e -> OrganizedEventView.display(stage, userId));

        addVendorButton.setOnAction(e -> {
            AddVendorView.display(stage, eventId, userId);  
        });

        addGuestButton.setOnAction(e -> {
            AddGuestView.display(stage, eventId, userId); 
        });
    }

    public void populateVendors(List<User> vendors) {
        vendorListView.getItems().clear();
        for (User vendor : vendors) {
            vendorListView.getItems().add(vendor.getUsername() + " (" + vendor.getEmail() + ")");
        }
    }

    public void populateGuests(List<User> guests) {
        guestListView.getItems().clear();
        for (User guest : guests) {
            guestListView.getItems().add(guest.getUsername() + " (" + guest.getEmail() + ")");
        }
    }

    public static void display(Stage stage, String eventId, String userId) {
        OrganizedEventDetailsView view = new OrganizedEventDetailsView();
        view.init();
        view.layout();
        view.setEventHandlers(stage, eventId, userId);

        view.scene = new Scene(view.root, 600, 500); 
        stage.setScene(view.scene);
        stage.setTitle("Event Details");
        stage.show();
    }
}
