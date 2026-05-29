
package com.smug.ui;

import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainLayout {

    // 1. تعريف المتغيرات هنا على مستوى الكلاس لكي تراها جميع الدالات بالأسفل
    private Stage window;
    private BorderPane mainPane;
    private NovelRepository repository; // add this

    public MainLayout(Stage stage, NovelRepository repository) { // add parameter
        this.window = stage;
        this.mainPane = new BorderPane();
        this.repository = repository; // assign it
    }


    public void show() {
        window.setTitle("WebNovel App - لوحة التحكم");

        // ---- الشريط العلوي الثابت (Top: HBox) ----
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2c3e50;");

        Label logoLabel = new Label("WebNovel App");
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        logoLabel.setStyle("-fx-text-fill: white;");

        TextField searchField = new TextField();
        searchField.setPromptText("ابحث عن رواية...");
        searchField.setPrefWidth(250);

        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("كل التصنيفات", "رعب", "رومانسي", "خيال");
        filterBox.setValue("كل التصنيفات");

        topBar.getChildren().addAll(logoLabel, searchField, filterBox);
        mainPane.setTop(topBar);

        // ---- القائمة الجانبية الثابتة (Left: VBox) ----
        VBox leftMenu = new VBox(10);
        leftMenu.setPadding(new Insets(20, 15, 20, 15));
        leftMenu.setPrefWidth(200);
        leftMenu.setStyle("-fx-background-color: #34495e;");

        Button homeBtn = createMenuButton("🏠 الرئيسية");
        Button favoriteBtn = createMenuButton("⭐ المفضلة");
        Button savedBtn = createMenuButton("💾 المحفوظات");

        leftMenu.getChildren().addAll(homeBtn, favoriteBtn, savedBtn);
        mainPane.setLeft(leftMenu);

        // تشغيل الشاشة المركزية الافتراضية عند الإقلاع
        switchToHome();

        // ربط زر الرئيسية للتنقل
        homeBtn.setOnAction(e -> switchToHome());

        // ---- إعداد وعرض الـ Scene ----
        Scene scene = new Scene(mainPane, 1000, 700);
        window.setScene(scene);
        window.setResizable(true);
        window.show();
    }

    // ---- دالات التبديل الديناميكي للمنتصف (Functions to switch scenes) ----


    public void switchToHome() {
        HomeView homeView = new HomeView(this, repository); // pass it here
        mainPane.setCenter(homeView.getView());
    }

    public void switchToNovelDetail(NovelModel novel) {
        NovelDetailView detailView = new NovelDetailView(this, novel, repository);
        mainPane.setCenter(detailView.getView());
    }

    // دالة مساعدة لتنسيق أزرار القائمة الجانبية
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.BASELINE_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px;"));
        return btn;
    }
}