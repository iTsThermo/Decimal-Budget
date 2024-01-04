package com.example.javaproject;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;

public class login extends Application {
    private final TextField tfUsername = new TextField();
    private final PasswordField tfPassword = new PasswordField();
    private final PasswordField tfConfirmation = new PasswordField();
    private final Label lblStatus = new Label();
    private final Button btLogin = new Button("Sign in");
    private final String textBox = "#3F3F3F";
    private final String rectangleBox = "#282E33";
    private final String red = "#cc3333";
    private final TextField tfFirstName = new TextField();
    private final TextField tfLastName = new TextField();
    private Statement stmt;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        //Calls the database
        initializeDB();

        lblStatus.setText("");

        //create new grid pane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setHgap(5);
        pane.setVgap(5);
        //center the grid pane
        pane.setAlignment(Pos.CENTER);

        //shadow effect for objects
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        //shadow effect for objects(background)
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0f);
        shadowBg.setOffsetX(5.0f);
        shadowBg.setSpread(.01);
        shadowBg.setColor(Color.BLACK);

        //create new stack pane
        StackPane mainPane = new StackPane();

        //Create linear gradiant background color
        LinearGradient L1 = new LinearGradient(
                0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.99, 0.87, 0.91, 1.0)),
                new Stop(1.0, new Color(0.71, 1.0, 0.99, 1.0)));
        mainPane.setBackground(Background.fill(L1));

        //create new rectangle for background/foreground
        Rectangle bgBox = new Rectangle(450, 650, Color.web(rectangleBox));
        bgBox.setEffect(shadowBg);
        mainPane.getChildren().add(bgBox);

        //insert a logo
        ImageView logo = new ImageView("Logo1.png");
        logo.setTranslateY(-175);
        logo.setEffect(shadow);
        logo.setFitWidth(200);
        logo.setFitHeight(200);
        mainPane.getChildren().add(logo);

        //Create new hyperlink and labels
        Hyperlink hpCreate = new Hyperlink("Don't have an account?");

        Label usernameTxt = new Label("Username:");
        usernameTxt.setFont(Font.font("Daytona", 12));
        usernameTxt.setStyle("-fx-text-fill: white;");

        Label passwordTxt = new Label("Password:");
        passwordTxt.setFont(Font.font("Daytona", 12));
        passwordTxt.setStyle("-fx-text-fill: white;");

        //set shadow effect to objects
        tfUsername.setEffect(shadow);
        tfPassword.setEffect(shadow);
        btLogin.setEffect(shadow);
        hpCreate.setEffect(shadow);

        //set button skin
        btLogin.setSkin(new MyButtonSkin(btLogin));

        //improve the quality of the objects to better suit the project
        tfUsername.setPromptText("Enter your username");
        tfPassword.setPromptText("Enter your password");
        tfUsername.setStyle("-fx-text-fill: white;-fx-background-radius: 15;-fx-background-color: " + textBox + ";");
        tfPassword.setStyle("-fx-text-fill: white;-fx-background-radius: 15;-fx-background-color: " + textBox + ";");
        btLogin.setBackground(Background.fill(Paint.valueOf(textBox)));
        btLogin.setStyle("-fx-text-fill: white;");
        hpCreate.setStyle("-fx-text-fill: white;");
        lblStatus.setStyle("-fx-text-fill: white;");
        tfUsername.setPrefWidth(350);
        tfPassword.setPrefWidth(350);

        //center all the objects
        GridPane.setHalignment(btLogin, HPos.CENTER);
        GridPane.setHalignment(hpCreate, HPos.CENTER);
        GridPane.setHalignment(lblStatus, HPos.CENTER);

        //add objects to grid pane
        pane.add(usernameTxt, 0, 2);
        pane.add(tfUsername, 0, 3);
        pane.add(passwordTxt, 0, 4);
        pane.add(tfPassword, 0, 5);
        Region c1 = new Region();
        c1.setPrefHeight(5);
        pane.add(c1, 0, 6);
        pane.add(btLogin, 0, 7);
        pane.add(hpCreate, 0, 8);
        pane.add(lblStatus, 0, 9);

        //add grid pane to stack pane
        mainPane.getChildren().add(pane);

        //Login button action, verifies if the username and password match and are valid
        btLogin.setOnAction(ex -> {
            try {
                if (verify()) {
                    //gets the accountId
                    String account = getAccount(tfUsername.getText().trim());
                    //opens transactions menu
                    new transactions(stage, account);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        hpCreate.setOnAction(ex -> createAccountMenu(stage));

        Scene scene = new Scene(mainPane, 1280, 720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeDB() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost/javabook", "scott", "tiger");

            System.out.println("Database connected");

            // Create a statement
            stmt = connection.createStatement();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean verify() throws SQLException {
        lblStatus.setText("");
        //stores the username and password
        String typedUsername = tfUsername.getText().trim();
        String typedPassword = tfPassword.getText().trim();
        //query selects the password of the username
        String getPass = "SELECT pass from Accounts where username = '" + typedUsername + "';";
        ResultSet verifyPass = stmt.executeQuery(getPass);
        try {
            while (verifyPass.next()) {
                String password = verifyPass.getString(1);
                //if the password matches return true
                if (typedPassword.equals(password)) {
                    lblStatus.setText("Success!");
                    return true;
                }
            }
        } catch (SQLException e) {
            lblStatus.setText("Error!");
        }
        //return false if the password does not match
        lblStatus.setText("Invalid username or password.");
        return false;
    }

    private void createAccountMenu(Stage stage) {
        lblStatus.setText("");
        //create new stack pane
        StackPane mainPane = new StackPane();
        //set background of stack pane
        LinearGradient L1 = new LinearGradient(
                0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.99, 0.87, 0.91, 1.0)),
                new Stop(1.0, new Color(0.71, 1.0, 0.99, 1.0)));
        mainPane.setBackground(Background.fill(L1));

        //create new grid pane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        //shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);
        //shadow effect
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0f);
        shadowBg.setOffsetX(5.0f);
        shadowBg.setSpread(.01);
        shadowBg.setColor(Color.BLACK);

        //create new rectangle
        Rectangle bgBox = new Rectangle(450, 650, Color.web(rectangleBox));
        bgBox.setEffect(shadowBg);

        //create new header for window
        Label createTxt = new Label("Create an Account:");
        createTxt.setFont(Font.font("Century Gothic", FontWeight.BOLD, 20));
        createTxt.setStyle("-fx-text-fill: white;");
        createTxt.setTranslateY(-25);
        createTxt.setEffect(shadowBg);

        //set shadow effect
        tfFirstName.setEffect(shadow);
        tfLastName.setEffect(shadow);
        tfConfirmation.setEffect(shadow);

        //personalize the text fields
        tfFirstName.setStyle("-fx-text-fill: white;-fx-background-radius: 15;-fx-background-color: " + textBox + ";");
        tfLastName.setStyle("-fx-text-fill: white;-fx-background-radius: 15;-fx-background-color: " + textBox + ";");
        tfConfirmation.setStyle("-fx-text-fill: white;-fx-background-radius: 15;-fx-background-color: " + textBox + ";");
        tfUsername.setPromptText("Enter new username");
        tfPassword.setPromptText("Enter new password");
        tfFirstName.setPromptText("Enter your first name");
        tfLastName.setPromptText("Enter your last name");
        tfConfirmation.setPromptText("Re-enter your password");

        //create new labels for each text field
        Label firstNameTxt = new Label("First Name:");
        firstNameTxt.setFont(Font.font("Daytona", 12));
        firstNameTxt.setStyle("-fx-text-fill: white;");
        Label lastNameTxt = new Label("Last Name:");
        lastNameTxt.setFont(Font.font("Daytona", 12));
        lastNameTxt.setStyle("-fx-text-fill: white;");
        Label usernameTxt = new Label("Username:");
        usernameTxt.setFont(Font.font("Daytona", 12));
        usernameTxt.setStyle("-fx-text-fill: white;");
        Label passwordTxt = new Label("Password:");
        passwordTxt.setFont(Font.font("Daytona", 12));
        passwordTxt.setStyle("-fx-text-fill: white;");
        Label confirmationTxt = new Label("Re-enter password:");
        confirmationTxt.setFont(Font.font("Daytona", 12));
        confirmationTxt.setStyle("-fx-text-fill: white;");

        //create new button for back and create account
        Button btCreate = new Button("Create Account");
        btCreate.setBackground(Background.fill(Color.web(textBox)));
        btCreate.setStyle("-fx-text-fill: white;");
        Button btBack = new Button("Back");
        btBack.setBackground(Background.fill(Paint.valueOf(red)));
        btBack.setStyle("-fx-text-fill: white;");

        //personalize the buttons
        btCreate.setSkin(new MyButtonSkin(btCreate));
        btBack.setSkin(new MyButtonSkin(btBack));
        GridPane.setHalignment(btCreate, HPos.CENTER);
        GridPane.setHalignment(btBack, HPos.CENTER);
        btCreate.setEffect(shadow);
        btBack.setEffect(shadow);

        //add the objects to the grid pane
        pane.add(createTxt, 0, 0);
        pane.add(firstNameTxt, 0, 1);
        pane.add(tfFirstName, 0, 2);
        pane.add(lastNameTxt, 0, 3);
        pane.add(tfLastName, 0, 4);
        pane.add(usernameTxt, 0, 5);
        pane.add(tfUsername, 0, 6);
        pane.add(passwordTxt, 0, 7);
        pane.add(tfPassword, 0, 8);
        pane.add(confirmationTxt, 0, 9);
        pane.add(tfConfirmation, 0, 10);
        //create gap
        Region c1 = new Region();
        c1.setPrefHeight(3);
        pane.add(c1, 0, 11);
        pane.add(btCreate, 0, 12);
        pane.add(btBack, 0, 13);
        pane.add(lblStatus, 0, 14);

        //action call for create button
        btCreate.setOnAction(ex -> {
            try {
                //calls create button
                CreateAccount();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //action call for back button
        btBack.setOnAction(ex -> {
            try {
                //calls start
                start(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //add children the stack pane
        mainPane.getChildren().add(bgBox);
        mainPane.getChildren().add(pane);

        Scene scene = new Scene(mainPane, 1280, 720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }

    private void CreateAccount() throws SQLException {
        lblStatus.setText("");
        //checks if password and username are of proper length
        if (tfPassword.getText().trim().length() < 6 || tfUsername.getText().trim().length() < 3) {
            lblStatus.setText("Password needs to be longer than 6 characters, please try again");
            return;
        }
        //stores username
        String typedUsername = tfUsername.getText().trim();
        //calls for all username in Accounts Category
        String queryString = "SELECT username from Accounts";
        ResultSet getUsername = stmt.executeQuery(queryString);
        try {
            //iterates through usernames to make sure there are no duplicated names
            while (getUsername.next()) {
                String username = getUsername.getString(1);
                //if found return
                if (typedUsername.equals(username)) {
                    lblStatus.setText("Username is already taken, please select another username.");
                    return;
                }
            }
        } catch (SQLException e) {
            lblStatus.setText("Username is available.");
        }
        //Checks if the password entered and the confirmation password match
        if (tfPassword.getText().trim().compareTo(tfConfirmation.getText().trim()) != 0) {
            lblStatus.setText("Passwords do not match, please re-enter your password");
            return;
        }
        //inserts the account information into MySQL
        String insertStmt =
                "INSERT INTO Accounts(username, pass, firstName, lastName) VALUES('"
                        + tfUsername.getText().trim() + "','"
                        + tfPassword.getText().trim() + "','"
                        + tfFirstName.getText().trim() + "','"
                        + tfLastName.getText().trim() + "');";
        try {
            stmt.executeUpdate(insertStmt);
            //display account created
            lblStatus.setText("Account created successfully!");
        } catch (SQLException ex) {
            //display error
            lblStatus.setText("Error! Please try again later.");
        }
    }

    public String getAccount(String username) {
        //selects the account where the username matches
        String accountStmt =
                "SELECT accountId from Accounts where username = '" + username + "';";
        try {
            ResultSet account = stmt.executeQuery(accountStmt);
            account.next();
            //returns the accountId of the username to be passed onto the transactions menu
            return account.getString("accountId");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFirstName(String accountId) {
        initializeDB();
        //selects the firstname where the accountId matches
        String accountStmt =
                "SELECT firstName from Accounts where accountId = '" + accountId + "';";
        try {
            ResultSet account = stmt.executeQuery(accountStmt);
            account.next();
            //returns the firstname of the account
            return account.getString("firstName");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Button Skin with fade in and fade out animation
    public static class MyButtonSkin extends ButtonSkin {
        public MyButtonSkin(Button control) {
            super(control);
            final FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
            fadeIn.setNode(control);
            fadeIn.setToValue(1);
            control.setOnMouseEntered(e -> fadeIn.playFromStart());
            final FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
            fadeOut.setNode(control);
            fadeOut.setToValue(.8);
            control.setOnMouseExited(e -> fadeOut.playFromStart());
            control.setOpacity(.8);
        }
    }
}