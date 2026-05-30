package com.smug;

import com.smug.repository.NovelRepository;
import com.smug.repository.PostgresNovelRepository;
import com.smug.ui.MainLayout;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Global reference to your friend's backend operations
    public static NovelRepository repository;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("[System Launcher] Launching Application Framework...");

        // Connect directly to your friend's database tier implementation
        try {
            repository = new PostgresNovelRepository();
        } catch (Exception e) {
            System.err.println("DB offline, running in offline mode");
            repository = null; // HomeView already handles null via its catch block
        }

        try {
            // Initialize and present the Main Layout shell window
            MainLayout mainLayout = new MainLayout(primaryStage);
            mainLayout.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}