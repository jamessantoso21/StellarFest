package views.eventorganizer;

import controllers.EventOrganizerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Event;
import utils.Response;
import views.ChangeProfileView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class OrganizedEventView {
    private VBox root;
    private Label titleLabel;
    private Button backButton;
    private TableView<Event> tableView;
    private Scene scene;
    
    private HBox navbar;
    private Button changeProfileButton, eventViewButton, createEventViewButton;

    public void init(String userId) {
        root = new VBox(10);
        titleLabel = new Label("Your Events");
        backButton = new Button("Back");
        tableView = new TableView<>();
        
        changeProfileButton = new Button("Profile");
        eventViewButton = new Button("Event");
        createEventViewButton = new Button("Create Event");
        backButton = new Button("Back");
    }

    public void layout() {
    	navbar = new HBox(10);
        navbar.getChildren().addAll(createEventViewButton, eventViewButton, changeProfileButton);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(10));
    	
        root.getChildren().addAll(navbar, titleLabel, tableView, backButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefWidth(600);
    }

    public void setEventHandlers(Stage stage, String userId) {
    	createEventViewButton.setOnAction(e -> CreateEventView.display(stage, userId));
    	
        changeProfileButton.setOnAction(e -> ChangeProfileView.display(stage));

        eventViewButton.setOnAction(e -> OrganizedEventView.display(stage, userId));
    	

        Response<List<Event>> eventsResponse = EventOrganizerController.viewOrganizedEvents(userId);
        if (eventsResponse.isSuccess()) {
            List<Event> events = eventsResponse.getData();
            ObservableList<Event> eventList = FXCollections.observableArrayList(events);
            tableView.setItems(eventList);

            TableColumn<Event, String> eventIdColumn = new TableColumn<>("Event ID");
            eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));

            TableColumn<Event, String> eventNameColumn = new TableColumn<>("Event Name");
            eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));

            TableColumn<Event, String> eventDateColumn = new TableColumn<>("Event Date");
            eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

            TableColumn<Event, String> eventLocationColumn = new TableColumn<>("Event Location");
            eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));

            TableColumn<Event, Void> eventDetailsColumn = new TableColumn<>("Details");
            eventDetailsColumn.setCellFactory(param -> new TableCell<Event, Void>() {
                private final Button btn = new Button("View Details");

                {
                    btn.setOnAction(e -> {
                        Event event = getTableView().getItems().get(getIndex());
                        String eventId = event.getEventId();
                        OrganizedEventDetailsView.display(stage, eventId, userId);
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            });

            tableView.getColumns().addAll(eventIdColumn, eventNameColumn, eventDateColumn, eventLocationColumn, eventDetailsColumn);
        } else {
            Text errorMessage = new Text("Failed to fetch events: " + eventsResponse.getMessage());
            root.getChildren().add(errorMessage);
        }
        
        backButton.setOnAction(e -> EventOrganizerHomeView.display(stage));
    }

    public static void display(Stage stage, String userId) {
        OrganizedEventView view = new OrganizedEventView();
        view.init(userId);
        view.layout();
        view.setEventHandlers(stage, userId);

        view.scene = new Scene(view.root, 600, 400);
        stage.setScene(view.scene);
        stage.setTitle("View Organized Events");
        stage.show();
    }
}
