package com.smug.ui;

import com.smug.Main;
import com.smug.model.NovelModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.util.List;

public class HomeView {

    private MainLayout mainLayout;
    private ScrollPane scrollPane;
    private boolean showOnlyFavorites;
    private String searchQuery;

    public HomeView(MainLayout mainLayout, boolean showOnlyFavorites, String searchQuery) {
        this.mainLayout = mainLayout;
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
            // Grabbing streaming live database rows from your friend's backend repository layer
            List<NovelModel> databaseNovels = Main.repository.getAllNovels();

            if (databaseNovels == null || databaseNovels.isEmpty()) {
                showEmptyMessage("No novels found inside your backend database repository library.");
                return;
            }

            int visibleNovelsCount = 0;

            for (NovelModel novel : databaseNovels) {

                // Filtering based on your friend's model naming convention flag (.isFavior)
                if (showOnlyFavorites && !novel.isFavorite()) {
                    continue;
                }

                String title = novel.getTitle() != null ? novel.getTitle() : "Untitled Novel";
                if (!searchQuery.isEmpty() && !title.toLowerCase().contains(searchQuery)) {
                    continue;
                }

                visibleNovelsCount++;

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

                // Instantly switch straight to interactive reader arena scene on click selection
                novelCard.setOnMouseClicked(e -> mainLayout.switchToReadScene(novel));

                // --- FLOATING FLOATING OVERLAY HEART ELEMENT DESIGN ---
                Button heartBtn = new Button(novel.isFavorite() ? "❤️" : "🤍");
                heartBtn.setFont(Font.font(16));
                heartBtn.setStyle("-fx-background-color: rgba(255,255,255,0.90); -fx-background-radius: 20; -fx-cursor: hand;");

                // Keeps favorited novels' hearts visible
                heartBtn.setVisible(novel.isFavorite());

                // Mouse hover events handling interactive state visibility
                cardContainer.setOnMouseEntered(e -> heartBtn.setVisible(true));
                cardContainer.setOnMouseExited(e -> heartBtn.setVisible(novel.isFavorite()));

                heartBtn.setOnAction(e -> {
                    try {
                        boolean nextState = !novel.isFavorite();
                        // Aligned to pass getFilePath() into your friend's toggle parameter layout mapping
                        Main.repository.toggleFavorite(novel.getId(), nextState);
                        novel.setFavorite(nextState);

                        heartBtn.setText(nextState ? "❤️" : "🤍");

                        if (showOnlyFavorites && !nextState) {
                            mainLayout.switchToHome(true, searchQuery);
                        }
                    } catch (Exception ex) {
                        System.err.println("Could not update favorite state parameter: " + ex.getMessage());
                    }
                });

                cardContainer.getChildren().addAll(novelCard, heartBtn);
                StackPane.setAlignment(heartBtn, Pos.TOP_RIGHT);
                StackPane.setMargin(heartBtn, new Insets(8));

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
            // Clean fallback display placeholder message when running app without spun up PostgreSQL server
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
        msg.setFont(Font.font("Arial",  FontWeight.BOLD,14));
        msg.setStyle("-fx-text-fill: #556080;");
        msg.setPadding(new Insets(40));
        scrollPane.setContent(msg);
    }

    public ScrollPane getView() {
        return this.scrollPane;
    }
}