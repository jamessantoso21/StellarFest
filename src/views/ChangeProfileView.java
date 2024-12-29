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

public class ChangeProfileView {

    private VBox root;
    private Label titleLabel;
    private TextField emailField, nameField;
    private PasswordField oldPasswordField, newPasswordField;
    private Button updateButton, backButton;
    private Scene scene;

    public void init() {
        root = new VBox(15);
        titleLabel = new Label("Change Profile");

        emailField = new TextField();
        nameField = new TextField();
        oldPasswordField = new PasswordField();
        newPasswordField = new PasswordField();

        updateButton = new Button("Update Profile");
        backButton = new Button("Back");

        emailField.setPromptText("Enter new email");
        nameField.setPromptText("Enter new name");
        oldPasswordField.setPromptText("Enter old password");
        newPasswordField.setPromptText("Enter new password");
    }

    public void layout() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Old Password:"), 0, 2);
        grid.add(oldPasswordField, 1, 2);
        grid.add(new Label("New Password:"), 0, 3);
        grid.add(newPasswordField, 1, 3);
        grid.add(updateButton, 0, 4);
        grid.add(backButton, 1, 4);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, grid);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage) {
        updateButton.setOnAction(e -> handleProfileUpdate(stage));
        backButton.setOnAction(e -> handleBackButton(stage));
    }

    private void handleProfileUpdate(Stage stage) {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (email.isEmpty() || name.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields are required!");
            return;
        }

        Response<Void> response = UserController.changeProfile(email, name, oldPassword, newPassword);

        if (response.isSuccess()) {
            showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, response.getMessage());
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
                showAlert(Alert.AlertType.ERROR, "Unknown role: " + userRole);
                LoginView.display(stage);
                break;
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void display(Stage stage) {
        ChangeProfileView view = new ChangeProfileView();
        view.init();
        view.layout();
        view.setEventHandlers(stage);

        view.scene = new Scene(view.root, 400, 400);
        stage.setScene(view.scene);
        stage.setTitle("Change Profile");
        stage.show();
    }
}
