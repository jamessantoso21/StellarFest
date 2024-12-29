package views;

import controllers.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Response;
import views.admin.AdminHomeView;
import views.eventorganizer.EventOrganizerHomeView;
import views.guest.GuestHomeView;
import views.vendor.VendorHomeView;

public class LoginView {
    private VBox root;
    private GridPane grid;
    private Label titleLabel, emailLabel, passwordLabel;
    private TextField emailInput;
    private PasswordField passwordInput;
    private Button loginButton;
    private Hyperlink registerLink;
    private Scene scene;

    public void init() {
        root = new VBox();
        grid = new GridPane();
        titleLabel = new Label("Login");
        emailLabel = new Label("Email:");
        passwordLabel = new Label("Password:");
        emailInput = new TextField();
        emailInput.setPromptText("Enter your email");
        passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter your password");
        loginButton = new Button("Login");
        registerLink = new Hyperlink("Don't have an account? Register here.");
    }

    public void layout() {
        grid.add(emailLabel, 0, 0);
        grid.add(emailInput, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordInput, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        root.getChildren().addAll(titleLabel, grid, registerLink);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage) {
        loginButton.setOnAction(e -> {
            String email = emailInput.getText();
            String password = passwordInput.getText();

            if (email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.", ButtonType.OK);
                alert.showAndWait();
            } else {
                Response<Void> response = UserController.login(email, password);

                if (response.isSuccess()) { 
                    String role = UserController.getAuthenticatedUserRole();
                    String userId = UserController.getAuthenticatedUserId();
                    if (role != null) {
                        switch (role) {
                            case "Admin":
                                AdminHomeView.display(stage);
                                break;
                            case "Event Organizer":
                                EventOrganizerHomeView.display(stage);
                                break;
                            case "Vendor":
                                VendorHomeView.display(stage);
                                break;
                            case "Guest":
                                GuestHomeView.display(stage);
                                break;
                            default:
                                showErrorAlert("Invalid role.");
                        }
                    } else {
                        showErrorAlert("Role not assigned.");
                    }
                } else {
                    showErrorAlert(response.getMessage()); 
                }
            }
        });

        registerLink.setOnAction(e -> RegisterView.display(stage));
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void display(Stage stage) {
        LoginView loginView = new LoginView();
        loginView.init();
        loginView.layout();
        loginView.setEventHandlers(stage);

        loginView.scene = new Scene(loginView.root, 400, 300);
        stage.setScene(loginView.scene);
        stage.setTitle("Login - StellarFest");
        stage.show();
    }
}
