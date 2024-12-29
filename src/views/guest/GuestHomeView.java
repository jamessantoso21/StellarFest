package views.guest;

import controllers.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import views.ChangeProfileView;
import views.LoginView;
import views.GuestAndVendor.AcceptedEventsView;
import views.GuestAndVendor.InvitationView;


public class GuestHomeView {
	private User user;
    private VBox root;
    private Label welcomeLabel;
    private Button changeProfileButton, accEventViewButton, viewInvitationButton, logoutButton;
    private HBox navbar;
    private Scene scene;

    public void init(String name, String userId, User user) {
    	this.user = user;
        root = new VBox(20);

        welcomeLabel = new Label("Welcome, " + name + " (ID: " + userId + ")!");

        changeProfileButton = new Button("Profile");
        accEventViewButton = new Button("Event");
        viewInvitationButton = new Button("Invitation");
        logoutButton = new Button("Logout");

        navbar = new HBox(10);
        navbar.getChildren().addAll(changeProfileButton, accEventViewButton, viewInvitationButton);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(10));
    }

    public void layout() {
    	root.getChildren().addAll(welcomeLabel, navbar, logoutButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setSpacing(20);
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage, String userId, String userRole) {
        changeProfileButton.setOnAction(e -> ChangeProfileView.display(stage));
        accEventViewButton.setOnAction(e -> AcceptedEventsView.display(stage, userId, userRole));
        viewInvitationButton.setOnAction(e -> InvitationView.display(stage, user.getEmail())); 
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
        String userRole = user.getRole();
        GuestHomeView view = new GuestHomeView();
        view.init(user.getUsername(), user.getUserId(), user); 
        view.layout();
        view.setEventHandlers(stage, user.getUserId(), userRole);
        view.scene = new Scene(view.root, 400, 300);
        stage.setScene(view.scene);
        stage.setTitle("Guest Home");
        stage.show();
    }
}