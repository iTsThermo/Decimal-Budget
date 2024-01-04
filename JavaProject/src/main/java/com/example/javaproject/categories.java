package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class categories extends login{
    private Statement stmt;
    private final String darkRectangle = "#282E33";
    private final String darkTextBox = "#d1dbe5";
    private final String red = "#cc3333";
    private final String font = "black";
    private final Label lbStatus = new Label();
    private int id;
    public categories(Stage stage, String accountId){
        categoriesMenu(stage, accountId);
    }

    private void categoriesMenu(Stage stage, String accountId) {
        //initialize database
        initializeDB();

        //personalize status label
        lbStatus.setText("");
        lbStatus.setFont(Font.font("Daytona", 12.0));
        lbStatus.setStyle("-fx-text-fill: "+font+";");

        //create stack game
        StackPane mainPane = new StackPane();

        //create new gradiant
        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true,CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.61, 0.93, 0.99, 1.0)),
                new Stop(0.5,new Color( 0.39, 0.78, 0.97, 1.0)),
                new Stop(1.0,new Color( 0.01, 0.33, 0.84, 1.0)));
        //set gradiant to background of stack
        mainPane.setBackground(Background.fill(L1));

        //create new shadow effect for background
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0);
        shadowBg.setOffsetX(5.0);
        shadowBg.setSpread(0.01);
        shadowBg.setColor(Color.BLACK);

        //create new shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);

        //create new label with options as header
        Label optionsTxt = new Label("Category Options:");
        optionsTxt.setFont(Font.font("Century Gothic", FontWeight.BOLD,20));
        optionsTxt.setStyle("-fx-text-fill: white;");
        optionsTxt.setTranslateY(-25);
        optionsTxt.setEffect(shadow);

        //create new buttons
        Button btCreateCategory = new Button("Create Category");
        Button btUpdateCategories = new Button("Update Category");
        Button btDeleteCategory = new Button("Delete Category");
        Button btBack = new Button("Back to transactions");

        //personalize the buttons
        btCreateCategory.setBackground(Background.fill(Paint.valueOf(darkTextBox)));
        btCreateCategory.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;");
        btUpdateCategories.setBackground(Background.fill(Paint.valueOf(darkTextBox)));
        btUpdateCategories.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;");
        btDeleteCategory.setBackground(Background.fill(Paint.valueOf(darkTextBox)));
        btDeleteCategory.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;");
        btBack.setBackground(Background.fill(Paint.valueOf(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");
        btCreateCategory.setSkin(new MyButtonSkin(btCreateCategory));
        btUpdateCategories.setSkin(new MyButtonSkin(btUpdateCategories));
        btDeleteCategory.setSkin(new MyButtonSkin(btDeleteCategory));
        btBack.setSkin(new MyButtonSkin(btBack));
        btDeleteCategory.setPrefWidth(200);
        btDeleteCategory.setPrefHeight(75);
        btCreateCategory.setPrefWidth(200);
        btCreateCategory.setPrefHeight(75);
        btUpdateCategories.setPrefWidth(200);
        btUpdateCategories.setPrefHeight(75);
        btBack.setPrefHeight(30);
        GridPane.setHalignment(btBack, HPos.CENTER);
        btCreateCategory.setEffect(shadow);
        btUpdateCategories.setEffect(shadow);
        btDeleteCategory.setEffect(shadow);
        btBack.setEffect(shadow);

        //create new regions for spacing purposes
        Region content1 = new Region();
        content1.setPrefHeight(25);
        Region content2 = new Region();
        content2.setPrefHeight(25);
        Region content3 = new Region();
        content3.setPrefHeight(15);

        //add buttons to grid pane
        pane.add(optionsTxt, 0 ,0);
        pane.add(btCreateCategory, 0,1);
        pane.add(content1,0,2);
        pane.add(btUpdateCategories,0,3);
        pane.add(content2,0,4);
        pane.add(btDeleteCategory,0,5);
        pane.add(content3,0,6);
        pane.add(btBack,0,7);

        //add pane to main pane
        mainPane.getChildren().add(pane);

        //set action for create category
        btCreateCategory.setOnAction(ex ->{
            //call create category menu
            createCategoryMenu(stage, accountId);
        });

        //set action for update category
        btUpdateCategories.setOnAction(ex -> {
            //call update category menu
            updateCategoryMenu(stage, accountId);
        });

        //set action for delete category
        btDeleteCategory.setOnAction(ex ->{
            //call delete category menu
            deleteCategory(stage,accountId);
        });

        //set action for back button
        btBack.setOnAction(ex ->{
            stage.close();
            try {
                //call transaction menu
                new transactions(stage, accountId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(mainPane, 1280,720);
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
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private void updateCategoryMenu(Stage stage, String accountId){

        StackPane mainPane = new StackPane();

        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true, CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.49, 0.91, 0.93, 1.0)),
                new Stop(1.0,new Color( 0.29, 0.29, 0.89, 1.0)));

        mainPane.setBackground(Background.fill(L1));

        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0);
        shadowBg.setOffsetX(5.0);
        shadowBg.setSpread(0.01);
        shadowBg.setColor(Color.BLACK);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        Rectangle bgBox = new Rectangle(450.0, 650.0, Color.web(darkRectangle));
        bgBox.setEffect(shadowBg);
        mainPane.getChildren().add(bgBox);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5,5,5,5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(Background.fill(L1));

        //create new labels
        Label detailTxt = new Label("Category Details:");
        detailTxt.setFont(Font.font("Century Gothic", FontWeight.BOLD,20));
        detailTxt.setStyle("-fx-text-fill: white;");
        detailTxt.setTranslateY(-5);

        Label categoryNameTxt = new Label("Category name:");
        categoryNameTxt.setFont(Font.font("Daytona", 12.0));
        categoryNameTxt.setStyle("-fx-text-fill: "+font+";");

        Label budgetTxt = new Label("Budget amount($):");
        budgetTxt.setFont(Font.font("Daytona", 12.0));
        budgetTxt.setStyle("-fx-text-fill: "+font+";");

        Label startTxt = new Label("Start date:");
        startTxt.setFont(Font.font("Daytona", 12.0));
        startTxt.setStyle("-fx-text-fill: "+font+";");

        Label endTxt = new Label("End date:");
        endTxt.setFont(Font.font("Daytona", 12.0));
        endTxt.setStyle("-fx-text-fill: "+font+";");

        ComboBox<String> cbCategories = new ComboBox<String>();
        TextField tfCategoryName = new TextField();
        TextField tfStart = new TextField();
        TextField tfEnd = new TextField();
        TextField tfBudget = new TextField();

        //personalize the text fields
        cbCategories.setEffect(shadow);
        cbCategories.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        cbCategories.setPrefWidth(300);
        tfCategoryName.setPromptText("\"Japan Trip, etc\"");
        tfCategoryName.setEffect(shadow);
        tfCategoryName.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfBudget.setPromptText("\"$100.00\"");
        tfBudget.setEffect(shadow);
        tfBudget.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfStart.setPromptText("\"mm/dd/yyyy\"");
        tfStart.setEffect(shadow);
        tfStart.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfEnd.setPromptText("\"mm/dd/yyyy\"");
        tfEnd.setEffect(shadow);
        tfEnd.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");

        //create and personalize the buttons
        Button btUpdate = new Button("Update");
        GridPane.setHalignment(btUpdate, HPos.CENTER);
        btUpdate.setEffect(shadow);
        btUpdate.setBackground(Background.fill(Color.web(darkTextBox)));
        btUpdate.setStyle("-fx-text-fill: "+font+";");
        btUpdate.setSkin(new MyButtonSkin(btUpdate));

        Button btBack = new Button("Back to menu");
        GridPane.setHalignment(btBack, HPos.CENTER);
        btBack.setEffect(shadow);
        btBack.setBackground(Background.fill(Color.web(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");
        btBack.setSkin(new MyButtonSkin(btBack));

        btBack.setOnAction(ex -> {
            //call category menu
            categoriesMenu(stage, accountId);
        });

        //load combobox with category names
        String getAllCategories =
                "SELECT Category.categoryName FROM Category JOIN CategoryIdTable ON CategoryIdTable.categoryId = Category.categoryId WHERE CategoryIdTable.accountId = '"+accountId+"'  ORDER BY Category.categoryName;";
        try {
            ResultSet allCategories = stmt.executeQuery(getAllCategories);
            ObservableList<String> list = FXCollections.observableArrayList();
            while (allCategories.next()) {
                list.add(allCategories.getString("categoryName"));
            }
            cbCategories.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //load text field with category information
        cbCategories.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String getCategoryInfo =
                    "SELECT * FROM category where category.categoryName = '" + cbCategories.getValue().trim() + "';";
            try {
                ResultSet categoryInfo = stmt.executeQuery(getCategoryInfo);
                categoryInfo.next();
                tfCategoryName.setText(categoryInfo.getString("categoryName"));
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                tfBudget.setText(decimalFormat.format(categoryInfo.getFloat("amountSet")));
                tfStart.setText(categoryInfo.getString("startDate"));
                tfEnd.setText(categoryInfo.getString("endDate"));
                id = categoryInfo.getInt("categoryId");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //set action for update
        btUpdate.setOnAction(ex ->{
            //updates the categories
            String categoryUpdate =
                    "Update Category SET categoryName = '"+tfCategoryName.getText().trim()+"', startDate = '"+tfStart.getText().trim()+"', endDate = '"+tfEnd.getText().trim()+"', amountSet = '"+tfBudget.getText().trim()+"' where categoryId = '"+id+"';";
            try{
                lbStatus.setText("Category successfully updated");
                stmt.executeUpdate(categoryUpdate);
            } catch (SQLException e) {
                lbStatus.setText("Category update unsuccessful");
            }
        });

        GridPane.setHalignment(lbStatus,HPos.CENTER);
        //add everything to pane
        pane.add(detailTxt, 0,0);
        pane.add(cbCategories,0,1);
        pane.add(categoryNameTxt,0,2);
        pane.add(tfCategoryName,0,3);
        pane.add(budgetTxt,0,4);
        pane.add(tfBudget,0,5);
        pane.add(startTxt,0,6);
        pane.add(tfStart,0,7);
        pane.add(endTxt,0,8);
        pane.add(tfEnd,0,9);
        pane.add(btUpdate,0,10);
        pane.add(btBack,0,11);
        pane.add(lbStatus,0,12);

        mainPane.getChildren().add(pane);

        Scene scene = new Scene(mainPane, 1280,720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }

    private void deleteCategory(Stage stage, String accountId){
        StackPane mainPane = new StackPane();

        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true, CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.49, 0.91, 0.93, 1.0)),
                new Stop(1.0,new Color( 0.29, 0.29, 0.89, 1.0)));

        mainPane.setBackground(Background.fill(L1));

        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0);
        shadowBg.setOffsetX(5.0);
        shadowBg.setSpread(0.01);
        shadowBg.setColor(Color.BLACK);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        Rectangle bgBox = new Rectangle(450.0, 650.0, Color.web(darkRectangle));
        bgBox.setEffect(shadowBg);
        mainPane.getChildren().add(bgBox);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5,5,5,5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(Background.fill(L1));

        //create new labels
        Label detailTxt = new Label("Category Details:");
        detailTxt.setFont(Font.font("Century Gothic", FontWeight.BOLD,20));
        detailTxt.setStyle("-fx-text-fill: white;");
        detailTxt.setTranslateY(-5);

        Label categoryNameTxt = new Label("Category name:");
        categoryNameTxt.setFont(Font.font("Daytona", 12.0));
        categoryNameTxt.setStyle("-fx-text-fill: "+font+";");

        Label budgetTxt = new Label("Budget amount($):");
        budgetTxt.setFont(Font.font("Daytona", 12.0));
        budgetTxt.setStyle("-fx-text-fill: "+font+";");

        Label startTxt = new Label("Start date:");
        startTxt.setFont(Font.font("Daytona", 12.0));
        startTxt.setStyle("-fx-text-fill: "+font+";");

        Label endTxt = new Label("End date:");
        endTxt.setFont(Font.font("Daytona", 12.0));
        endTxt.setStyle("-fx-text-fill: "+font+";");

        ComboBox<String> cbCategories = new ComboBox<String>();
        TextField tfCategoryName = new TextField();
        TextField tfStart = new TextField();
        TextField tfEnd = new TextField();
        TextField tfBudget = new TextField();

        //personalize the text fields and buttons
        cbCategories.setBackground(Background.fill(Color.web(darkTextBox)));
        cbCategories.setEffect(shadow);
        cbCategories.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        cbCategories.setPrefWidth(300);
        tfCategoryName.setPromptText("\"Japan Trip\", etc");
        tfCategoryName.setEffect(shadow);
        tfCategoryName.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfBudget.setPromptText("\"$100.00\"");
        tfBudget.setEffect(shadow);
        tfBudget.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfStart.setPromptText("\"mm/dd/yyyy\"");
        tfStart.setEffect(shadow);
        tfStart.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfEnd.setPromptText("\"mm/dd/yyyy\"");
        tfEnd.setEffect(shadow);
        tfEnd.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");

        //create caution text fields
        Label cautionTxt = new Label("Warning: Deleting a category will permanently erase");
        cautionTxt.setFont(Font.font("Daytona", 12.0));
        cautionTxt.setStyle("-fx-text-fill: "+font+";");
        GridPane.setHalignment(cautionTxt, HPos.CENTER);

        Label cautionTxt2 = new Label("all associated transactions within that category");
        cautionTxt2.setFont(Font.font("Daytona", 12.0));
        cautionTxt2.setStyle("-fx-text-fill: "+font+";");
        GridPane.setHalignment(cautionTxt2, HPos.CENTER);

        //create buttons for delete and back
        Button btDelete = new Button("Delete");
        GridPane.setHalignment(btDelete, HPos.CENTER);
        btDelete.setEffect(shadow);
        btDelete.setBackground(Background.fill(Color.web(darkTextBox)));
        btDelete.setStyle("-fx-text-fill: "+font+";");
        btDelete.setSkin(new MyButtonSkin(btDelete));


        Button btBack = new Button("Back to menu");
        GridPane.setHalignment(btBack, HPos.CENTER);
        btBack.setEffect(shadow);
        btBack.setBackground(Background.fill(Color.web(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");
        btBack.setSkin(new MyButtonSkin(btBack));

        //set action for back
        btBack.setOnAction(ex -> {
            //call category menu
            categoriesMenu(stage, accountId);
        });

        //load combobox with category names
        String getAllCategories =
                "SELECT Category.categoryName FROM Category JOIN CategoryIdTable ON CategoryIdTable.categoryId = Category.categoryId WHERE CategoryIdTable.accountId = '"+accountId+"'  ORDER BY Category.categoryName;";
        try {
            ResultSet allCategories = stmt.executeQuery(getAllCategories);
            ObservableList<String> list = FXCollections.observableArrayList();
            while (allCategories.next()) {
                list.add(allCategories.getString("categoryName"));
            }
            cbCategories.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //load text fields with information of selected category
        cbCategories.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String getCategoryInfo =
                    "SELECT * FROM category where category.categoryName = '" + cbCategories.getValue().trim() + "';";
            try {
                ResultSet categoryInfo = stmt.executeQuery(getCategoryInfo);
                categoryInfo.next();
                tfCategoryName.setText(categoryInfo.getString("categoryName"));
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                tfBudget.setText(decimalFormat.format(categoryInfo.getFloat("amountSet")));
                tfStart.setText(categoryInfo.getString("startDate"));
                tfEnd.setText(categoryInfo.getString("endDate"));
                id = categoryInfo.getInt("categoryId");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //add children to the pane
        pane.add(detailTxt, 0,0);
        pane.add(cbCategories,0,1);
        pane.add(categoryNameTxt,0,2);
        pane.add(tfCategoryName,0,3);
        pane.add(budgetTxt,0,4);
        pane.add(tfBudget,0,5);
        pane.add(startTxt,0,6);
        pane.add(tfStart,0,7);
        pane.add(endTxt,0,8);
        pane.add(tfEnd,0,9);
        pane.add(cautionTxt,0,10);
        pane.add(cautionTxt2,0,11);
        pane.add(btDelete,0,12);
        pane.add(btBack,0,13);
        pane.add(lbStatus,0,14);

        mainPane.getChildren().add(pane);

        GridPane.setHalignment(lbStatus, HPos.CENTER);
        //set action for delete button
        btDelete.setOnAction(ex ->{
            //checks if category is selected
            if (cbCategories.getValue() != null) {
                delete(id);
                lbStatus.setText("Successfully deleted.");
            }
            else{
                lbStatus.setText("Please select a category.");
            }
        });

        Scene scene = new Scene(mainPane, 1280,720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }

    private void delete(int categoryId){
        //deletes all transactions with the category
        String transactionDeletion = "DELETE FROM Transactions Where Transactions.categoryId = '"+categoryId+"';";
        try{
            stmt.executeUpdate(transactionDeletion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //deletes the category information
        String categoryDelete = "DELETE FROM Category Where Category.categoryId = '"+categoryId+"';";
        try{
            stmt.executeUpdate(categoryDelete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //deletes the bridge for the category
        String categoryDelete2 = "DELETE FROM CategoryIdTable Where CategoryIdTable.categoryId = '"+categoryId+"';";
        try{
            stmt.executeUpdate(categoryDelete2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void createCategoryMenu(Stage stage, String accountId){

        StackPane mainPane = new StackPane();

        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true, CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.49, 0.91, 0.93, 1.0)),
                new Stop(1.0,new Color( 0.29, 0.29, 0.89, 1.0)));

        mainPane.setBackground(Background.fill(L1));

        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0);
        shadowBg.setOffsetX(5.0);
        shadowBg.setSpread(0.01);
        shadowBg.setColor(Color.BLACK);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        Rectangle bgBox = new Rectangle(450.0, 650.0, Color.web(darkRectangle));
        bgBox.setEffect(shadowBg);
        mainPane.getChildren().add(bgBox);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5,5,5,5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(Background.fill(L1));

        //create new labels
        Label createTxt = new Label("New Category:");
        createTxt.setFont(Font.font("Century Gothic", FontWeight.BOLD,20));
        createTxt.setStyle("-fx-text-fill: white;");
        createTxt.setTranslateY(-5);

        Label categoryNameTxt = new Label("Category name:");
        categoryNameTxt.setFont(Font.font("Daytona", 12.0));
        categoryNameTxt.setStyle("-fx-text-fill: "+font+";");

        Label budgetTxt = new Label("Budget amount($):");
        budgetTxt.setFont(Font.font("Daytona", 12.0));
        budgetTxt.setStyle("-fx-text-fill: "+font+";");

        Label startTxt = new Label("Start date:");
        startTxt.setFont(Font.font("Daytona", 12.0));
        startTxt.setStyle("-fx-text-fill: "+font+";");

        Label endTxt = new Label("End date:");
        endTxt.setFont(Font.font("Daytona", 12.0));
        endTxt.setStyle("-fx-text-fill: "+font+";");

        TextField tfCategoryName = new TextField();
        TextField tfStart = new TextField();
        TextField tfEnd = new TextField();
        TextField tfBudget = new TextField();

        //personalize text fields
        tfCategoryName.setPrefWidth(250);
        tfBudget.setPrefWidth(250);
        tfEnd.setPrefWidth(250);
        tfStart.setPrefWidth(250);

        //personalize the text fields
        tfCategoryName.setBackground(Background.fill(Color.web(darkTextBox)));
        tfCategoryName.setPromptText("\"groceries, trip, etc.\"");
        tfCategoryName.setEffect(shadow);
        tfCategoryName.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfBudget.setPromptText("\"$100.00\"");
        tfBudget.setEffect(shadow);
        tfBudget.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfStart.setPromptText("\"mm/dd/yyyy\"");
        tfStart.setEffect(shadow);
        tfStart.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");
        tfEnd.setPromptText("\"mm/dd/yyyy\"");
        tfEnd.setEffect(shadow);
        tfEnd.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+darkTextBox+";");

        //create buttons and personalize them
        Button btCreate = new Button("Create Category");
        GridPane.setHalignment(btCreate, HPos.CENTER);
        btCreate.setEffect(shadow);
        btCreate.setBackground(Background.fill(Color.web(darkTextBox)));
        btCreate.setStyle("-fx-text-fill: "+font+";");
        btCreate.setSkin(new MyButtonSkin(btCreate));

        Button btBack = new Button("Back to menu");
        GridPane.setHalignment(btBack, HPos.CENTER);
        btBack.setEffect(shadow);
        btBack.setBackground(Background.fill(Color.web(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");
        btBack.setSkin(new MyButtonSkin(btBack));

        //set action for back button
        btBack.setOnAction(ex -> {
            //calls category menu
            categoriesMenu(stage, accountId);
        });

        //sets start date to current
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String date = dateObj.format(formatter);
        tfStart.setText(date);

        //add all objects to the pane
        pane.add(createTxt, 0,0);
        pane.add(categoryNameTxt,0,1);
        pane.add(tfCategoryName,0,2);
        pane.add(budgetTxt,0,3);
        pane.add(tfBudget,0,4);
        pane.add(startTxt,0,5);
        pane.add(tfStart,0,6);
        pane.add(endTxt,0,7);
        pane.add(tfEnd,0,8);
        pane.add(btCreate,0,9);
        pane.add(btBack,0,10);
        pane.add(lbStatus,0,11);

        mainPane.getChildren().add(pane);

        GridPane.setHalignment(lbStatus, HPos.CENTER);

        btCreate.setOnAction(ex -> {
            //checks if the date has correct formatting
            if (tfStart.getText().trim().length() != 10 || tfEnd.getText().trim().length() != 10) {
                lbStatus.setText("WARNING: Incorrect date format");
            } else {
                try {
                    //formats the dates
                    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate startDate = LocalDate.parse(tfStart.getText().trim(), formatterDate);
                    LocalDate endDate = LocalDate.parse(tfEnd.getText().trim(), formatterDate);
                    //create new categoryId with the account
                    String newCategoryId =
                            "INSERT INTO CategoryIdTable(accountId) Values ('" + accountId + "');";
                    stmt.execute(newCategoryId);
                    //creates new category with all information from text fields
                    String newCategory =
                            "INSERT INTO Category(categoryName, startDate, endDate, amountSet) VALUES ('" +
                                    tfCategoryName.getText().trim() + "','" +
                                    startDate + "','" +
                                    endDate + "','" +
                                    Float.parseFloat(tfBudget.getText().trim()) + "');";
                    lbStatus.setText("Category successfully create");
                    stmt.execute(newCategory);
                } catch (Exception e) {
                    lbStatus.setText("WARNING: An error occurred");
                }
            }
        });
        Scene scene = new Scene(mainPane, 1280,720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }
}
