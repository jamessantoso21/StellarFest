package views.admin;

import controllers.AdminController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.Response;
import views.ChangeProfileView;

import java.util.List;

public class AllUsersView {
	private Button changeProfileButton, allEventButton, allUserButton, backButton;
    private HBox navbar;
    private VBox root;
    private TableView<User> usersTable;
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

        usersTable = new TableView<>();
        VBox.setVgrow(usersTable, Priority.ALWAYS);

        TableColumn<User, String> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Name");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        usersTable.getColumns().addAll(userIdColumn, emailColumn, usernameColumn);

        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadUserData();

        deleteButton = new Button("Delete");

        root.getChildren().addAll(navbar, usersTable, deleteButton, backButton);
    }

    private void loadUserData() {
        Response<List<User>> response = AdminController.getAllUsers();
        if (response.isSuccess()) {
            usersTable.getItems().setAll(response.getData());
        } else {
            showErrorDialog("Error fetching users", response.getMessage());
        }
    }
    
    private void deleteUser(String userId) {
        Response<Void> response = AdminController.deleteUser(userId);
        if (response.isSuccess()) {
            usersTable.getItems().removeIf(user -> user.getUserId().equals(userId));
            showSuccessDialog("User deleted successfully");
        } else {
            showErrorDialog("Error deleting user", response.getMessage());
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
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                boolean confirmDeletion = showConfirmationDialog("Are you sure you want to delete this user?");
                if (confirmDeletion) {
                    deleteUser(selectedUser.getUserId());
                }
            } else {
                showErrorDialog("No user selected", "Please select a user to delete.");
            }
        });
        
        backButton.setOnAction(e -> AdminHomeView.display(stage));
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response.getButtonData().isDefaultButton()).isPresent();
    }

    public Scene getScene() {
        return new Scene(root, 400, 400);
    }

    public static void display(Stage stage) {
        AllUsersView view = new AllUsersView();
        view.init();
        view.setEventHandlers(stage);
        stage.setScene(view.getScene());
        stage.show();
    }
}
