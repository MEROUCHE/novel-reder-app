package com.smug.ui;

import com.smug.Main;
import com.smug.model.NovelModel;
import com.smug.service.BookImportService;
import com.smug.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class MainLayout {

    private Stage window;
    private BorderPane mainPane;
    private boolean currentlyOnFavoritesPage = false;

    private LibraryService libraryService;
    private BookImportService bookImportService;

    public MainLayout(Stage stage, LibraryService libraryService, BookImportService bookImportService) {
        this.window = stage;
        this.mainPane = new BorderPane();
        this.libraryService = libraryService;
        this.bookImportService = bookImportService;
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

        Button homeBtn = createMenuButton("🏠 Home Library", true);
        Button favoriteBtn = createMenuButton("⭐ Favorites", false);

        // Spacer pushes Add Novel button to the bottom
        Region menuSpacer = new Region();
        VBox.setVgrow(menuSpacer, Priority.ALWAYS);

        // ---- ADD NOVEL BUTTON ----
        Button addNovelBtn = new Button("＋  Add Novel");
        addNovelBtn.setMaxWidth(Double.MAX_VALUE);
        addNovelBtn.setPrefHeight(42);
        addNovelBtn.setAlignment(Pos.CENTER_LEFT);
        addNovelBtn.setPadding(new Insets(0, 0, 0, 12));
        addNovelBtn.setStyle(
                "-fx-background-color: #e2b96f;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        addNovelBtn.setOnMouseEntered(e ->
                addNovelBtn.setStyle(
                        "-fx-background-color: #f0ca85;" +
                                "-fx-text-fill: #1a1a2e;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                )
        );
        addNovelBtn.setOnMouseExited(e ->
                addNovelBtn.setStyle(
                        "-fx-background-color: #e2b96f;" +
                                "-fx-text-fill: #1a1a2e;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                )
        );
        addNovelBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a PDF Novel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File selectedFile = fileChooser.showOpenDialog(window);
            if (selectedFile != null) {
                try {
                    bookImportService.importBook(selectedFile);
                    switchToHome(false, "");
                } catch (Exception ex) {
                    System.err.println("[Import Error] " + ex.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Import Failed");
                    alert.setHeaderText("Could not import the selected PDF.");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        leftMenu.getChildren().addAll(menuTitle, homeBtn, favoriteBtn, menuSpacer, addNovelBtn);
        mainPane.setLeft(leftMenu);

        switchToHome(false, "");

        homeBtn.setOnAction(e -> {
            setActiveButton(homeBtn, favoriteBtn);
            currentlyOnFavoritesPage = false;
            searchField.clear();
            switchToHome(false, "");
        });

        favoriteBtn.setOnAction(e -> {
            setActiveButton(favoriteBtn, homeBtn);
            currentlyOnFavoritesPage = true;
            searchField.clear();
            switchToHome(true, "");
        });

        searchField.textProperty().addListener((obs, old, newVal) ->
                switchToHome(currentlyOnFavoritesPage, newVal)
        );

        Scene scene = new Scene(mainPane, 1150, 780);
        window.setScene(scene);
        window.setResizable(true);
        window.show();
    }

    public void switchToHome(boolean showOnlyFavorites, String searchQuery) {
        HomeView homeView = new HomeView(this, libraryService, showOnlyFavorites, searchQuery);
        mainPane.setCenter(homeView.getView());
    }

    public void switchToReadScene(NovelModel novel) {
        ReadView readView = new ReadView(this, novel, libraryService);
        mainPane.setCenter(readView.getView());
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 12));
        String base = "-fx-background-radius: 10; -fx-font-size: 13px; -fx-cursor: hand;";
        if (active) {
            btn.setStyle(base + "-fx-background-color: #e2b96f; -fx-text-fill: #1a1a2e; -fx-font-weight: bold;");
        } else {
            btn.setStyle(base + "-fx-background-color: #1e2d45; -fx-text-fill: #c8d0e0;");
        }
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#e2b96f"))
                btn.setStyle(base + "-fx-background-color: #2e4066; -fx-text-fill: #ffffff;");
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#e2b96f"))
                btn.setStyle(base + "-fx-background-color: #1e2d45; -fx-text-fill: #c8d0e0;");
        });
        return btn;
    }

    private void setActiveButton(Button active, Button inactive) {
        String base = "-fx-background-radius: 10; -fx-font-size: 13px; -fx-cursor: hand;";
        active.setStyle(base + "-fx-background-color: #e2b96f; -fx-text-fill: #1a1a2e; -fx-font-weight: bold;");
        inactive.setStyle(base + "-fx-background-color: #1e2d45; -fx-text-fill: #c8d0e0;");
    }
}