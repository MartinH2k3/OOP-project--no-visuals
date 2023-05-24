package gui;

import dataStorage.IdManager;
import dataStorage.UserMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.Rating;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dataStorage.Venues;
import users.*;
import venueData.*;

import java.util.Stack;

public class AppScreen extends Application {
    /** GUI for main */
    public AppScreen(User user, UserMap users) {
        this.user = user; this.users = users;
    }

    // uses of multiThreading
    private void uploadUsers(){
        new Thread(() -> {
            users.upload("src/main/resources/users");
            System.out.println("Users uploaded");
        }).start();
    }

    private void uploadVenues(){
        new Thread(() -> {
            venues.upload("src/main/resources/venues");
            System.out.println("Venues uploaded");
        }).start();
    }

    private void loadVenues(){
        new Thread(() -> {
            venues.load("src/main/resources/venues");
            System.out.println("Venues loaded");
        }).start();
    }

    User user;
    UserMap users;
    Venues venues;
    IdManager idManager;

    @Override
    public void start(Stage primaryStage) {
        venues = new Venues();
        loadVenues();
        idManager = new IdManager("src/main/resources/idTracker.txt");

        primaryStage.setOnCloseRequest(event -> {
            uploadVenues();
            uploadUsers();
            primaryStage.close();
            Platform.exit();
        });


        HBox mainLayout = new HBox(50);
        mainLayout.setAlignment(Pos.CENTER);

        VBox buttonLayout = new VBox(20);
        buttonLayout.setAlignment(Pos.TOP_LEFT);

        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> {
            uploadVenues();
            uploadUsers();
            primaryStage.close();
            var loginScreen = new LoginScreen();
            loginScreen.start(new Stage());
        });

        Button createVenueButton = new Button("Create Venue");
        createVenueButton.setOnAction(event -> {
            Stage venueStage = new Stage();
            VBox venueLayout = createVenueForm(venueStage);
            Scene venueScene = new Scene(venueLayout, 500, 500);
            venueStage.setTitle("Create Venue");
            venueStage.setScene(venueScene);
            venueStage.show();
        });

        Button browseButton = new Button("Browse Venues");
        ComboBox<String> venueBrowseDropdown = new ComboBox<>(FXCollections.observableArrayList("All", "Restaurant", "Experience", "Shop"));
        venueBrowseDropdown.getSelectionModel().selectFirst();

        HBox browseButtonLayout = new HBox(10);
        browseButtonLayout.getChildren().addAll(browseButton, venueBrowseDropdown);

