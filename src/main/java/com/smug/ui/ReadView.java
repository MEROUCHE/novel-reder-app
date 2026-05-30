package com.smug.ui;

import com.smug.model.NovelModel;
import com.smug.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.embed.swing.SwingFXUtils;

import java.io.File;

public class ReadView {

    private PDDocument document;
    private MainLayout mainLayout;
    private NovelModel novel;
    private BorderPane viewPane;
    private int currentPage;
    private LibraryService libraryService;
    private ImageView pageView;
    private ScrollPane pageScrollPane;

    public ReadView(MainLayout mainLayout, NovelModel novel, LibraryService libraryService) {
        this.mainLayout = mainLayout;
        this.novel = novel;
        this.libraryService = libraryService;
        this.currentPage = (novel.getCurrentPage() != null) ? novel.getCurrentPage() : 1;
        this.viewPane = new BorderPane();
        try {
            document = PDDocument.load(new File(novel.getFilePath()));
        } catch (Exception e) {
            System.err.println("Could not open PDF: " + e.getMessage());
        }
        buildUI();
    }

    private void buildUI() {
        viewPane.setPadding(new Insets(20));
        viewPane.setStyle("-fx-background-color: #111827;");

        // ---- HEADER ----
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));
        header.setStyle("-fx-border-color: #2e4066; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("⬅ Back to Library");
        backBtn.setStyle(
                "-fx-background-color: #e2b96f;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        Label titleLabel = new Label("📖  " + novel.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #e2e2f0;");

        header.getChildren().addAll(backBtn, titleLabel);
        viewPane.setTop(header);

        // ---- CONTENT ----
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(15, 20, 15, 20));
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        pageView = new ImageView();
        pageView.setPreserveRatio(true);

        // wrap in StackPane for centering
        StackPane imageWrapper = new StackPane(pageView);
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.setStyle("-fx-background-color: #111827;");

        pageScrollPane = new ScrollPane(imageWrapper);
        pageScrollPane.setFitToWidth(true);
        pageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pageScrollPane.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-background: #111827;"
        );
        VBox.setVgrow(pageScrollPane, Priority.ALWAYS);

        // dynamically resize image to fit viewport
        pageScrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            pageView.setFitWidth(newVal.getWidth() - 40);
        });

        contentBox.getChildren().add(pageScrollPane);
        viewPane.setCenter(contentBox);

        // load first page
        loadPage(currentPage);

        // ---- FOOTER ----
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15, 0, 0, 0));
        footer.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-border-color: #2e4066;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        Button prevBtn = new Button("◀  Previous Page");
        prevBtn.setStyle(
                "-fx-background-color: #1e2d45;" +
                        "-fx-text-fill: #e2e2f0;" +
                        "-fx-border-color: #2e4066;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 18 8 18;"
        );

        Label pageStatus = new Label("Page " + currentPage + " of " + novel.getTotalPages());
        pageStatus.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        pageStatus.setStyle("-fx-text-fill: #e2b96f;");

        Button nextBtn = new Button("Next Page  ▶");
        nextBtn.setStyle(
                "-fx-background-color: #1e2d45;" +
                        "-fx-text-fill: #e2e2f0;" +
                        "-fx-border-color: #2e4066;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 18 8 18;"
        );

        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageStatus.setText("Page " + currentPage + " of " + novel.getTotalPages());
                loadPage(currentPage);
            }
        });

        nextBtn.setOnAction(e -> {
            if (novel.getTotalPages() == null || currentPage < novel.getTotalPages()) {
                currentPage++;
                pageStatus.setText("Page " + currentPage + " of " + novel.getTotalPages());
                loadPage(currentPage);
            }
        });

        footer.getChildren().addAll(prevBtn, pageStatus, nextBtn);
        viewPane.setBottom(footer);

        // ---- BACK — saves progress ----
        backBtn.setOnAction(e -> {
            try {
                novel.setCurrentPage(currentPage);
                libraryService.updateReadingProgress(novel.getId(), currentPage);
                if (document != null) document.close();
            } catch (Exception ex) {
                System.err.println("[Progress save failed] " + ex.getMessage());
            }
            mainLayout.switchToHome(false, "");
        });
    }

    private void loadPage(int page) {
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            java.awt.image.BufferedImage bufferedImage = renderer.renderImageWithDPI(page - 1, 150);
            javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            pageView.setImage(fxImage);
            pageScrollPane.setVvalue(0);
        } catch (Exception ex) {
            System.err.println("Could not render page: " + ex.getMessage());
        }
    }

    public BorderPane getView() {
        return this.viewPane;
    }
}