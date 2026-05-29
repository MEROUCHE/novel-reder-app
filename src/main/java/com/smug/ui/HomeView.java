package com.smug.ui;

import com.smug.Main; // استدعاء كلاس المين للوصول للـ repository
import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class HomeView {

    private MainLayout mainLayout;
    private ScrollPane scrollPane;
    private NovelRepository repository;

    // pass repository in from MainLayout
    public HomeView(MainLayout mainLayout, NovelRepository repository) {
        this.mainLayout = mainLayout;
        this.repository = repository; // now it's assigned!
        this.scrollPane = new ScrollPane();
        buildUI();
    }

    public ScrollPane getView() {
        return this.scrollPane;
    }
    private void buildUI() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        int column = 0;
        int row = 0;

        try {
            List<NovelModel> novels = repository.getAllNovels();

            if (novels == null || novels.isEmpty()) {
                Label emptyLabel = new Label("المكتبة فارغة. قم بإضافة روايات عبر PDF!");
                emptyLabel.setFont(Font.font("System", 16));
                scrollPane.setContent(emptyLabel);
                return;
            }

            for (NovelModel novel : novels) {

                VBox novelCard = new VBox(10);
                novelCard.setPrefSize(160, 220);
                novelCard.setAlignment(Pos.CENTER);
                novelCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

                // try to load the real cover, fall back to emoji if missing
                javafx.scene.Node coverNode;
                try {
                    String coverPath = novel.getCoverPath();
                    if (coverPath != null && new java.io.File(coverPath).exists()) {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(
                                new java.io.File(coverPath).toURI().toString(),
                                140, 180, true, true
                        );
                        coverNode = new javafx.scene.image.ImageView(img);
                    } else {
                        Label fallback = new Label("📖");
                        fallback.setFont(Font.font("System", 48));
                        coverNode = fallback;
                    }
                } catch (Exception ex) {
                    Label fallback = new Label("📖");
                    fallback.setFont(Font.font("System", 48));
                    coverNode = fallback;
                }

                Label titleLabel = new Label(novel.getTitle());
                titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
                titleLabel.setWrapText(true);
                titleLabel.setAlignment(Pos.CENTER);

                int current  = novel.getCurrentPage() != null ? novel.getCurrentPage() : 0;
                int total    = novel.getTotalPages()  != null ? novel.getTotalPages()  : 1;
                double progress = (double) current / total;

                javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(progress);
                progressBar.setPrefWidth(130);

                Label progressLabel = new Label(current + " / " + total + " pages");
                progressLabel.setFont(Font.font("System", 10));
                progressLabel.setStyle("-fx-text-fill: #7f8c8d;");

                novelCard.getChildren().addAll(coverNode, titleLabel, progressBar, progressLabel);
                novelCard.setOnMouseClicked(e -> mainLayout.switchToNovelDetail(novel));

                grid.add(novelCard, column, row);
                column++;
                if (column == 4) { column = 0; row++; }
            }

        } catch (Exception e) {
            Label errorLabel = new Label("⚠️ تعذر الاتصال بقاعدة البيانات.\nتأكد من تشغيل سيرفر PostgreSQL.");
            errorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            errorLabel.setStyle("-fx-text-fill: #c0392b;");

            VBox errorBox = new VBox(errorLabel);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(50));
            scrollPane.setContent(errorBox);
            return;
        }

        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #fafafa;");
    }
    /*private void buildUI() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        int column = 0;
        int row = 0;

        try {
            // 1. جلب قائمة الروايات الحقيقية من الـ Repository الخاص بصديقك
            List<NovelModel> databaseNovels = repository.getAllNovels();

            // التحقق من أن قاعدة البيانات تحتوي على روايات
            if (databaseNovels == null || databaseNovels.isEmpty()) {
                Label emptyLabel = new Label("المكتبة فارغة حالياً. قم بإضافة روايات عبر الـ PDF!");
                emptyLabel.setFont(Font.font("System", 16));
                scrollPane.setContent(emptyLabel);
                return;
            }

            // 2. الدوران على الروايات الحقيقية وبناء الكروت ديناميكياً
            for (NovelModel novel : databaseNovels) {

                VBox novelCard = new VBox(10);
                novelCard.setPrefSize(160, 220);
                novelCard.setAlignment(Pos.CENTER);
                novelCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

                // وضع أيقونة كتاب كـ غلاف افتراضي
                Label coverPlaceholder = new Label("📖");
                coverPlaceholder.setFont(Font.font("System", 32));

                // سحب اسم الرواية الحقيقي المخزن في الـ Model
                // (ملاحظة: إذا كان صديقك يستخدم دالة getName() أو getTitle() استبدلها بها هنا)
                String novelTitle = novel.getFilePath();
                if (novelTitle.contains("/")) {
                    // كود ذكي لتنظيف مسار الملف وعرض اسم الملف النهائي فقط كعنوان
                    novelTitle = novelTitle.substring(novelTitle.lastIndexOf("/") + 1);
                }

                Label titleLabel = new Label(novelTitle);
                titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
                titleLabel.setWrapText(true);
                titleLabel.setAlignment(Pos.CENTER);

                novelCard.getChildren().addAll(coverPlaceholder, titleLabel);

                // عند الضغط على الكارد، ننتقل لواجهة التفاصيل ونمرر الأوبجكت الحقيقي للرواية
                String finalNovelTitle = novelTitle;
                novelCard.setOnMouseClicked(e -> mainLayout.switchToNovelDetail(finalNovelTitle));

                grid.add(novelCard, column, row);

                column++;
                if (column == 4) { // 4 روايات في كل سطر
                    column = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            // في حال عدم وجود اتصال بقاعدة البيانات (مثل خطأ الـ Connection refused الحالي)
            // سيعرض التطبيق رسالة تنبيهية أنيقة بدلاً من الانهيار!
            Label errorLabel = new Label("⚠️ تعذر الاتصال بقاعدة البيانات لعرض الروايات.\nتأكد من تشغيل سيرفر PostgreSQL.");
            errorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            errorLabel.setStyle("-fx-text-fill: #c0392b;");

            VBox errorBox = new VBox(errorLabel);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(50));
            scrollPane.setContent(errorBox);
            return;
        }

        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #fafafa;");
    }

    public ScrollPane getView() {
        return this.scrollPane;
    }*/
}