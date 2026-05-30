package com.smug;

import com.smug.repository.NovelRepository;
import com.smug.repository.PostgresNovelRepository;
import com.smug.service.BookImportService;
import com.smug.service.LibraryService;
import com.smug.ui.MainLayout;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Global reference to your friend's backend operations
    public static LibraryService libraryService;
    BookImportService bookImportService;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("[System Launcher] Launching Application Framework...");

        // Connect directly to your friend's database tier implementation
        try {
            NovelRepository repository = new PostgresNovelRepository();
            libraryService = new LibraryService(repository);
            bookImportService = new BookImportService(repository);

        } catch (Exception e) {
            System.err.println("DB offline, running in offline mode");
        }

        try {
            // Initialize and present the Main Layout shell window
            MainLayout mainLayout = new MainLayout(primaryStage,libraryService,bookImportService);
            mainLayout.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}