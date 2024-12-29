package views;

import controllers.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView {
    private VBox root;
    private GridPane grid;
    private Label titleLabel, emailLabel, nameLabel, passwordLabel, roleLabel;
    private TextField emailInput, nameInput;
    private PasswordField passwordInput;
    private ComboBox<String> roleComboBox;
    private Button registerButton;
    private Hyperlink loginLink;
    private Scene scene;

    public void init() {
        root = new VBox();
        grid = new GridPane();
        titleLabel = new Label("Create Your Account");
        emailLabel = new Label("Email:");
        nameLabel = new Label("Name:");
        passwordLabel = new Label("Password:");
        roleLabel = new Label("Role:");

        emailInput = new TextField();
        emailInput.setPromptText("Enter your email");

        nameInput = new TextField();
        nameInput.setPromptText("Enter your name");

        passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter your password");

        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Event Organizer", "Vendor", "Guest");
        roleComboBox.setValue("Guest"); 

        registerButton = new Button("Register");

        loginLink = new Hyperlink("Already have an account? Login here.");
    }

    public void layout() {
        grid.add(emailLabel, 0, 0);
        grid.add(emailInput, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameInput, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordInput, 1, 2);
        grid.add(roleLabel, 0, 3);
        grid.add(roleComboBox, 1, 3);
        grid.add(registerButton, 1, 4);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        root.getChildren().addAll(titleLabel, grid, loginLink);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage) {
        registerButton.setOnAction(e -> {
            String email = emailInput.getText();
            String name = nameInput.getText();
            String password = passwordInput.getText();
            String role = roleComboBox.getValue();

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.", ButtonType.OK);
                alert.showAndWait();
            } else {
                UserController userController = new UserController();
                if (UserController.register(email, name, password, role).isSuccess()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful!", ButtonType.OK);
                    alert.showAndWait();
                    LoginView.display(stage); 
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Registration failed: " + UserController.register(email, name, password, role).getMessage(), ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        loginLink.setOnAction(e -> LoginView.display(stage)); 
    }

    public static void display(Stage stage) {
        RegisterView registerView = new RegisterView();
        registerView.init();
        registerView.layout();
        registerView.setEventHandlers(stage);

        registerView.scene = new Scene(registerView.root, 400, 350);
        stage.setScene(registerView.scene);
        stage.setTitle("Register - StellarFest");
        stage.show();
    }
}
