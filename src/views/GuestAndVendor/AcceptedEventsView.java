package views.GuestAndVendor;

import controllers.GuestController;
import controllers.UserController;
import controllers.VendorController;
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
import views.LoginView;
import views.admin.AdminHomeView;
import views.eventorganizer.EventOrganizerHomeView;
import views.guest.GuestHomeView;
import views.vendor.VendorHomeView;
import utils.Auth;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AcceptedEventsView {
    private Button changeProfileButton, accEventViewButton, viewInvitationButton, logoutButton;
    private HBox navbar;
    
    private VBox root;
    private Label titleLabel;
    private Button backButton;
    private TableView<Event> tableView;
    private Scene scene;
	protected Stage stage;

    public void init() {
        root = new VBox(10);
        titleLabel = new Label("Your Accepted Events");
        backButton = new Button("Back");
        tableView = new TableView<>();
    }

    public void layout() {
        root.getChildren().addAll(titleLabel, backButton, tableView);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefWidth(600);
    }

    public void setEventHandlers(Stage stage, String userId, String userRole) {
    	backButton.setOnAction(e -> handleBackButton(stage));
        loadAcceptedEvents(userId, userRole, stage);
    }

    private void loadAcceptedEvents(String userId, String userRole, Stage stage) {
        String userEmail = Auth.get().getEmail();
        Response<List<Event>> response;
        
        if ("Vendor".equals(userRole)) {
            response = VendorController.viewAcceptedEvents(userEmail);  
        } 
        else {
            response = GuestController.viewAcceptedEvents(userEmail);  
        }

        if (response.isSuccess()) {
            List<Event> events = response.getData();
            ObservableList<Event> eventList = FXCollections.observableArrayList(events);
            tableView.setItems(eventList);

            TableColumn<Event, String> eventNameColumn = new TableColumn<>("Event Name");
            eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));

            TableColumn<Event, String> eventDateColumn = new TableColumn<>("Event Date");
            eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

            TableColumn<Event, String> eventLocationColumn = new TableColumn<>("Event Location");
            eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));

            TableColumn<Event, Void> detailsColumn = new TableColumn<>("Details");
            detailsColumn.setCellFactory(param -> new TableCell<Event, Void>() {
                private final Button detailBtn = new Button("View Details");
                {
                    detailBtn.setOnAction(e -> {
                        Event event = getTableView().getItems().get(getIndex());
                        EventDetailsView.display(stage, event, userId, userRole);
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailBtn);
                    }
                }
            });

            tableView.getColumns().clear();
            tableView.getColumns().addAll(eventNameColumn, eventDateColumn, eventLocationColumn, detailsColumn);
        } else {
            Text errorMessage = new Text("Failed to fetch events: " + response.getMessage());
            root.getChildren().add(errorMessage);
        }
    }
    
    private void handleBackButton(Stage stage) {
        String userRole = UserController.getAuthenticatedUserRole();
        if (userRole == null) {
            showAlert(Alert.AlertType.ERROR, "Unable to determine user role. Please log in again.");
            LoginView.display(stage);
            return;
        }
        switch (userRole) {
            case "Vendor":
                VendorHomeView.display(stage);
                break;
            case "Guest":
                GuestHomeView.display(stage);
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Unknown role: " + userRole);
                LoginView.display(stage);
                break;
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }

   
    public static void display(Stage stage, String userId, String userRole) {
        AcceptedEventsView view = new AcceptedEventsView();
        view.init();
        view.layout();
        view.setEventHandlers(stage, userId, userRole);

        view.scene = new Scene(view.root, 600, 400);
        stage.setScene(view.scene);
        stage.setTitle("View Accepted Events");
        stage.show();
    }
}
