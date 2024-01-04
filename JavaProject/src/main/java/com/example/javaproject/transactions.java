package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class transactions extends login{
    private Statement stmt;
    private Statement stmt2;
    private float currentAmount;
    private final TextField tfEntryDate = new TextField();
    private final TextField tfAmount = new TextField();
    private final TextField tfTransactionDesc = new TextField();
    private final TextField tfCategory = new TextField();
    private final Button btInsert = new Button("Add Transaction");
    private final Button btLogout = new Button("Sign Out");
    private final Button btInfo = new Button("Spending Chart");
    private final Button btEdit = new Button("Update Transaction");
    private final Button btCategory = new Button("Category Menu");
    private final Button btRefresh = new Button("Refresh Page");
    private final String textBox = "#d1dbe5";
    private final String lightGrey = "#bec9ca";
    private final String grey = "#e0e5e6";
    private final String rectangleBox = "#282E33";
    private final String largeTxt = "#f9feff";
    private final String font = "black";
    private final String red = "#cc3333";
    private final ComboBox<String> cbCategories = new ComboBox<>();
    private Label transactionCategoryTxt = new Label();
    private final Button btNext = new Button("Next Page");
    private final Button btPrev = new Button("Prev Page");
    private int currentPage = 0;
    private final Label lbStatus = new Label();
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    public transactions(Stage stage, String accountId) throws SQLException {
        transactionMenu(stage, accountId, currentPage);
    }
    private void transactionMenu(Stage stage, String accountId, int page_number) throws SQLException {

        lbStatus.setText("");
        //call database
        initializeDB();

        //create new stack pane and set background to a gradiant color
        StackPane stack = new StackPane();
        LinearGradient L2 = new LinearGradient(
                0.0,0.0,1.0,0.0,true,CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.61, 0.93, 0.99, 1.0)),
                new Stop(0.5,new Color( 0.39, 0.78, 0.97, 1.0)),
                new Stop(1.0,new Color( 0.01, 0.33, 0.84, 1.0)));
        stack.setBackground(Background.fill(L2));

        //create new grid pane
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPadding(new Insets(10, 10, 10, 10));

        //create new shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(0.0f);
        shadow.setOffsetX(0.0f);
        shadow.setColor(Color.BLACK);

        //create new shadow effect for background
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0f);
        shadowBg.setOffsetX(5.0f);
        shadowBg.setSpread(.01);
        shadowBg.setColor(Color.BLACK);

        //personalize the buttons with effects
        btInsert.setEffect(shadow);
        btEdit.setEffect(shadow);
        btLogout.setEffect(shadow);
        btCategory.setEffect(shadow);
        btRefresh.setEffect(shadow);
        btNext.setEffect(shadow);
        btPrev.setEffect(shadow);
        btInfo.setEffect(shadow);
        btInsert.setSkin(new MyButtonSkin(btInsert));
        btEdit.setSkin(new MyButtonSkin(btEdit));
        btNext.setSkin(new MyButtonSkin(btNext));
        btPrev.setSkin(new MyButtonSkin(btPrev));
        btRefresh.setSkin(new MyButtonSkin(btRefresh));
        btCategory.setSkin(new MyButtonSkin(btCategory));
        btLogout.setSkin(new MyButtonSkin(btLogout));
        btInfo.setSkin(new MyButtonSkin(btInfo));
        //create new rectangle for the foreground
        Rectangle bgBox = new Rectangle(0, 0, Color.web(rectangleBox));
        bgBox.setEffect(shadowBg);
        bgBox.heightProperty().bind(stage.heightProperty().divide(1.5));
        bgBox.widthProperty().bind(stage.widthProperty().divide(1.3));
        bgBox.translateXProperty().bind(stage.widthProperty().multiply(.085));
        bgBox.translateYProperty().bind(stage.heightProperty().multiply(-.01));
        stack.getChildren().add(bgBox);
        bgBox.setArcHeight(25);
        bgBox.setArcWidth(25);

        //create new rectangle for the foreground
        Rectangle toolBoxBg = new Rectangle(0, 0, Color.web(rectangleBox));
        toolBoxBg.setEffect(shadowBg);
        toolBoxBg.heightProperty().bind(stage.heightProperty().divide(1.5));
        toolBoxBg.widthProperty().bind(stage.widthProperty().divide(6));
        toolBoxBg.translateXProperty().bind(stage.widthProperty().multiply(-.39));
        toolBoxBg.translateYProperty().bind(stage.heightProperty().multiply(-.01));
        toolBoxBg.setArcHeight(25);
        toolBoxBg.setArcWidth(25);

        stack.getChildren().add(toolBoxBg);

        //create new rectangle for the foreground
        Rectangle progressbarBg = new Rectangle(1080, 720, Color.web(rectangleBox));
        progressbarBg.setEffect(shadowBg);
        progressbarBg.heightProperty().bind(stage.heightProperty().divide(9));
        progressbarBg.widthProperty().bind(stage.widthProperty().divide(1.06));
        progressbarBg.translateXProperty().bind(stage.widthProperty().multiply(0));
        progressbarBg.translateYProperty().bind(stage.heightProperty().multiply(.4));
        stack.getChildren().add(progressbarBg);
        progressbarBg.setArcHeight(25);
        progressbarBg.setArcWidth(25);

        //set the alignment for the buttons to center
        GridPane.setHalignment(btInsert, HPos.CENTER);
        GridPane.setHalignment(btEdit, HPos.CENTER);
        GridPane.setHalignment(btLogout, HPos.CENTER);
        GridPane.setHalignment(btCategory, HPos.CENTER);
        GridPane.setHalignment(btRefresh, HPos.CENTER);
        GridPane.setHalignment(btNext, HPos.CENTER);
        GridPane.setHalignment(btPrev, HPos.CENTER);
        GridPane.setHalignment(btInfo, HPos.CENTER);

        //set the size of the buttons to be bound by the stage
        btInsert.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btLogout.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btEdit.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btCategory.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btRefresh.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btNext.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btPrev.prefWidthProperty().bind(stage.widthProperty().divide(10));
        btInfo.prefWidthProperty().bind(stage.widthProperty().divide(10));
        //set the size of the buttons to be bound by the stage
        btInsert.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btLogout.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btEdit.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btCategory.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btRefresh.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btNext.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btPrev.prefHeightProperty().bind(stage.heightProperty().divide(20));
        btInfo.prefHeightProperty().bind(stage.heightProperty().divide(20));

        //add the buttons to grid pane
        pane.add(btInsert, 0, 0);
        pane.add(btCategory, 0, 2);
        pane.add(btInfo,0,4);
        pane.add(btNext,0,6);
        pane.add(btPrev,0,8);
        pane.add(btRefresh,0,10);
        //create gap between buttons
        Region gap2 = new Region();
        gap2.setPrefHeight(100);
        pane.add(gap2,0,12);
        pane.add(btLogout,0,14);

        //action for add transaction
        btInsert.setOnAction(ex ->{
            stage.close();
            addTransactionMenu(new Stage(), accountId);
        });

        //action to call categories menu
        btCategory.setOnAction(ex -> {
            stage.close();
            new categories(new Stage(), accountId);
        });

        //action to return to start menu
        btLogout.setOnAction(ex ->{
            stage.close();
            try {
                start(new Stage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //action for next page
        btNext.setOnAction(ex -> {
            try {
                next(stage, accountId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //action for prev page
        btPrev.setOnAction(ex ->{
            try {
                prev(stage, accountId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //action to refresh the page
        btRefresh.setOnAction(ex -> {
            try {
                new transactions(stage, accountId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        btInfo.setOnAction(ex -> {
            try{
                informationMenu(stage, accountId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        //personalize the buttons
        btEdit.setBackground(Background.fill(Color.web(textBox)));
        btEdit.setStyle("-fx-text-fill: "+font+";");
        btLogout.setBackground(Background.fill(Color.web(red)));
        btLogout.setStyle("-fx-text-fill: "+font+";");
        btInsert.setBackground(Background.fill(Color.web(textBox)));
        btInsert.setStyle("-fx-text-fill: "+font+";");
        btCategory.setBackground(Background.fill(Color.web(textBox)));
        btCategory.setStyle("-fx-text-fill: "+font+";");
        btRefresh.setBackground(Background.fill(Color.web(textBox)));
        btRefresh.setStyle("-fx-text-fill: "+font+";");
        btNext.setBackground(Background.fill(Color.web(textBox)));
        btNext.setStyle("-fx-text-fill: "+font+";");
        btPrev.setBackground(Background.fill(Color.web(textBox)));
        btPrev.setStyle("-fx-text-fill: "+font+";");
        btInfo.setBackground(Background.fill(Color.web(textBox)));
        btInfo.setStyle("-fx-text-fill: "+font+";");

        //create new grid panes for transactions/header
        GridPane headerGrid = new GridPane();
        GridPane headerGridBackground = new GridPane();

        //Move the header file to the correct position
        headerGridBackground.translateXProperty().bind(stage.widthProperty().multiply(.2045));
        headerGridBackground.translateYProperty().bind(stage.heightProperty().multiply(.16));

        //create Region to create a gap between grid pane
        Region content1 = new Region();
        content1.prefWidthProperty().bind(stage.widthProperty().multiply(.21));

        //move the height of the grid pane
        headerGrid.translateYProperty().bind(stage.heightProperty().multiply(.1675));

        //move the position of the pane
        pane.translateXProperty().bind(stage.widthProperty().multiply(.05));
        pane.translateYProperty().bind(stage.heightProperty().multiply(.175));

        //Create rectangles, position them, and add to header background
        Rectangle dateHeaderBox = new Rectangle(0, 0, Color.web(lightGrey));
        dateHeaderBox.heightProperty().bind(stage.heightProperty().divide(25));
        dateHeaderBox.widthProperty().bind(stage.widthProperty().divide(8));
        headerGridBackground.add(dateHeaderBox,0,0);

        //Create rectangles, position them, and add to header background
        Rectangle idHeaderBox = new Rectangle(0, 0, Color.web(grey));
        idHeaderBox.heightProperty().bind(stage.heightProperty().divide(25));
        idHeaderBox.widthProperty().bind(stage.widthProperty().divide(12));
        headerGridBackground.add(idHeaderBox,1, 0);

        //Create rectangles, position them, and add to header background
        Rectangle amountHeaderBox = new Rectangle(0, 0, Color.web(lightGrey));
        amountHeaderBox.heightProperty().bind(stage.heightProperty().divide(25));
        amountHeaderBox.widthProperty().bind(stage.widthProperty().divide(8));
        headerGridBackground.add(amountHeaderBox,2,0);

        //Create rectangles, position them, and add to header background
        Rectangle categoryHeaderBox = new Rectangle(0, 0, Color.web(grey));
        categoryHeaderBox.heightProperty().bind(stage.heightProperty().divide(25));
        categoryHeaderBox.widthProperty().bind(stage.widthProperty().divide(8));
        headerGridBackground.add(categoryHeaderBox,3,0);

        //Create rectangles, position them, and add to header background
        Rectangle descriptionHeaderBox = new Rectangle(0, 0, Color.web(lightGrey));
        descriptionHeaderBox.heightProperty().bind(stage.heightProperty().divide(25));
        descriptionHeaderBox.widthProperty().bind(stage.widthProperty().divide(3.4));
        headerGridBackground.add(descriptionHeaderBox,4, 0);

        //Create new grid pane
        GridPane topPane = new GridPane();
        //create header label and personalize it
        Label welcome = new Label("WELCOME " + getFirstName(accountId).toUpperCase() + "!");
        welcome.setFont(Font.font("Century Gothic", FontWeight.BOLD,45));
        welcome.setTextFill(Color.WHITESMOKE);
        welcome.translateXProperty().bind(stage.widthProperty().multiply(.02));
        welcome.translateYProperty().bind(stage.heightProperty().multiply(.0315));
        welcome.setAlignment(Pos.TOP_LEFT);
        welcome.setEffect(shadow);
        topPane.add(welcome,0,0);

        //Create labels for the transactions
        Label dateTxt = new Label("Date");
        dateTxt.setFont(Font.font("Daytona", 14));
        dateTxt.setStyle("-fx-text-fill: "+ font +";");
        dateTxt.prefWidthProperty().bind(stage.widthProperty().multiply(0.128));
        GridPane.setValignment(dateTxt, VPos.TOP);
        dateTxt.setAlignment(Pos.CENTER);

        //Create labels for the transactions
        Label idTxt = new Label("ID");
        idTxt.setFont(Font.font("Daytona", 14));
        idTxt.setStyle("-fx-text-fill: "+ font +";");
        idTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.088));
        GridPane.setValignment(idTxt, VPos.TOP);
        idTxt.setAlignment(Pos.CENTER);

        //Create labels for the transactions
        Label amountTxt = new Label("Amount");
        amountTxt.setFont(Font.font("Daytona", 14));
        amountTxt.setStyle("-fx-text-fill: "+ font +";");
        amountTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.128));
        GridPane.setValignment(amountTxt, VPos.TOP);
        amountTxt.setAlignment(Pos.CENTER);

        //Create labels for the transactions
        Label categoryTxt = new Label("Category");
        categoryTxt.setFont(Font.font("Daytona", 14));
        categoryTxt.setStyle("-fx-text-fill: "+ font +";");
        categoryTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.129));
        GridPane.setValignment(categoryTxt, VPos.TOP);
        categoryTxt.setAlignment(Pos.CENTER);

        //Create labels for the transactions
        Label descriptionTxt = new Label("Description");
        descriptionTxt.setFont(Font.font("Daytona", 14));
        descriptionTxt.setStyle("-fx-text-fill: "+ font +";");
        descriptionTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.298));
        GridPane.setValignment(descriptionTxt, VPos.TOP);
        descriptionTxt.setAlignment(Pos.CENTER);

        //Create region for spacing purposes
        Region content2 = new Region();
        content2.prefWidthProperty().bind(stage.widthProperty().multiply(.036));

        //add labels to the grid pane
        headerGrid.add(content1, 0, 0);
        headerGrid.add(dateTxt, 1, 0);
        headerGrid.add(idTxt,2,0);
        headerGrid.add(amountTxt,3,0);
        headerGrid.add(categoryTxt,4,0);
        headerGrid.add(descriptionTxt,5,0);
        headerGrid.add(content2, 6, 0);

        //add grid pane to stack pane
        stack.getChildren().add(headerGridBackground);
        stack.getChildren().add(headerGrid);

        //selects all transactions where accountId is equal to transaction account ID
        String transactionsQuery = "SELECT * FROM Transactions WHERE accountId = '"+accountId+"' ORDER" +
                " BY transactionId DESC LIMIT "+page_number+", 14;";
        ResultSet allTransactionInfo;
        try{
            allTransactionInfo = stmt.executeQuery(transactionsQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //create a blur effect
        BoxBlur bb = new BoxBlur();
        bb.setWidth(5);
        bb.setHeight(5);
        bb.setIterations(3);

        //create transaction background pane
        GridPane transactionPane = new GridPane();

        //create transaction txt pane
        GridPane transactionTxtPane = new GridPane();

        //position and set V gap for the transaction txt pane
        transactionTxtPane.setVgap(14);
        transactionTxtPane.translateYProperty().bind(stage.heightProperty().multiply(.21));

        //position the transactions background pane
        transactionPane.translateXProperty().bind(stage.widthProperty().multiply(.2045));
        transactionPane.translateYProperty().bind(stage.heightProperty().multiply(.2));

        //initialize index
        int index = 0;
        //create while statement to iterate through result Set
        while (allTransactionInfo.next()){
            //creates alternating background colors (light and dark) for visibility
            if (index % 2 == 0) {
                //Create the background boxes for transactions labels, position and set shadow effect
                Rectangle transactionDateBg = new Rectangle(0, 0, Color.web(lightGrey));
                transactionDateBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionDateBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionDateBg.setEffect(bb);

                Rectangle transactionIdBg = new Rectangle(0, 0, Color.web(lightGrey));
                transactionIdBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionIdBg.widthProperty().bind(stage.widthProperty().divide(12));
                transactionIdBg.setEffect(bb);

                Rectangle transactionAmountBg = new Rectangle(0, 0, Color.web(lightGrey));
                transactionAmountBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionAmountBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionAmountBg.setEffect(bb);

                Rectangle transactionCategoryBg = new Rectangle(0, 0, Color.web(lightGrey));
                transactionCategoryBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionCategoryBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionCategoryBg.setEffect(bb);

                Rectangle transactionDescriptionBg = new Rectangle(0, 0, Color.web(lightGrey));
                transactionDescriptionBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionDescriptionBg.widthProperty().bind(stage.widthProperty().divide(3.4));
                transactionDescriptionBg.setEffect(bb);

                transactionPane.add(transactionDateBg, 0,index);
                transactionPane.add(transactionIdBg, 1,index);
                transactionPane.add(transactionAmountBg, 2,index);
                transactionPane.add(transactionCategoryBg, 3,index);
                transactionPane.add(transactionDescriptionBg, 4,index);
            }
            else{
                //Create the background boxes for transactions labels, position and set shadow effect
                Rectangle transactionDateBg = new Rectangle(0, 0, Color.web(grey));
                transactionDateBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionDateBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionDateBg.setEffect(bb);

                Rectangle transactionIdBg = new Rectangle(0, 0, Color.web(grey));
                transactionIdBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionIdBg.widthProperty().bind(stage.widthProperty().divide(12));
                transactionIdBg.setEffect(bb);

                Rectangle transactionAmountBg = new Rectangle(0, 0, Color.web(grey));
                transactionAmountBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionAmountBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionAmountBg.setEffect(bb);

                Rectangle transactionCategoryBg = new Rectangle(0, 0, Color.web(grey));
                transactionCategoryBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionCategoryBg.widthProperty().bind(stage.widthProperty().divide(8));
                transactionCategoryBg.setEffect(bb);

                Rectangle transactionDescriptionBg = new Rectangle(0, 0, Color.web(grey));
                transactionDescriptionBg.heightProperty().bind(stage.heightProperty().divide(25));
                transactionDescriptionBg.widthProperty().bind(stage.widthProperty().divide(3.4));
                transactionDescriptionBg.setEffect(bb);

                transactionPane.add(transactionDateBg, 0,index);
                transactionPane.add(transactionIdBg, 1,index);
                transactionPane.add(transactionAmountBg, 2,index);
                transactionPane.add(transactionCategoryBg, 3,index);
                transactionPane.add(transactionDescriptionBg, 4,index);
            }
            //create new blank regions for spacing purposes
            Region blankSpace = new Region();
            blankSpace.prefWidthProperty().bind(stage.widthProperty().multiply(.21));
            Region blankSpace1 = new Region();
            blankSpace1.prefWidthProperty().bind(stage.widthProperty().multiply(.036));

            //Creates new Labels that will insert the transactions into, personalize and set positions
            Label transactionDateTxt = new Label(allTransactionInfo.getString("entryDate"));
            transactionDateTxt.setFont(Font.font("Daytona", 12));
            transactionDateTxt.setStyle("-fx-text-fill: "+ font +";");
            transactionDateTxt.prefWidthProperty().bind(stage.widthProperty().multiply(0.128));
            GridPane.setValignment(transactionDateTxt, VPos.TOP);
            transactionDateTxt.setAlignment(Pos.CENTER);

            Label transactionIdTxt = new Label(allTransactionInfo.getString("transactionId"));
            transactionIdTxt.setFont(Font.font("Daytona", 12));
            transactionIdTxt.setStyle("-fx-text-fill: "+ font +";");
            transactionIdTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.088));
            GridPane.setValignment(transactionIdTxt, VPos.TOP);
            transactionIdTxt.setAlignment(Pos.CENTER);

            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            Label transactionAmountTxt = new Label(formatter.format(allTransactionInfo.getFloat("amount")));
            transactionAmountTxt.setFont(Font.font("Daytona", 12));
            transactionAmountTxt.setStyle("-fx-text-fill: "+ font +";");
            transactionAmountTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.128));
            GridPane.setValignment(transactionAmountTxt, VPos.TOP);
            transactionAmountTxt.setAlignment(Pos.CENTER);
            //Selects the Category name where categoryIdTable and category Info are joined together
            String catNameQuery = "SELECT cat.CategoryName FROM Category cat LEFT JOIN CategoryIdTable c ON c.categoryId = cat.categoryId WHERE c.categoryId = '"+ allTransactionInfo.getString("categoryId")+"';";
            try {
                ResultSet name = stmt2.executeQuery(catNameQuery);
                //next row
                if (name.next()) {
                    //creates new label for the category name and sets positions and personalized
                    transactionCategoryTxt = new Label(name.getString("CategoryName"));
                    transactionCategoryTxt.setFont(Font.font("Daytona", 12));
                    transactionCategoryTxt.setStyle("-fx-text-fill: " + font + ";");
                    transactionCategoryTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.129));
                    GridPane.setValignment(transactionCategoryTxt, VPos.TOP);
                    transactionCategoryTxt.setAlignment(Pos.CENTER);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Label transactionDescriptionTxt = new Label(allTransactionInfo.getString("transactionDesc"));
            transactionDescriptionTxt.setFont(Font.font("Daytona", 12));
            transactionDescriptionTxt.setStyle("-fx-text-fill: "+ font +";");
            transactionDescriptionTxt.prefWidthProperty().bind(stage.widthProperty().multiply(.298));
            GridPane.setValignment(transactionDescriptionTxt, VPos.TOP);
            transactionDescriptionTxt.setAlignment(Pos.CENTER);

            //adds all labels to the grid pane and sets the row as the index
            transactionTxtPane.add(blankSpace, 0, index);
            transactionTxtPane.add(transactionDateTxt, 1, index);
            transactionTxtPane.add(transactionIdTxt,2,index);
            transactionTxtPane.add(transactionAmountTxt,3,index);
            GridPane.setColumnIndex(transactionCategoryTxt,4);
            GridPane.setRowIndex(transactionCategoryTxt,index);
            transactionTxtPane.getChildren().add(transactionCategoryTxt);
            transactionTxtPane.add(transactionDescriptionTxt,5,index);
            transactionTxtPane.add(blankSpace1, 6, index);

            index++;
        }

        //creates new grid pane
        GridPane progressGrid = new GridPane();
        progressGrid.setAlignment(Pos.CENTER);
        //Selects the current amount that the account has accrued
        String getCurrent=
                "SELECT current_val from Transactions where transactions.accountId = '"+accountId+"' ORDER BY transactionId DESC LIMIT 1";
        try {
            ResultSet current = stmt.executeQuery(getCurrent);
            current.next();
            //parse the current value from the query to equal the float current amount
            currentAmount = current.getFloat("current_val");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //select total number of categories for the account
        String getTotalCat =
                "SELECT count(*) from (SELECT categoryId from categoryidtable where CategoryIdTable.accountId = '"+accountId+"') as x;";
        int totalCat;
        try {
            ResultSet current = stmt.executeQuery(getTotalCat);
            current.next();
            //store the number
            totalCat = current.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //if the total category is 0
        if (totalCat == 0){
            //create currency formatter
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            formatter.setMaximumFractionDigits(0);
            //Create new label for the current state
            Label tips = new Label("Budgeting information not available. Please create a category.");
            tips.setFont(Font.font("Century Gothic", FontWeight.BOLD, 30));
            tips.setStyle("-fx-text-fill: "+ largeTxt +";");
            tips.setAlignment(Pos.CENTER);
            progressGrid.translateYProperty().bind(stage.heightProperty().multiply(.4));
            progressGrid.add(tips, 0, 0);
        }
        else if (totalCat > 1) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            formatter.setMaximumFractionDigits(0);
            //Create new label for the current state
            Label tips = new Label("You have spent a total of ~" + formatter.format(currentAmount) + " across " + totalCat + " categories.");
            tips.setFont(Font.font("Century Gothic", FontWeight.BOLD, 30));
            tips.setStyle("-fx-text-fill: "+ largeTxt +";");
            tips.setAlignment(Pos.CENTER);
            progressGrid.translateYProperty().bind(stage.heightProperty().multiply(.4));
            progressGrid.add(tips, 0, 0);
        }
        else{
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            formatter.setMaximumFractionDigits(0);
            //Create new label for the current state
            Label tips = new Label("You have spent a total of ~" + formatter.format(currentAmount) + " in " + totalCat + " category.");
            tips.setFont(Font.font("Century Gothic", FontWeight.BOLD, 30));
            tips.setStyle("-fx-text-fill: "+ largeTxt +";");
            tips.setAlignment(Pos.CENTER);
            progressGrid.translateYProperty().bind(stage.heightProperty().multiply(.4));
            progressGrid.add(tips, 0, 0);
        }
        //Add children to the stack pane
        stack.getChildren().add(transactionPane);
        stack.getChildren().add(transactionTxtPane);
        stack.getChildren().add(topPane);
        stack.getChildren().add(progressGrid);
        stack.getChildren().add(pane);

        //set window size to not resizable
        stage.setResizable(false);
        Scene scene = new Scene(stack, 1280,720);
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }
    private void informationMenu(Stage stage, String accountId){
        pieChartData.clear();
        //create new grid pane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        //create new linear gradiant and sets background to the gradiant
        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true,CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.61, 0.93, 0.99, 1.0)),
                new Stop(0.5,new Color( 0.39, 0.78, 0.97, 1.0)),
                new Stop(1.0,new Color( 0.01, 0.33, 0.84, 1.0)));
        pane.setBackground(Background.fill(L1));

        //create new shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        //create new shadow effect for background
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0f);
        shadowBg.setOffsetX(5.0f);
        shadowBg.setSpread(.01);
        shadowBg.setColor(Color.BLACK);

        //create back button
        Button btBack = new Button("Back");
        btBack.setBackground(Background.fill(Color.web(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");

        //personalize the buttons
        GridPane.setHalignment(btBack, HPos.CENTER);
        btBack.setEffect(shadow);
        //query to select all category name and id where account ID match
        String getAllCategories =
                "SELECT cat.categoryName, cat.categoryId from CategoryIdTable t LEFT JOIN Category cat ON cat.categoryId = t.categoryId where t.accountId = '"+accountId+"' ORDER BY categoryName;";
        try{
            ResultSet allCategories = stmt.executeQuery(getAllCategories);
            //loop through all category names and add to combobox
            try{
                //while loop through all categories
                while (allCategories.next()){
                    //get sum of all categories
                    String getCategoryTotal =
                            "SELECT SUM(amount) FROM Transactions where categoryId = '" + allCategories.getString("categoryId") + "';";
                    ResultSet total = stmt2.executeQuery(getCategoryTotal);
                    //get next total
                    total.next();
                    //add new instance to the pie-chart with the name, and total expenses
                    pieChartData.add(new PieChart.Data(allCategories.getString("categoryName") + "($"+total.getInt(1)+")", total.getInt(1)));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PieChart chart = new PieChart(pieChartData);

        Label lbTotal = new Label();
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(0);

        String totalSum =
                "SELECT SUM(amount) FROM Transactions where accountId = '"+accountId+"';";
        try{
            ResultSet total = stmt.executeQuery(totalSum);
            total.next();
            lbTotal.setText(formatter.format(total.getFloat(1)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Create new label for the current state
        lbTotal.setFont(Font.font("Century Gothic", FontWeight.BOLD, 35));
        lbTotal.setStyle("-fx-text-fill: "+ largeTxt +";");
        lbTotal.setEffect(shadow);
        chart.setPrefSize(800, 600);
        chart.setLegendVisible(false);
        //add text field and labels to pane
        StackPane sp = new StackPane();
        pane.add(chart,0,0);
        pane.add(btBack,0,1);

        //create new circle to turn the pie-chart into a donut chart
        Circle c1 = new Circle(125, Color.WHITE);
        c1.setCenterX(stage.getWidth()/2);
        c1.setCenterY(stage.getHeight()/2);
        sp.getChildren().add(pane);
        //checks if any categories were created
        if (chart.getData().isEmpty()){
            chart.setTitle("No categories found. Create a category to see information.");
        }
        else {
            chart.setTitle("Category Spending Chart:");
            sp.getChildren().add(c1);
        }
        sp.getChildren().add(lbTotal);

        c1.radiusProperty().bind(chart.heightProperty().divide(5));
        //sets action for back button
        btBack.setOnAction(ex -> {
            try {
                //calls transaction menu
                transactionMenu(stage,accountId,0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        c1.setFill(L1);
        Scene scene = new Scene(sp, 1280, 720);
        //call css sheet
        scene.getStylesheets().add("piechart.css");
        stage.setTitle("Decimal Budget");
        stage.setScene(scene);
        stage.show();
    }

    private void next(Stage stage, String accountId) throws SQLException {
        //Select all transactions
        String transactionsCount =
                "SELECT COUNT(*) FROM (SELECT * FROM Transactions WHERE accountId = '"+accountId+"' ORDER BY transactionId DESC) AS x;";
        ResultSet maxPages;
        try {
            maxPages = stmt.executeQuery(transactionsCount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        maxPages.next();
        //if there are more transactions, go next page
        if (maxPages.getInt(1) > (currentPage + 14)) {
            //increment page number
            currentPage += 14;
            //call transaction menu with current page number
            transactionMenu(stage, accountId, currentPage);
        }
    }
    private void prev(Stage stage, String accountId) throws SQLException {
        //decrement the page unless
        if (currentPage >= 14){
            currentPage -= 14;
            transactionMenu(stage, accountId, currentPage);
        }
    }
    private void addTransactionMenu(Stage stage, String accountId){
        //clear the entries
        tfEntryDate.clear();
        tfTransactionDesc.clear();
        tfAmount.clear();
        tfCategory.clear();

        //create new grid pane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        //create new linear gradiant and sets background to the gradiant
        LinearGradient L1 = new LinearGradient(
                0.0,0.0,1.0,0.0,true,CycleMethod.NO_CYCLE,
                new Stop(0.0,new Color( 0.61, 0.93, 0.99, 1.0)),
                new Stop(0.5,new Color( 0.39, 0.78, 0.97, 1.0)),
                new Stop(1.0,new Color( 0.01, 0.33, 0.84, 1.0)));
        pane.setBackground(Background.fill(L1));

        //create new shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(1.0f);
        shadow.setOffsetX(1.0f);
        shadow.setColor(Color.BLACK);

        //create new shadow effect for background
        DropShadow shadowBg = new DropShadow();
        shadowBg.setOffsetY(5.0f);
        shadowBg.setOffsetX(5.0f);
        shadowBg.setSpread(.01);
        shadowBg.setColor(Color.BLACK);

        //create new label for header
        Label createTxt = new Label("Create New Transaction:");
        createTxt.setFont(Font.font("Aharoni", FontWeight.BOLD,20));
        createTxt.setStyle("-fx-text-fill: "+font+";");
        createTxt.setTranslateY(-10);

        //personalize the text fields and combobox
        cbCategories.setStyle("-fx-base: "+ textBox +";-fx-text-fill: "+font+";-fx-background-radius: 15;");
        tfAmount.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+ textBox +";");
        tfCategory.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+ textBox +";");
        tfEntryDate.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+ textBox +";");
        tfTransactionDesc.setStyle("-fx-text-fill: "+font+";-fx-background-radius: 15;-fx-background-color: "+ textBox +";");
        tfAmount.setPromptText("\"100.00\", etc");
        tfCategory.setPromptText("Select Category");
        tfEntryDate.setPromptText("mm/dd/yyyy");
        tfTransactionDesc.setPromptText("\"food\", \"new shoes\", etc.");
        tfAmount.setEffect(shadow);
        tfTransactionDesc.setEffect(shadow);
        tfEntryDate.setEffect(shadow);
        tfCategory.setEffect(shadow);
        tfTransactionDesc.setMaxWidth(300);
        tfEntryDate.setMaxWidth(300);
        tfAmount.setMaxWidth(300);
        cbCategories.setPrefWidth(300);
        tfTransactionDesc.setPrefHeight(100);
        tfTransactionDesc.setAlignment(Pos.TOP_LEFT);
        cbCategories.setEffect(shadow);

        //create new labels and personalize them
        Label amountTxt = new Label("Transaction amount($):");
        amountTxt.setFont(Font.font("Daytona", 14));
        amountTxt.setStyle("-fx-text-fill: "+font+";");

        Label dateTxt = new Label("Enter date:");
        dateTxt.setFont(Font.font("Daytona", 14));
        dateTxt.setStyle("-fx-text-fill: "+font+";");

        Label categoryTxt = new Label("Select category:");
        categoryTxt.setFont(Font.font("Daytona", 14));
        categoryTxt.setStyle("-fx-text-fill: "+font+";");

        Label descriptionTxt = new Label("Description of transaction:");
        descriptionTxt.setFont(Font.font("Daytona", 14));
        descriptionTxt.setStyle("-fx-text-fill: "+font+";");

        //personalize status label
        lbStatus.setFont(Font.font("Daytona", 14));
        lbStatus.setStyle("-fx-text-fill: "+font+";");
        GridPane.setHalignment(lbStatus, HPos.CENTER);

        //create transaction button
        Button btDone = new Button("Create Transaction");
        btDone.setBackground(Background.fill(Color.web(textBox)));
        btDone.setStyle("-fx-text-fill: "+font+";");

        //create back button
        Button btBack = new Button("Back");
        btBack.setBackground(Background.fill(Color.web(red)));
        btBack.setStyle("-fx-text-fill: "+font+";");

        //personalize the buttons
        GridPane.setHalignment(btDone, HPos.CENTER);
        GridPane.setHalignment(btBack, HPos.CENTER);
        btDone.setEffect(shadow);
        btBack.setEffect(shadow);

        //set the text field date to the current date
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String date = dateObj.format(formatter);
        tfEntryDate.setText(date);

        //query to select all category name where account ID match
        String getAllCategories =
                "SELECT cat.categoryName from CategoryIdTable t LEFT JOIN Category cat ON cat.categoryId = t.categoryId where t.accountId = '"+accountId+"' ORDER BY categoryName;";
        try{
            ResultSet allCategories = stmt.executeQuery(getAllCategories);
            ObservableList<String> list = FXCollections.observableArrayList();
            //loop through all category names and add to combobox
            while (allCategories.next()){
                list.add(allCategories.getString("categoryName"));
            }
            cbCategories.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //add text field and labels to pane
        pane.add(createTxt, 0,0);
        pane.add(amountTxt,0,1);
        pane.add(tfAmount,0,2);
        pane.add(dateTxt,0,3);
        pane.add(tfEntryDate,0,4);
        pane.add(categoryTxt,0,5);
        pane.add(cbCategories,0,6);
        pane.add(descriptionTxt,0,7);
        pane.add(tfTransactionDesc,0,8);
        pane.add(btDone,0,9);
        pane.add(btBack,0,10);
        pane.add(lbStatus,0,11);

        //set action create transaction button
        btDone.setOnAction(ex -> {
            try {
                //checks if text fields are empty
                if (tfEntryDate.getText().isEmpty() || tfAmount.getText().isEmpty() || cbCategories.getValue() == null || tfTransactionDesc.getText().isEmpty()){
                    lbStatus.setText("WARNING: Please fill out text fields");
                }
                else {
                    //calls the insert function
                    insert(Float.parseFloat(tfAmount.getText().trim()), tfEntryDate.getText().trim(), cbCategories.getValue().trim(), tfTransactionDesc.getText().trim(), accountId, stage);
                }
            } catch (SQLException e) {
                lbStatus.setText("Error!");
            }
        });

        //sets action for back button
        btBack.setOnAction(ex -> {
            try {
                //calls transaction menu
                transactionMenu(stage,accountId,0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(pane, 1280,720);
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
            stmt2 = connection.createStatement();

        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private void insert(float amount, String entryDate, String category, String description, String accountId, Stage stage) throws SQLException {
        //gets the current amount
        String getCurrent=
                "SELECT current_val from Transactions where transactions.accountId = '"+accountId+"' ORDER BY transactionId DESC LIMIT 1";
        try {
            ResultSet current = stmt.executeQuery(getCurrent);
            current.next();
            currentAmount = current.getFloat("current_val");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //creates formatter for dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate localDate = LocalDate.parse(entryDate, formatter);
        //increments current amount
        currentAmount += amount;
        //gets the category id from the category name
        String getCatId =
                "SELECT CategoryIdTable.categoryId FROM CategoryIdTable, Category where Category.categoryName = '"+category+"'"+
                        " AND CategoryIdTable.accountId = '"+accountId+"' AND CategoryIdTable.categoryId = Category.categoryId;";
        int catId;
        try{
            ResultSet Id = stmt.executeQuery(getCatId);
            Id.next();
            catId = Id.getInt("categoryId");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //inserts the transaction into the transactions table
        String setAccountId =
                "Insert into Transactions(accountId, transactionDesc, entryDate, current_val, amount, categoryId) values ('"
                        + accountId + "','"
                        + description + "','"
                        + localDate + "','"
                        + currentAmount + "','"
                        + amount + "','"
                        + catId + "');";
        try{
            stmt.executeUpdate(setAccountId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        stage.close();
        transactionMenu(stage, accountId, 0);
    }
}