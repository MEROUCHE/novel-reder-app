package com.smug;

import com.smug.database.DataBaseManager;
import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;
import com.smug.repository.PostgresNovelRepository;
import com.smug.service.BookImportService;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] Initializing System via Repository Interface...");

        NovelRepository repository = new PostgresNovelRepository();

        File testFile = new File("/home/smug/Downloads/Chapitre 4 Gestion Mémoire Secondaire_copy.pdf");

        if (testFile.exists()) {
            try {
                System.out.println("\n[System] Step 1: Processing book import...");
                NovelModel newNovel = BookImportService.processNewPdf(testFile);

                System.out.println("[System] Step 2: Saving book via Repository...");
                repository.addNovel(newNovel);

                String bookId = newNovel.getFilePath();

                System.out.println("\n[System] Step 3: Simulating UI interaction (Faved & read to page 12)...");
                repository.toggleFavorite(bookId, true);
                repository.updateReadingProgress(bookId, 12);

            } catch (Exception e) {
                System.err.println("[System] Error during simulation: " + e.getMessage());
            }
        } else {
            System.out.println("\n[System] Skipping import test: Specify a valid local PDF file path to test parsing.");
        }

        System.out.println("\n[System] Step 4: Fetching updated library overview...");
        List<NovelModel> currentLibrary = repository.getAllNovels();

        System.out.println("\n--- FINAL VERIFIED LIBRARY STATE ---");
        for (NovelModel book : currentLibrary) {
            System.out.println(book);
        }
        System.out.println("------------------------------------");
    }
}
