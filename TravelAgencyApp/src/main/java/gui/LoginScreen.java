package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dataStorage.UserMap;

public class LoginScreen extends Application {
    Label systemMessage;
    UserMap users;

    @Override
    public void start(Stage primaryStage) {
        /** GUI for logging in and registering */
        users = new UserMap();
        // usage of multiThreading
        new Thread(() -> {
            users.load("src/main/resources/users"); // make sure to have valid starting json file
            System.out.println("Users loaded");
        }).start();

        primaryStage.setOnCloseRequest(event -> {
            new Thread(() -> {
                users.upload("src/main/resources/users");
                System.out.println("Users uploaded");
            }).start();
            Platform.exit();
        });

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        HBox buttonsLayout = new HBox(30);
        buttonsLayout.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Log In");
        Button registerButton = new Button("Register");

        buttonsLayout.getChildren().addAll(loginButton, registerButton);
        mainLayout.getChildren().add(buttonsLayout);

        systemMessage = new Label();
        systemMessage.setText("Welcome to Traveler!");
        mainLayout.getChildren().add(systemMessage);

        VBox loginLayout = createLoginForm(users, primaryStage);
        VBox registrationLayout = createRegistrationForm(users);

        mainLayout.getChildren().addAll(loginLayout, registrationLayout);

        loginButton.setOnAction(event -> {
            loginLayout.setVisible(true);
            registrationLayout.setVisible(false);
        });

        registerButton.setOnAction(event -> {
            loginLayout.setVisible(false);
            registrationLayout.setVisible(true);
        });
        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setTitle("Start here");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public VBox createLoginForm(UserMap users, Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setSpacing(25);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button submitLoginButton = new Button("Submit");

        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> submitLoginButton.fire());

        submitLoginButton.setOnAction(event -> {
            var newUser = users.login(usernameField.getText(), passwordField.getText());
            usernameField.clear();
            passwordField.clear();
            if (newUser != null) {
                systemMessage.setText("Login successful");
                startApp(newUser);
                primaryStage.close();
            } else {
                systemMessage.setText("Login failed");
            }
        });

        loginLayout.getChildren().addAll(usernameField, passwordField, submitLoginButton);
        loginLayout.setVisible(false);

        return loginLayout;
    }

    public VBox createRegistrationForm(UserMap users) {
        VBox registrationLayout = new VBox();
        registrationLayout.setAlignment(Pos.CENTER);
        registrationLayout.setSpacing(25);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.maxWidth(50);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.maxWidth(50);

        RadioButton radioButton1 = new RadioButton("Casual");
        RadioButton radioButton2 = new RadioButton("Professional");
        RadioButton radioButton3 = new RadioButton("Property Owner");
        RadioButton radioButton4 = new RadioButton("Admin");

        radioButton1.setSelected(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton3.setToggleGroup(toggleGroup);
        radioButton4.setToggleGroup(toggleGroup);

        toggleGroup.getSelectedToggle();

        HBox radioButtonsLayout = new HBox(10);
        radioButtonsLayout.setAlignment(Pos.BOTTOM_CENTER);
        radioButtonsLayout.getChildren().addAll(radioButton1, radioButton2, radioButton3, radioButton4);

        Button submitRegisterButton = new Button("Submit");

        usernameField.requestFocus();
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> submitRegisterButton.fire());

        submitRegisterButton.setOnAction(event -> {
            if (!users.register(usernameField.getText(),
                    passwordField.getText(),
                    ((RadioButton)toggleGroup.getSelectedToggle()).getText()))
            {systemMessage.setText("Registration unsuccessful");}
            else {systemMessage.setText("Registration successful");}
            usernameField.clear();
            passwordField.clear();
        });

        registrationLayout.getChildren().addAll(usernameField, passwordField, radioButtonsLayout, submitRegisterButton);
        registrationLayout.setVisible(false);

        return registrationLayout;
    }

    private void startApp(users.User user) {
        Platform.runLater(() -> {
            AppScreen appScreen = new AppScreen(user, users);
            appScreen.start(new Stage());
        });
    }
}