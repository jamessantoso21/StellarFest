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

public class AddGuestView {
    private VBox root;
    private Label titleLabel, errorLabel;
    private TableView<User> guestTable;
    private Button inviteButton, backButton;
    private ArrayList<User> selectedGuests = new ArrayList<>();
    private Scene scene;

    public void init() {
        root = new VBox(20);

        titleLabel = new Label("Add Guests");
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        guestTable = new TableView<>();
        guestTable.setPrefHeight(400);

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
                        selectedGuests.add(user);
                    } else {
                        selectedGuests.remove(user);
                    }
                });
                setGraphic(checkBox);
                setAlignment(Pos.CENTER);
            }
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        guestTable.getColumns().addAll(selectColumn, idColumn, emailColumn, nameColumn);

        HBox buttonsBox = new HBox(10, inviteButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, guestTable, errorLabel, buttonsBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    }

    public void setEventHandlers(Stage stage, String eventId, String userId) {
        Response<List<User>> uninvitedGuestsResponse = EventOrganizerController.getGuests(eventId);

        if (uninvitedGuestsResponse.isSuccess()) {
            populateUsers(uninvitedGuestsResponse.getData());
        } else {
            errorLabel.setText(uninvitedGuestsResponse.getMessage());
        }

        inviteButton.setOnAction(e -> {
            if (selectedGuests.isEmpty()) {
                errorLabel.setText("Please select at least one guest to invite.");
            } else {
                boolean allInvited = true;
                for (User guest : selectedGuests) {
                    Response<Void> response = InvitationController.sendInvitation(guest.getEmail(), eventId);
                    if (!response.isSuccess()) {
                        allInvited = false;
                        errorLabel.setText("Failed to send invitation to: " + guest.getEmail() + ". " + response.getMessage());
                    }
                }

                if (allInvited) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Invitations sent successfully!", ButtonType.OK);
                    alert.showAndWait();
                    selectedGuests.clear();
//                    stage.close();
                }
            }
        });

        backButton.setOnAction(e -> OrganizedEventDetailsView.display(stage, eventId, userId));
    }

    public void populateUsers(List<User> users) {
        guestTable.getItems().clear();
        guestTable.getItems().addAll(users);
    }

    public static void display(Stage stage, String eventId, String userId) {
        AddGuestView view = new AddGuestView();
        view.init();
        view.layout();
        view.setEventHandlers(stage, eventId, userId);

        view.scene = new Scene(view.root, 600, 500);
        stage.setScene(view.scene);
        stage.setTitle("Add Guests");
        stage.show();
    }
}
