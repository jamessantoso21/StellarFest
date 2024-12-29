package views.admin;

import controllers.AdminController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;
import utils.Response;
import views.ChangeProfileView;

import java.util.List;

public class AllEventsView {
	private Button changeProfileButton, allEventButton, allUserButton, backButton;
    private HBox navbar;
    
    private VBox root;
    private TableView<Event> eventsTable;
    private Button deleteButton;

    public void init() {
    	
    	changeProfileButton = new Button("Profile");
        allEventButton = new Button("Event");
        allUserButton = new Button("Users");	
        backButton = new Button("Back");
        
        navbar = new HBox(10);
        navbar.getChildren().addAll(changeProfileButton, allEventButton, allUserButton);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(10));
        
        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        eventsTable = new TableView<>();
        VBox.setVgrow(eventsTable, Priority.ALWAYS);

        
        TableColumn<Event, String> eventIdColumn = new TableColumn<>("Event ID");
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        
        TableColumn<Event, String> eventNameColumn = new TableColumn<>("Event Name");
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));

        TableColumn<Event, String> eventDateColumn = new TableColumn<>("Event Date");
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

        eventsTable.getColumns().addAll(eventNameColumn, eventIdColumn, eventDateColumn);

        eventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadEventData();

        deleteButton = new Button("Delete");

        root.getChildren().addAll(navbar, eventsTable, deleteButton, backButton);
    }

    private void loadEventData() {
        Response<List<Event>> response = AdminController.viewAllEvents();
        if (response.isSuccess()) {
            eventsTable.getItems().setAll(response.getData());
        } else {
            showErrorDialog("Error fetching events", response.getMessage());
        }
    }

    private void deleteEvent(String eventId) {
        Response<Void> response = AdminController.deleteEvent(eventId);
        if (response.isSuccess()) {
            eventsTable.getItems().removeIf(event -> event.getEventId().equals(eventId));
            showSuccessDialog("Event deleted successfully");
        } else {
            showErrorDialog("Error deleting event", response.getMessage());
        }
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEventHandlers(Stage stage) {
    	
    	changeProfileButton.setOnAction(e -> ChangeProfileView.display(stage));

        allEventButton.setOnAction(e -> AllEventsView.display(stage));

        allUserButton.setOnAction(e -> AllUsersView.display(stage));

        deleteButton.setOnAction(e -> {
            Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                boolean confirmDeletion = showConfirmationDialog("Are you sure you want to delete this event?");
                if (confirmDeletion) {
                    deleteEvent(selectedEvent.getEventId());
                }
            } else {
                showErrorDialog("No event selected", "Please select an event to delete.");
            }
        });
        
        backButton.setOnAction(e -> AdminHomeView.display(stage));

        eventsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
                if (selectedEvent != null) {
                    String eventId = selectedEvent.getEventId();
                    EventDetailView.display(stage, eventId); 
                }
            }
        });
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response.getButtonData().isDefaultButton()).isPresent();
    }

    public Scene getScene() {
        return new Scene(root, 400, 300);
    }

    public static void display(Stage stage) {
        AllEventsView view = new AllEventsView();
        view.init();
        view.setEventHandlers(stage);
        stage.setScene(view.getScene());
        stage.show();
    }
}
