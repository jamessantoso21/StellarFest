package views.eventorganizer;

import controllers.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import views.ChangeProfileView;
import views.LoginView;

public class EventOrganizerHomeView {
    private BorderPane root;
    private VBox contentBox;
    private HBox navbar;
    private Label welcomeLabel;
    private Button changeProfileButton, eventViewButton, createEventButton, logoutButton;
    private Scene scene;

    public void init(String name, String userId) {
        root = new BorderPane();

        createEventButton = new Button("Create Event");
        changeProfileButton = new Button("Profile");
        eventViewButton = new Button("Event");
        logoutButton = new Button("Logout");

        contentBox = new VBox(20);
        welcomeLabel = new Label("Welcome, " + name + " (ID: " + userId + ")!");
    }

    public void layout() {
        navbar = new HBox(10);
        navbar.getChildren().addAll(createEventButton, eventViewButton, changeProfileButton);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(10));

        contentBox.getChildren().addAll(welcomeLabel, logoutButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        root.setTop(navbar);
        root.setCenter(contentBox);
    }

    public void setEventHandlers(Stage stage, String userId) {
    	createEventButton.setOnAction(e -> CreateEventView.display(stage, userId));
    	
        changeProfileButton.setOnAction(e -> ChangeProfileView.display(stage));

        eventViewButton.setOnAction(e -> OrganizedEventView.display(stage, userId));

        logoutButton.setOnAction(e -> {
            UserController.logout(); 
            LoginView.display(stage); 
        });
    }

    public static void display(Stage stage) {
        User user = UserController.getAuthenticatedUser(); 
        if (user == null) {
            LoginView.display(stage); 
            return;
        }

        EventOrganizerHomeView view = new EventOrganizerHomeView();
        view.init(user.getUsername(), user.getUserId());
        view.layout();
        view.setEventHandlers(stage, user.getUserId());

        view.scene = new Scene(view.root, 400, 300);
        stage.setScene(view.scene);
        stage.setTitle("Event Organizer Home");
        stage.show();
    }
}
