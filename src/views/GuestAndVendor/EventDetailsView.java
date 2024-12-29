package views.GuestAndVendor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;

public class EventDetailsView {
    private VBox root;
    private Label titleLabel;
    private Label eventIdLabel;
    private Label dateLabel;
    private Label locationLabel;
    private Label descriptionLabel;
    private Button backButton;
    private Scene scene;

    public void init(Event event) {
        root = new VBox(10);
        titleLabel = new Label(event.getEventName());
        eventIdLabel = new Label("Event ID: " + event.getEventId());
        dateLabel = new Label("Date: " + event.getEventDate());
        locationLabel = new Label("Location: " + event.getEventLocation());
        descriptionLabel = new Label("Description: " + event.getEventDescription());
        backButton = new Button("Back");
    }

    public void layout() {
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        String labelStyle = "-fx-font-size: 14px;";
        eventIdLabel.setStyle(labelStyle);
        dateLabel.setStyle(labelStyle);
        locationLabel.setStyle(labelStyle);
        descriptionLabel.setStyle(labelStyle);
        
        root.getChildren().addAll(
            titleLabel,
            eventIdLabel,
            dateLabel,
            locationLabel,
            descriptionLabel,
            backButton
        );
    }

    public void setEventHandlers(Stage stage, String userId, String userRole) {
        backButton.setOnAction(e -> AcceptedEventsView.display(stage, userId, userRole));
    }

    public static void display(Stage stage, Event event, String userId, String userRole) {
        EventDetailsView view = new EventDetailsView();
        view.init(event);
        view.layout();
        view.setEventHandlers(stage, userId, userRole);

        view.scene = new Scene(view.root, 600, 400);
        stage.setScene(view.scene);
        stage.setTitle("Event Details - " + event.getEventName());
        stage.show();
    }
}