        TextArea venueTextArea = new TextArea();
        venueTextArea.setEditable(false);
        ScrollPane scrollPane = new ScrollPane(venueTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        browseButton.setOnAction(event -> {
            if (venueBrowseDropdown.getValue().equals("All")) {
                venueTextArea.setText(venues.toString());
            }
            else {
                venueTextArea.setText(venues.toString(venueBrowseDropdown.getValue()));
            }

        });

        ComboBox<String> venueSelectDropdown = new ComboBox<>(FXCollections.observableArrayList("Restaurant", "Experience", "Shop"));
        venueSelectDropdown.getSelectionModel().selectFirst();
        TextField venueId = new TextField();
        venueId.setPromptText("ID");
        venueId.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        })); // Only allow numbers to be entered
        Button selectButton = new Button("Select Venue");
        selectButton.setOnAction(event -> {
            String selectedVenueType = venueSelectDropdown.getValue();
            try {
                long selectedVenueId = Long.parseLong(venueId.getText());
                inspectVenueWindow(selectedVenueType, selectedVenueId);
            } catch (NullPointerException e ) {
                venueTextArea.setText("Pay closer attention to available venue IDs!");
            } catch (NumberFormatException e) {
                venueTextArea.setText("Browse venues again and pick one of the IDs!");
            }
        });
        venueId.setOnAction(e -> selectButton.fire());
        HBox selectButtonLayout = new HBox(10);
        selectButtonLayout.getChildren().addAll(selectButton, venueSelectDropdown, venueId);

        if (user instanceof Traveler) {
            Button pathManagerButton = new Button("Path Manager");
            pathManagerButton.setOnAction(event -> {
                inspectPathsWindow();
            });
            buttonLayout.getChildren().add(pathManagerButton);
        }

        if (user instanceof Admin) {
            Button adminButton = new Button("Administration");
            adminButton.setOnAction(event -> {
                Stage adminStage = new Stage();
                VBox adminLayout = createAdminWindow();
                Scene adminScene = new Scene(adminLayout, 500, 600);
                adminStage.setTitle("Administration");
                adminStage.setScene(adminScene);
                adminStage.show();
            });
            buttonLayout.getChildren().add(adminButton);
        }

        buttonLayout.getChildren().addAll(createVenueButton, browseButtonLayout, selectButtonLayout, logoutButton);
        mainLayout.getChildren().addAll(buttonLayout, scrollPane);

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setTitle("TravelApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createVenueForm(Stage venueStage) {
        VBox venueLayout = new VBox(10);
        venueLayout.setAlignment(Pos.CENTER);

        Label systemMessage = new Label();
        systemMessage.setText("Create Venue");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField streetField = new TextField();
        streetField.setPromptText("Street");

        TextField streetNumberField = new TextField();
        streetNumberField.setPromptText("Street Number");
        streetNumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        nameField.setOnAction(event -> cityField.requestFocus());
        cityField.setOnAction(event -> streetField.requestFocus());
        streetField.setOnAction(event -> streetNumberField.requestFocus());

        RadioButton radioButton1 = new RadioButton("Experience");
        RadioButton radioButton2 = new RadioButton("Restaurant");
        RadioButton radioButton3 = new RadioButton("Shop");

        radioButton1.setSelected(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton3.setToggleGroup(toggleGroup);

        toggleGroup.getSelectedToggle();

        HBox radioButtonsLayout = new HBox(10);
        radioButtonsLayout.setAlignment(Pos.BOTTOM_CENTER);
        radioButtonsLayout.getChildren().addAll(radioButton1, radioButton2, radioButton3);

        CheckBox ownershipCheckBox = new CheckBox("I own this property");
        venueLayout.getChildren().add(ownershipCheckBox);
        ownershipCheckBox.setVisible(false);

        VBox ExperienceInfoLayout = new VBox(10);
        TextField activityTextField = new TextField();
        activityTextField.setPromptText("Add Activity Type");
        TextField groupSizeTextField = new TextField();
        groupSizeTextField.setPromptText("Add Group Size");
        groupSizeTextField.textFormatterProperty().setValue(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));
        ExperienceInfoLayout.getChildren().addAll(activityTextField, groupSizeTextField);

        VBox RestaurantInfoLayout = new VBox(10);
        TextField contactTextField = new TextField();
        contactTextField.setPromptText("Add Contact");
        RestaurantInfoLayout.getChildren().add(contactTextField);

        VBox ShopInfoLayout = new VBox(10);
        TextField productCategoryTextField = new TextField();
        productCategoryTextField.setPromptText("Add Product Category");
        ShopInfoLayout.getChildren().add(productCategoryTextField);

        ExperienceInfoLayout.setVisible(false);
        RestaurantInfoLayout.setVisible(false);
        ShopInfoLayout.setVisible(false);

        if (user instanceof PropertyOwner){
            ownershipCheckBox.setVisible(true);

            ownershipCheckBox.setOnAction(e -> {
                if (ownershipCheckBox.isSelected()) {
                    String type = ((RadioButton)toggleGroup.getSelectedToggle()).getText();
                    if (type.equals("Experience")){
                        ExperienceInfoLayout.setVisible(true);
                    }
                    else if (type.equals("Restaurant")){
                        RestaurantInfoLayout.setVisible(true);
                    }
                    else if (type.equals("Shop")){
                        ShopInfoLayout.setVisible(true);
                    }

                } else {
                    ExperienceInfoLayout.setVisible(false);
                    RestaurantInfoLayout.setVisible(false);
                    ShopInfoLayout.setVisible(false);
                }

                toggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {

                    String type = ((RadioButton)toggleGroup.getSelectedToggle()).getText();
                    if (type.equals("Experience")){
                        ExperienceInfoLayout.setVisible(true);
                        RestaurantInfoLayout.setVisible(false);
                        ShopInfoLayout.setVisible(false);
                    }
                    else if (type.equals("Restaurant")){
                        ExperienceInfoLayout.setVisible(false);
                        RestaurantInfoLayout.setVisible(true);
                        ShopInfoLayout.setVisible(false);
                    }
                    else if (type.equals("Shop")){
                        ExperienceInfoLayout.setVisible(false);
                        RestaurantInfoLayout.setVisible(false);
                        ShopInfoLayout.setVisible(true);
                    }
                });
            });

            venueLayout.getChildren().addAll(ExperienceInfoLayout, RestaurantInfoLayout, ShopInfoLayout);
        }

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            long id = idManager.makeId();
            String type = ((RadioButton)toggleGroup.getSelectedToggle()).getText();
            if (nameField.getText().isEmpty() || cityField.getText().isEmpty() || streetField.getText().isEmpty() || streetNumberField.getText().isEmpty()) {
                systemMessage.setText("Please fill all the fields");
            }
            else if (user instanceof Casual && ((Casual) user).getVenueLimit() == 0){
                systemMessage.setText("You have reached your venue limit");
            }
            else {
                if (user instanceof Casual){
                    ((Casual) user).decrementVenueLimit();
                }
                Universal.Address address = new Universal.Address(cityField.getText(), streetField.getText(), Integer.parseInt(streetNumberField.getText()));
                if (descriptionField.getText().isEmpty()){
                    venues.addVenue(address, id, nameField.getText(), type);
                }
                else {
                    venues.addVenue(address, id, nameField.getText(), type, descriptionField.getText());
                }
                if (ownershipCheckBox.isSelected()){
                    var currentVenue = venues.getVenue(type, id);
                    ((PropertyOwner) user).addOwnedVenue(type, id);
                    currentVenue.setOwner(user.getName());
                    currentVenue.setVerified(true);

                    if (currentVenue instanceof Experience){
                        ((Experience) currentVenue).setActivityType(activityTextField.getText());
                        ((Experience) currentVenue).setGroupSizeLimit(Integer.parseInt(groupSizeTextField.getText()));
                    }
                    else if (currentVenue instanceof Restaurant){
                        ((Restaurant) currentVenue).setContact(contactTextField.getText());
                    }
                    else if (currentVenue instanceof Shop){
                        ((Shop) currentVenue).setProductCategory(productCategoryTextField.getText());
                    }
                }
                systemMessage.setText("Venue created and added to the list");
                venueStage.close();
            }
        });

        venueLayout.getChildren().addAll(systemMessage, nameField, cityField, streetField, streetNumberField, radioButtonsLayout, descriptionField, submitButton);
        return venueLayout;
    }

    private void inspectVenueWindow(String venueType, long id) {
        var venue = venues.getVenue(venueType, id);
        Stage venueStage = new Stage();
        VBox venueLayout = new VBox(10);
        venueLayout.setAlignment(Pos.CENTER);

        Label venueTitle = new Label(venue.getName());
        venueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> venueStage.close());

        Label descriptionLabel = new Label(venue.getDescription());

        Button submitReviewButton = new Button("Submit Review");
        submitReviewButton.setOnAction(event -> {
            submitReviewButton.setVisible(false);
            VBox reviewForm = createReviewForm(venue, venueStage);
            venueLayout.getChildren().add(reviewForm);
        });

        // Additional information based on the venue type
        if (venue instanceof Restaurant) {
            Restaurant restaurant = (Restaurant) venue;
            Label contactLabel = new Label(restaurant.getContact() != null ? "Contact: " + restaurant.getContact() : "");
            Label cuisineTypeLabel = new Label(restaurant.getCuisineType() != null ? "Cuisine type: " + restaurant.getCuisineType() : "");

            venueLayout.getChildren().addAll(contactLabel, cuisineTypeLabel);
        } else if (venue instanceof Experience) {
            Experience experience = (Experience) venue;
            Label activityTypeLabel = new Label(experience.getActivityType() != null ? "Activity type: " + experience.getActivityType() : "");
            Label groupSizeLimitLabel = new Label(experience.getGroupSizeLimit() != null ? "Group size limit: " + experience.getGroupSizeLimit().toString() : "");

            venueLayout.getChildren().addAll(activityTypeLabel, groupSizeLimitLabel);
        } else if (venue instanceof Shop) {
            Shop shop = (Shop) venue;
            Label productCategoryLabel = new Label(shop.getProductCategory() != null ? "Product category: " + shop.getProductCategory() : "");

            venueLayout.getChildren().add(productCategoryLabel);
        }

        venueLayout.getChildren().addAll(venueTitle, descriptionLabel, submitReviewButton, closeButton);

        Scene venueScene = new Scene(venueLayout, 400, 300);
        venueStage.setTitle("Venue Details");
        venueStage.setScene(venueScene);
        venueStage.show();
    }

    private VBox createReviewForm(Venue venue, Stage venueStage) {
        VBox reviewLayout = new VBox(10);
        reviewLayout.setAlignment(Pos.CENTER);

        Label systemMessage = new Label();
        systemMessage.setText("Review " + venue.getName());

        Rating rating = new Rating();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            if (user instanceof Traveler) {
                int ratingValue = (int) Math.round(rating.getRating());
                ((Traveler) user).rateVenue(venue, ratingValue);
                systemMessage.setText("Review submitted!");
                venueStage.close();
            }
            else {
                systemMessage.setText("Only travelers can submit reviews");
            }

        });

        reviewLayout.getChildren().addAll(systemMessage, rating, submitButton);
        return reviewLayout;
    }

    private void inspectPathsWindow(){
        Stage pathManagerStage = new Stage();
        VBox pathManagerLayout = new VBox(10);
        pathManagerLayout.setAlignment(Pos.CENTER);

        Button makePathButton = new Button("Make Path");
        makePathButton.setOnAction(event -> {
            Stage makePathStage = new Stage();
            VBox makePathLayout = makePathForm(makePathStage);
            Scene makePathScene = new Scene(makePathLayout, 500, 300);
            makePathStage.setTitle("Make Path");
            makePathStage.setScene(makePathScene);
            makePathStage.show();
        });

        Button viewPathsButton = new Button("View Paths");
        viewPathsButton.setOnAction(event -> {
            Stage pathsStage = new Stage();
            VBox pathsLayout = viewPathsForm();
            Scene pathsScene = new Scene(pathsLayout, 500, 300);
            pathsStage.setTitle("View Paths");
            pathsStage.setScene(pathsScene);
            pathsStage.show();
        });

        Scene pathManagerScene = new Scene(pathManagerLayout, 100, 150);
        pathManagerLayout.getChildren().addAll(makePathButton, viewPathsButton);
        pathManagerStage.setTitle("Path Manager");
        pathManagerStage.setScene(pathManagerScene);
        pathManagerStage.show();
    }

    private VBox makePathForm(Stage makePathStage) {
        VBox makePathLayout = new VBox(10);
        makePathLayout.setAlignment(Pos.CENTER);

        Label systemMessage = new Label();
        systemMessage.setText("Make Path");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        ComboBox<String> venueTypeDropdown = new ComboBox<>(FXCollections.observableArrayList("Restaurant", "Experience", "Shop"));
        venueTypeDropdown.getSelectionModel().selectFirst();

        TextField venueIdField = new TextField();
        venueIdField.setPromptText("ID");
        venueIdField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        HBox venueSelectionLayout = new HBox(10);
        venueSelectionLayout.getChildren().addAll(venueTypeDropdown, venueIdField);

        Button addButton = new Button("Add Venue");

        Stack<Venue> selectedVenues = new Stack<>();

        addButton.setOnAction(event -> {
            String type = venueTypeDropdown.getValue();
            long id = Long.parseLong(venueIdField.getText());
            Venue venue = venues.getVenue(type, id);

            if (venue == null) {
                systemMessage.setText("Error: Venue not found");
            } else {
                selectedVenues.add(venue);
                systemMessage.setText("Venue added to the path");
            }
        });

        Button submitPathButton = new Button("Submit Path");
        submitPathButton.setOnAction(event -> {
            ((Traveler) user).makePath(nameField.getText(), selectedVenues.toArray(new Venue[0]));
            systemMessage.setText("Path created");
            makePathStage.close();
        });

        makePathLayout.getChildren().addAll(systemMessage, nameField, venueSelectionLayout, addButton, submitPathButton);
        return makePathLayout;
    }

    private VBox viewPathsForm() {
        VBox pathsLayout = new VBox(10);
        pathsLayout.setAlignment(Pos.CENTER);

        Label systemMessage = new Label();
        systemMessage.setText("View Paths");

        ComboBox<String> pathsDropdown = new ComboBox<>();
        pathsDropdown.setItems(FXCollections.observableArrayList(((Traveler)user).getPathNames()));

        pathsDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            systemMessage.setText(((Traveler) user).getPath(pathsDropdown.getValue()));
        });


        pathsLayout.getChildren().addAll(systemMessage, pathsDropdown);
        return pathsLayout;
    }

    private VBox createAdminWindow() {
        VBox adminLayout = new VBox(10);
        adminLayout.setAlignment(Pos.CENTER);

        Label systemMessage = new Label();
        systemMessage.setText("Admin Window");

        // Remove user section
        Label removeUserLabel = new Label("Remove user");
        TextField removeUserNameField = new TextField();
        removeUserNameField.setPromptText("Enter username");
        Button removeUserButton = new Button("Remove user");
        removeUserButton.setOnAction(event -> {
            var removedUser = users.getUser(removeUserNameField.getText());
            if (removedUser instanceof PropertyOwner){
                for (String type: new String[]{"Restaurant", "Experience", "Shop"}){
                    for (Long id: ((PropertyOwner) removedUser).getOwnedVenues(type)){
                        venues.removeVenue(type, id);
                    }
                }
            }
            ((Admin) user).removeUser(users, removeUserNameField.getText());
            systemMessage.setText("User removed.");

        });

        // Remove property section
        Label removePropertyLabel = new Label("Remove property");
        Label removePropertyTypeLabel = new Label("Property type: ");
        ComboBox<String> removePropertyTypeDropdown = new ComboBox<>(FXCollections.observableArrayList("Restaurant", "Experience", "Shop"));
        removePropertyTypeDropdown.getSelectionModel().selectFirst();
        TextField removePropertyIdField = new TextField();
        removePropertyIdField.setPromptText("Enter property ID");
        removePropertyIdField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));
        Button removePropertyButton = new Button("Remove property");
        removePropertyButton.setOnAction(event -> {
            var removedPropertyOwner = users.getUser(venues.getVenue(removePropertyTypeDropdown.getValue(), Long.parseLong(removePropertyIdField.getText())).getOwner());
            if (removedPropertyOwner != null){
                ((PropertyOwner) removedPropertyOwner).removeOwnedVenue(removePropertyTypeDropdown.getValue(), Long.parseLong(removePropertyIdField.getText()));
            }
            ((Admin) user).removeProperty(venues, removePropertyTypeDropdown.getValue(), Long.parseLong(removePropertyIdField.getText()));
            systemMessage.setText("Property removed.");
        });

        // Set venue rating section
        Label setVenueRatingLabel = new Label("Set venue rating");
        Label ratePropertyTypeLabel = new Label("Venue type: ");
        ComboBox<String> ratePropertyTypeDropdown = new ComboBox<>(FXCollections.observableArrayList("Restaurant", "Experience", "Shop"));
        ratePropertyTypeDropdown.getSelectionModel().selectFirst();
        TextField setVenueRatingIdField = new TextField();
        setVenueRatingIdField.setPromptText("Enter venue ID");
        TextField setVenueRatingRatingField = new TextField();
        setVenueRatingRatingField.setPromptText("Enter rating");
        Button setVenueRatingButton = new Button("Set rating");
        setVenueRatingButton.setOnAction(event -> {
            ((Admin) user).setVenueRating(venues, ratePropertyTypeDropdown.getValue(), Long.parseLong(setVenueRatingIdField.getText()), Integer.parseInt(setVenueRatingRatingField.getText()));
            systemMessage.setText("Venue rating updated.");
        });

        // View users section
        Label viewUsersLabel = new Label("View users");
        ListView<String> userListView = new ListView<>();
        for (String userName : users.getUsernames()) {
            userListView.getItems().add(userName);
        }

        VBox userInfoLayout = new VBox(10);
        Label userInfoLabel = new Label("User Information");
        TextArea userInfoTextArea = new TextArea();
        userInfoTextArea.setEditable(false);
        userInfoLayout.getChildren().addAll(userInfoLabel, userInfoTextArea);

        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                User selectedUser = users.getUser(newValue);
                String userType = "Unknown";
                String userAttributes = "";

                if (selectedUser instanceof Casual) {
                    userType = "Casual";
                    Casual casualUser = (Casual) selectedUser;
                    userAttributes = "Venue limit: " + casualUser.getVenueLimit();
                } else if (selectedUser instanceof PropertyOwner) {
                    userType = "Property Owner";
                    PropertyOwner propertyOwnerUser = (PropertyOwner) selectedUser;
                    userAttributes = "Owned venues: " + propertyOwnerUser.showOwnedVenues();
                } else if (selectedUser instanceof Professional) {
                    userType = "Professional";
                    userAttributes = "Venues rated: " + ((Professional) selectedUser).getVenuesRated();
                } else if (selectedUser instanceof Admin) {
                    userType = "Admin";
                }

                userInfoTextArea.setText("Name: " + selectedUser.getName() + "\nType: " + userType + "\n" + userAttributes);
            }
        });

        HBox viewUsersLayout = new HBox(10);
        viewUsersLayout.getChildren().addAll(userListView, userInfoLayout);

        adminLayout.getChildren().addAll(systemMessage, removeUserLabel, removeUserNameField, removeUserButton, removePropertyLabel,
                removePropertyTypeLabel, removePropertyTypeDropdown, removePropertyIdField, removePropertyButton, setVenueRatingLabel, ratePropertyTypeLabel, ratePropertyTypeDropdown,
                setVenueRatingIdField, setVenueRatingRatingField, setVenueRatingButton, viewUsersLabel, viewUsersLayout);
        return adminLayout;
    }
}


