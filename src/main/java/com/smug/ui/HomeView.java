package com.smug.ui;

import com.smug.Main;
import com.smug.model.NovelModel;
import com.smug.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.util.List;

public class HomeView {

    private MainLayout mainLayout;
    private ScrollPane scrollPane;
    private boolean showOnlyFavorites;
    private String searchQuery;

    private LibraryService libraryService;

    public HomeView(MainLayout mainLayout, LibraryService libraryService, boolean showOnlyFavorites, String searchQuery) {
        this.mainLayout = mainLayout;
        this.libraryService = libraryService;
        this.showOnlyFavorites = showOnlyFavorites;
        this.searchQuery = searchQuery.toLowerCase().trim();
        this.scrollPane = new ScrollPane();
        buildUI();
    }

    private void buildUI() {
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(25));

        int column = 0;
        int row = 0;

        try {
            List<NovelModel> databaseNovels = libraryService.getLibrary();

            if (databaseNovels == null || databaseNovels.isEmpty()) {
                showEmptyMessage("No novels found inside your backend database repository library.");
                return;
            }

            int visibleNovelsCount = 0;

            for (NovelModel novel : databaseNovels) {

                if (showOnlyFavorites && !novel.isFavorite()) continue;

                String title = novel.getTitle() != null ? novel.getTitle() : "Untitled Novel";
                if (!searchQuery.isEmpty() && !title.toLowerCase().contains(searchQuery)) continue;

                visibleNovelsCount++;

                // ---- CARD CONTAINER ---
                StackPane cardContainer = new StackPane();

                VBox novelCard = new VBox(12);
                novelCard.setPrefSize(160, 240);
                novelCard.setAlignment(Pos.CENTER);
                novelCard.setStyle("-fx-background-color: #1e2d45; -fx-border-color: #2e4066; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand;");

                VBox coverContainer = new VBox();
                coverContainer.setAlignment(Pos.CENTER);
                if (novel.getCoverPath() != null && !novel.getCoverPath().isEmpty()) {
                    File coverFile = new File(novel.getCoverPath());
                    if (coverFile.exists()) {
                        ImageView imageView = new ImageView(new Image(coverFile.toURI().toString()));
                        imageView.setFitWidth(110);
                        imageView.setFitHeight(150);
                        coverContainer.getChildren().add(imageView);
                    } else {
                        addDefaultCoverPlaceholder(coverContainer);
                    }
                } else {
                    addDefaultCoverPlaceholder(coverContainer);
                }

                Label titleLabel = new Label(title);
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                titleLabel.setWrapText(true);
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setStyle("-fx-text-fill: #e2e2f0;");
                novelCard.getChildren().addAll(coverContainer, titleLabel);

                novelCard.setOnMouseClicked(e -> mainLayout.switchToReadScene(novel));

                // ---- HEART BUTTON ----
                Button heartBtn = new Button(novel.isFavorite() ? "❤️" : "🤍");
                heartBtn.setFont(Font.font(16));
                heartBtn.setStyle("-fx-background-color: rgba(255,255,255,0.90); -fx-background-radius: 20; -fx-cursor: hand;");
                heartBtn.setVisible(novel.isFavorite());

                heartBtn.setOnAction(e -> {
                    try {
                        boolean nextState = !novel.isFavorite();
                        libraryService.toggleFavorite(novel.getId(), nextState);
                        novel.setFavorite(nextState);
                        heartBtn.setText(nextState ? "❤️" : "🤍");
                        if (showOnlyFavorites && !nextState) {
                            mainLayout.switchToHome(true, searchQuery);
                        }
                    } catch (Exception ex) {
                        System.err.println("Could not update favorite state: " + ex.getMessage());
                    }
                });

                // ---- DELETE BUTTON ----
                Button deleteBtn = new Button("−");
                deleteBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                deleteBtn.setStyle(
                        "-fx-background-color: #c0392b;" +
                                "-fx-text-fill: #ffffff;" +
                                "-fx-background-radius: 20;" +
                                "-fx-cursor: hand;" +
                                "-fx-min-width: 28px;" +
                                "-fx-min-height: 28px;" +
                                "-fx-max-width: 28px;" +
                                "-fx-max-height: 28px;" +
                                "-fx-padding: 0;"
                );
                deleteBtn.setVisible(false);

                deleteBtn.setOnAction(e -> {
                    // ---- BLOCKING MODAL ----
                    javafx.stage.Stage dialog = new javafx.stage.Stage();
                    dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                    dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);
                    dialog.setResizable(false);

                    VBox dialogBox = new VBox(18);
                    dialogBox.setPadding(new Insets(28, 32, 24, 32));
                    dialogBox.setAlignment(Pos.CENTER);
                    dialogBox.setStyle(
                            "-fx-background-color: #1e2d45;" +
                                    "-fx-border-color: #c0392b;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;"
                    );

                    Label icon = new Label("🗑");
                    icon.setFont(Font.font("Arial", 32));

                    Label msgLabel = new Label("Delete \"" + title + "\"?");
                    msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    msgLabel.setStyle("-fx-text-fill: #e2e2f0;");

                    Label subLabel = new Label("This will permanently remove the novel\nfrom your library. This cannot be undone.");
                    subLabel.setStyle("-fx-text-fill: #8899bb; -fx-font-size: 13px;");
                    subLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                    subLabel.setAlignment(Pos.CENTER);

                    HBox btnRow = new HBox(12);
                    btnRow.setAlignment(Pos.CENTER);

                    Button cancelBtn = new Button("Cancel");
                    cancelBtn.setPrefWidth(100);
                    cancelBtn.setPrefHeight(36);
                    cancelBtn.setStyle(
                            "-fx-background-color: #2e4066;" +
                                    "-fx-text-fill: #e2e2f0;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    );

                    Button confirmDeleteBtn = new Button("Yes, Delete");
                    confirmDeleteBtn.setPrefWidth(110);
                    confirmDeleteBtn.setPrefHeight(36);
                    confirmDeleteBtn.setStyle(
                            "-fx-background-color: #c0392b;" +
                                    "-fx-text-fill: #ffffff;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    );

                    cancelBtn.setOnAction(ev -> dialog.close());

                    confirmDeleteBtn.setOnAction(ev -> {
                        dialog.close();
                        try {
                            libraryService.deleteNovel(novel.getId());
                            mainLayout.switchToHome(showOnlyFavorites, searchQuery);
                        } catch (Exception ex) {
                            System.err.println("[Delete Error] " + ex.getMessage());
                        }
                    });

                    btnRow.getChildren().addAll(cancelBtn, confirmDeleteBtn);
                    dialogBox.getChildren().addAll(icon, msgLabel, subLabel, btnRow);

                    javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogBox, 340, 220);
                    dialog.setScene(dialogScene);
                    dialog.showAndWait();
                });

                // ---- HOVER ----
                cardContainer.setOnMouseEntered(e -> {
                    heartBtn.setVisible(true);
                    deleteBtn.setVisible(true);
                });
                cardContainer.setOnMouseExited(e -> {
                    heartBtn.setVisible(novel.isFavorite());
                    deleteBtn.setVisible(false);
                });

                // Stack: card at bottom, heart top-right, delete top-left
                cardContainer.getChildren().addAll(novelCard, heartBtn, deleteBtn);
                StackPane.setAlignment(heartBtn, Pos.TOP_RIGHT);
                StackPane.setMargin(heartBtn, new Insets(8));
                StackPane.setAlignment(deleteBtn, Pos.TOP_LEFT);
                StackPane.setMargin(deleteBtn, new Insets(8));

                grid.add(cardContainer, column, row);

                column++;
                if (column == 5) {
                    column = 0;
                    row++;
                }
            }

            if (visibleNovelsCount == 0) {
                showEmptyMessage("No novels match your active look-up filtering parameters.");
                return;
            }

        } catch (Exception e) {
            showEmptyMessage("⚠️ Database Link Offline. (Running Design and Layout Mode)");
            return;
        }

        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #111827; -fx-background: #111827;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private void addDefaultCoverPlaceholder(VBox container) {
        Label bookIcon = new Label("📖");
        bookIcon.setFont(Font.font("Arial", 40));
        container.getChildren().add(bookIcon);
    }

    private void showEmptyMessage(String message) {
        Label msg = new Label(message);
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        msg.setStyle("-fx-text-fill: #556080;");
        msg.setPadding(new Insets(40));
        scrollPane.setContent(msg);
    }

    public ScrollPane getView() {
        return this.scrollPane;
    }
}