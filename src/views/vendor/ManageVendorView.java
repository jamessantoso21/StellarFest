package views.vendor;

import controllers.VendorController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Response;
import models.VendorProduct;

public class ManageVendorView {
    private VBox root;
    private Label titleLabel, feedbackLabel;
    private TextField productNameField;
    private TextArea productDescriptionField;
    private Button backButton, submitButton, fetchButton;
    private Scene scene;

    public void init() {
        root = new VBox(15);

        titleLabel = new Label("Manage Vendor");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label productNameLabel = new Label("Product Name:");
        productNameField = new TextField();
        productNameField.setPromptText("Enter product name");

        Label productDescriptionLabel = new Label("Product Description:");
        productDescriptionField = new TextArea();
        productDescriptionField.setPromptText("Enter product description:");
        productDescriptionField.setWrapText(true);
        productDescriptionField.setPrefHeight(100);

        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: red;");

        backButton = new Button("Back");
        submitButton = new Button("Submit");
        fetchButton = new Button("Load Product");
        HBox buttonBox = new HBox(10, backButton, submitButton, fetchButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            titleLabel,
            productNameLabel, productNameField,
            productDescriptionLabel, productDescriptionField,
            feedbackLabel,
            buttonBox
        );
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
    }

    public void setEventHandlers(Stage stage) {
        submitButton.setOnAction(e -> handleSubmit());
        fetchButton.setOnAction(e -> handleFetch());
        backButton.setOnAction(e -> VendorHomeView.display(stage));
    }

    private void handleSubmit() {
        String productName = productNameField.getText();
        String productDescription = productDescriptionField.getText();

        Response<Void> response = VendorController.manageVendor(productDescription, productName);
        if (response.isSuccess()) {
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("Product information updated successfully!");
        } else {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(response.getMessage());
        }
    }

    private void handleFetch() {
        Response<VendorProduct> response = VendorController.getProduct();

        if (response.isSuccess() && response.getData() != null) {
            VendorProduct product = response.getData();
            productNameField.setText(product.getName());
            productDescriptionField.setText(product.getDescription());
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("Product information loaded successfully!");
        } else {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(response.getMessage());
        }
    }

    public static void display(Stage stage) {
        ManageVendorView view = new ManageVendorView();
        view.init();
        view.setEventHandlers(stage);
        view.scene = new Scene(view.root, 400, 300);
        stage.setScene(view.scene);
        stage.setTitle("Manage Vendor");
        stage.show();
    }
}
