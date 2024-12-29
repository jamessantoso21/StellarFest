package views.admin;

import controllers.AdminController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;
import models.Guest;
import models.Vendor;
import utils.Response;

import java.util.List;

public class EventDetailView {
    private VBox root;
    private Label titleLabel, eventNameLabel, eventDateLabel, eventLocationLabel, eventDescriptionLabel;
    private Label vendorListLabel, guestListLabel;
    private ListView<String> vendorListView, guestListView;
    private Button backButton;
    private Scene scene;

    public void init() {
        root = new VBox(20);

        titleLabel = new Label("Event Details");
        eventNameLabel = new Label();
        eventDateLabel = new Label();
        eventLocationLabel = new Label();
        eventDescriptionLabel = new Label();

        vendorListLabel = new Label("Vendor List");
        guestListLabel = new Label("Guest List");

        vendorListView = new ListView<>();
        guestListView = new ListView<>();

        backButton = new Button("Back");
    }

    public void layout() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(15);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        VBox eventDetailsVBox = new VBox(10);
        eventDetailsVBox.getChildren().addAll(eventNameLabel, eventDateLabel, eventLocationLabel, eventDescriptionLabel);

        VBox vendorVBox = new VBox(10);
        vendorVBox.getChildren().addAll(vendorListLabel, vendorListView);

        VBox guestVBox = new VBox(10);
        guestVBox.getChildren().addAll(guestListLabel, guestListView);

        HBox listsHBox = new HBox(20);
        listsHBox.getChildren().addAll(vendorVBox, guestVBox);
        listsHBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel,
                eventDetailsVBox,
                listsHBox,
                backButton
        );
    }

    public void setEventHandlers(Stage stage, String eventId) {
        Response<Event> eventResponse = AdminController.viewEventDetails(eventId);

        if (eventResponse.isSuccess()) {
            Event event = eventResponse.getData();
            eventNameLabel.setText("Event Name: " + event.getEventName());
            eventDateLabel.setText("Event Date: " + event.getEventDate());
            eventLocationLabel.setText("Event Location: " + event.getEventLocation());
            eventDescriptionLabel.setText("Event Description: " + event.getEventDescription());

            Response<List<Vendor>> vendorsResponse = AdminController.getVendorsByTransactionID(eventId);
            if (vendorsResponse.isSuccess()) {
                populateVendors(vendorsResponse.getData());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, vendorsResponse.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }

            Response<List<Guest>> guestsResponse = AdminController.getGuestsByTransactionID(eventId);
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

        backButton.setOnAction(e -> AllEventsView.display(stage));
    }

    public void populateVendors(List<Vendor> vendors) {
        vendorListView.getItems().clear();
        for (Vendor vendor : vendors) {
            vendorListView.getItems().add(vendor.getUsername());
        }
    }

    public void populateGuests(List<Guest> guests) {
        guestListView.getItems().clear();
        for (Guest guest : guests) {
            guestListView.getItems().add(guest.getUsername());
        }
    }

    public static void display(Stage stage, String eventId) {
        EventDetailView view = new EventDetailView();
        view.init();
        view.layout();
        view.setEventHandlers(stage, eventId);

        view.scene = new Scene(view.root, 800, 600);
        stage.setScene(view.scene);
        stage.setTitle("Event Details");
        stage.show();
    }
}
