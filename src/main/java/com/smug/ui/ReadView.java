package com.smug.ui;

import com.smug.Main;
import com.smug.model.NovelModel;
import com.smug.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class ReadView {

    private PDDocument document;
    private MainLayout mainLayout;
    private NovelModel novel;
    private BorderPane viewPane;
    private int currentPage;
    private TextArea textViewer;


    private LibraryService libraryService;

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

        // ---- TEXT AREA ----
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(15, 20, 15, 20));

        textViewer = new TextArea();
        VBox.setVgrow(textViewer, Priority.ALWAYS);
        textViewer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        textViewer.setEditable(false);
        textViewer.setWrapText(true);
        textViewer.setFont(Font.font("Georgia", 17));
        textViewer.setStyle(
                "-fx-control-inner-background: #1e2d45;" +
                        "-fx-text-fill: #e8eaf6;" +
                        "-fx-background-color: #1e2d45;" +
                        "-fx-border-color: #2e4066;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 24 32 24 32;" +
                        "-fx-line-spacing: 8;"
        );

        try (PDDocument doc = PDDocument.load(new File(novel.getFilePath()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(currentPage);
            stripper.setEndPage(currentPage);
            textViewer.setText(stripper.getText(doc));
        } catch (Exception ex) {
            textViewer.setText("Could not load PDF: " + ex.getMessage());
        }

        contentBox.getChildren().add(textViewer);
        viewPane.setCenter(contentBox);

        // ---- FOOTER NAVIGATION ----
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15, 0, 0, 0));
        footer.setStyle("-fx-background-color: #111827; -fx-border-color: #2e4066; -fx-border-width: 1 0 0 0;");

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

        Label pageStatus = new Label("Page: " + currentPage);
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
                pageStatus.setText("Page: " + currentPage);
                loadPage(currentPage);
            }
        });

        nextBtn.setOnAction(e -> {
            if (novel.getTotalPages() == null || currentPage < novel.getTotalPages()) {
                currentPage++;
                pageStatus.setText("Page: " + currentPage);
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
            } catch (Exception ex) {
                System.err.println("[Progress save failed] " + ex.getMessage());
            }
            mainLayout.switchToHome(false, "");
        });
    }

    private void loadPage(int page) {
        try (PDDocument doc = PDDocument.load(new File(novel.getFilePath()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);
            textViewer.setText(stripper.getText(doc));
        } catch (Exception ex) {
            textViewer.setText("Could not load PDF: " + ex.getMessage());
        }
    }

    public BorderPane getView() {
        return this.viewPane;
    }
}