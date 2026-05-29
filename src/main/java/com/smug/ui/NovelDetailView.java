package com.smug.ui;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NovelDetailView {

    private MainLayout mainLayout;
    private String novelName;
    private VBox rootContainer;

    public NovelDetailView(MainLayout mainLayout, String novelName) {
        this.mainLayout = mainLayout;
        this.novelName = novelName;
        this.rootContainer = new VBox(20);
        buildUI();
    }

    private void buildUI() {
        rootContainer.setPadding(new Insets(25));
        rootContainer.setStyle("-fx-background-color: #ffffff;");

        // زر للعودة للرئيسية مجدداً
        Button backBtn = new Button("⬅ العودة للرئيسية");
        backBtn.setOnAction(e -> mainLayout.switchToHome());

        Label titleLabel = new Label(novelName);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label descriptionLabel = new Label("الوصف: هذه رواية مشوقة ومثيرة مأخوذة مباشرة من قاعدة البيانات والـ PDF المستورد برمجياً.");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("System", 14));

        Label chaptersTitle = new Label("قائمة الفصول المتاحة:");
        chaptersTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        // قائمة الفصول
        ListView<String> chapterListView = new ListView<>();
        chapterListView.getItems().addAll("الفصل الأول: البداية الغامضة", "الفصل الثاني: المواجهة الأولى", "الفصل الثالث: الهروب الكبير");
        chapterListView.setPrefHeight(200);

        // تفاعل عند الضغط على الفصل لقراءته
        chapterListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                // هنا يمكنك فتح نافذة القراءة أو عرض النص بداخل TextArea
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "جاري فتح " + newV);
                alert.show();
            }
        });

        rootContainer.getChildren().addAll(backBtn, titleLabel, descriptionLabel, chaptersTitle, chapterListView);
    }

    public VBox getView() {
        return this.rootContainer;
    }
}
