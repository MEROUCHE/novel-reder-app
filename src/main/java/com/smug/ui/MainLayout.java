package com.smug.ui;

import com.smug.model.NovelModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainLayout {

    private Stage window;
    private BorderPane mainPane;
    private boolean currentlyOnFavoritesPage = false;

    public MainLayout(Stage stage) {
        this.window = stage;
        this.mainPane = new BorderPane();
    }

    public void show() {
        window.setTitle("WebNovel Management Dashboard");

        // ---- TOP CONTROL HEADER BAR ----
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #1e2a4a; ");

        Label logoLabel = new Label("📖 Novel Reader");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logoLabel.setStyle("-fx-text-fill: #e2b96f;");

        // The growing layout spacer engine forces the search field to stick to the far right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search novels...");
        searchField.setPrefWidth(280);
        searchField.setPrefHeight(36);
        searchField.setStyle(
                "-fx-background-color: #2a2a4a;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-prompt-text-fill: #8888aa;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-padding: 0 16 0 16;" +
                        "-fx-font-size: 13px;"
        );

        topBar.getChildren().addAll(logoLabel, spacer, searchField);
        mainPane.setTop(topBar);

        // ---- LEFT PANEL NAVIGATION MENU ----
        VBox leftMenu = new VBox(6);
        leftMenu.setPadding(new Insets(24, 12, 24, 12));
        leftMenu.setPrefWidth(190);
        leftMenu.setStyle("-fx-background-color: #162040;");

        Label menuTitle = new Label("MENU");
        menuTitle.setStyle("-fx-text-fill: #556080; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 10 8;");

        Button homeBtn = createMenuButton("🏠 Home Library",true);
        Button favoriteBtn = createMenuButton("⭐ Favorites",false);

        leftMenu.getChildren().addAll(menuTitle,homeBtn, favoriteBtn);
        mainPane.setLeft(leftMenu);

        // Show standard grid on startup window launch
        switchToHome(false, "");

        homeBtn.setOnAction(e -> {
            setActiveButton(homeBtn,favoriteBtn);
            currentlyOnFavoritesPage = false;
            searchField.clear();
            switchToHome(false, "");
        });

        favoriteBtn.setOnAction(e -> {
            setActiveButton(favoriteBtn,homeBtn);
            currentlyOnFavoritesPage = true;
            searchField.clear();
            switchToHome(true, "");
        });

        // Search trigger executing query on Keyboard Enter Stroke action event
        // Replace setOnAction with textProperty listener:
        searchField.textProperty().addListener((obs, old, newVal) ->
                switchToHome(currentlyOnFavoritesPage, newVal)
        );
// Remove the searchField.setOnAction(...) line

        Scene scene = new Scene(mainPane, 1150, 780);
        window.setScene(scene);
        window.setResizable(true);
        window.show();
    }

    public void switchToHome(boolean showOnlyFavorites, String searchQuery) {
        HomeView homeView = new HomeView(this, showOnlyFavorites, searchQuery);
        mainPane.setCenter(homeView.getView());
    }

    public void switchToReadScene(NovelModel novel) {
        ReadView readView = new ReadView(this, novel);
        mainPane.setCenter(readView.getView());
    }

    private Button createMenuButton(String text,boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0,0,0,12));
        String base = "-fx-background-radius: 10; -fx-font-size: 13px; -fx-cursor: hand;";
        if (active) {
            btn.setStyle(base + "-fx-background-color: #e2b96f; -fx-text-fill: #1a1a2e; -fx-font-weight: bold;");
        } else {
            btn.setStyle(base + "-fx-background-color: transparent; -fx-text-fill: #a0a8c0;");
        }

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#e2b96f"))
                btn.setStyle(base + "-fx-background-color: #2a2a4a; -fx-text-fill: #ffffff;");
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#e2b96f"))
                btn.setStyle(base + "-fx-background-color: transparent; -fx-text-fill: #a0a8c0;");
        });
        return btn;
    }

    private void setActiveButton(Button active, Button inactive) {
        String base = "-fx-background-radius: 10; -fx-font-size: 13px; -fx-cursor: hand;";
        active.setStyle(base + "-fx-background-color: #e2b96f; -fx-text-fill: #1a1a2e; -fx-font-weight: bold;");
        inactive.setStyle(base + "-fx-background-color: transparent; -fx-text-fill: #a0a8c0;");
    }
}