package views.eventorganizer;

import controllers.EventOrganizerController;
import controllers.InvitationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.Response;

import java.util.ArrayList;
import java.util.List;

public class AddVendorView {
    private VBox root;
    private Label titleLabel, errorLabel;
    private TableView<User> vendorTable;
    private Button inviteButton, backButton;
    private ArrayList<User> selectedVendors = new ArrayList<>();
    private Scene scene;

    public void init() {
        root = new VBox(20);

        titleLabel = new Label("Add Vendor");
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        vendorTable = new TableView<>();
        vendorTable.setPrefHeight(400);

        inviteButton = new Button("Invite");
        backButton = new Button("Back");
    }

    public void layout() {
        TableColumn<User, Boolean> selectColumn = new TableColumn<>("Select");
        TableColumn<User, String> idColumn = new TableColumn<>("User ID");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> nameColumn = new TableColumn<>("Name");

        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (checkBox.isSelected()) {
                        selectedVendors.add(user);
                    } else {
                        selectedVendors.remove(user);
                    }
                });
                setGraphic(checkBox);
                setAlignment(Pos.CENTER);
            }
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        vendorTable.getColumns().addAll(selectColumn, idColumn, emailColumn, nameColumn);

        HBox buttonsBox = new HBox(10, inviteButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, vendorTable, errorLabel, buttonsBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage, String eventId, String userId) {
        Response<List<User>> uninvitedVendorsResponse = EventOrganizerController.getVendors(eventId);

        if (uninvitedVendorsResponse.isSuccess()) {
            populateUsers(uninvitedVendorsResponse.getData());
        } else {
            errorLabel.setText(uninvitedVendorsResponse.getMessage());
        }

        inviteButton.setOnAction(e -> {
            if (selectedVendors.isEmpty()) {
                errorLabel.setText("Please select at least one guest to invite.");
            } else {
                boolean allInvited = true;
                for (User vendor : selectedVendors) {
                    Response<Void> response = InvitationController.sendInvitation(vendor.getEmail(), eventId);
                    if (!response.isSuccess()) {
                        allInvited = false;
                        errorLabel.setText("Failed to send invitation to: " + vendor.getEmail() + ". " + response.getMessage());
                    }
                }

                if (allInvited) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Invitations sent successfully!", ButtonType.OK);
                    alert.showAndWait();
                    selectedVendors.clear();
//                    stage.close();
                }
            }
        });

        backButton.setOnAction(e -> OrganizedEventDetailsView.display(stage, eventId, userId)); 
    }

    public void populateUsers(List<User> users) {
        vendorTable.getItems().clear();
        vendorTable.getItems().addAll(users);
    }

    public static void display(Stage stage, String eventId, String userId) {
        AddVendorView view = new AddVendorView();
        view.init();
        view.layout();
        view.setEventHandlers(stage, eventId, userId);

        view.scene = new Scene(view.root, 600, 500);
        stage.setScene(view.scene);
        stage.setTitle("Add Guests");
        stage.show();
    }
}
