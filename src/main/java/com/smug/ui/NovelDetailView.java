package com.smug.ui;
import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;

public class NovelDetailView {

    private MainLayout mainLayout;
    private String novelName;
    private VBox rootContainer;

    private NovelRepository repository;
    private NovelModel novel;

    public NovelDetailView(MainLayout mainLayout, NovelModel novel, NovelRepository repository) {
        this.mainLayout = mainLayout;
        this.novel = novel;
        this.repository = repository;
        this.rootContainer = new VBox(20);
        buildUI();
    }

    private void buildUI() {
        rootContainer.setPadding(new Insets(25));
        rootContainer.setStyle("-fx-background-color: #ffffff;");

        Button backBtn = new Button("⬅ العودة للرئيسية");
        backBtn.setOnAction(e -> mainLayout.switchToHome());

        Label titleLabel = new Label(novel.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // real progress info
        int current = novel.getCurrentPage() != null ? novel.getCurrentPage() : 0;
        int total   = novel.getTotalPages()  != null ? novel.getTotalPages()  : 0;

        Label pagesLabel = new Label("📄 " + total + " pages  |  last read: page " + current);
        pagesLabel.setFont(Font.font("System", 13));
        pagesLabel.setStyle("-fx-text-fill: #7f8c8d;");

        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(
                total > 0 ? (double) current / total : 0
        );
        progressBar.setPrefWidth(300);

        Label favoriteLabel = new Label(novel.isFavorite() ? "⭐ في المفضلة" : "☆ غير مفضلة");
        favoriteLabel.setFont(Font.font("System", 13));

        Label fileLabel = new Label("📁 " + novel.getFilePath());
        fileLabel.setFont(Font.font("System", 11));
        fileLabel.setStyle("-fx-text-fill: #95a5a6;");
        fileLabel.setWrapText(true);
// PDF Reader button
        Button readBtn = new Button("📖 اقرأ الرواية");
        readBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        readBtn.setOnAction(e -> openPdfReader());

        rootContainer.getChildren().addAll(
                backBtn, titleLabel, pagesLabel, progressBar, favoriteLabel, fileLabel, readBtn
        );
    }
    private void openPdfReader() {
        try {
            PDDocument document = PDDocument.load(new java.io.File(novel.getFilePath()));
            PDFRenderer renderer = new PDFRenderer(document);

            Stage readerStage = new Stage();
            readerStage.setTitle("قراءة: " + novel.getTitle());

            // the page image
            javafx.scene.image.ImageView pageView = new javafx.scene.image.ImageView();
            pageView.setPreserveRatio(true);
            pageView.setFitWidth(750);

            // page counter label
            int[] currentPage = {novel.getCurrentPage() != null ? novel.getCurrentPage() : 0};
            int totalPages = document.getNumberOfPages();

            Label pageLabel = new Label();

            // load a page by index
            Runnable loadPage = () -> {
                try {
                    BufferedImage img = renderer.renderImageWithDPI(currentPage[0], 150);
                    pageView.setImage(javafx.embed.swing.SwingFXUtils.toFXImage(img, null));
                    pageLabel.setText("صفحة " + (currentPage[0] + 1) + " / " + totalPages);
                } catch (Exception ex) {
                    System.err.println("Error rendering page: " + ex.getMessage());
                }
            };

            // navigation buttons
            Button prevBtn = new Button("◀ السابقة");
            Button nextBtn = new Button("التالية ▶");

            prevBtn.setOnAction(e -> {
                if (currentPage[0] > 0) {
                    currentPage[0]--;
                    loadPage.run();
                }
            });

            nextBtn.setOnAction(e -> {
                if (currentPage[0] < totalPages - 1) {
                    currentPage[0]++;
                    loadPage.run();
                }
            });

            // close and save progress
            readerStage.setOnCloseRequest(e -> {
                repository.updateReadingProgress(novel.getId(), currentPage[0]);
                try { document.close(); } catch (Exception ex) { ex.printStackTrace(); }
            });

            // load first page
            loadPage.run();

            HBox navBar = new HBox(20, prevBtn, pageLabel, nextBtn);
            navBar.setAlignment(Pos.CENTER);
            navBar.setPadding(new Insets(10));

            ScrollPane scrollPane = new ScrollPane(pageView);
            scrollPane.setFitToWidth(true);

            VBox readerLayout = new VBox(10, navBar, scrollPane);
            Scene readerScene = new Scene(readerLayout, 800, 900);
            readerStage.setScene(readerScene);
            readerStage.show();

        } catch (Exception e) {
            System.err.println("Error opening PDF: " + e.getMessage());
        }
    }

    public VBox getView() {
        return this.rootContainer;
    }
}
