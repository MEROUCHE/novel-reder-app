package com.smug;

import com.smug.database.DataBaseManager;
import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;
import com.smug.repository.PostgresNovelRepository;
import com.smug.service.BookImportService;
import com.smug.service.LibraryService;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] Initializing System via Repository Interface...");

        NovelRepository repository = new PostgresNovelRepository();
        BookImportService bookImportService = new BookImportService(repository);
        LibraryService libraryService = new LibraryService(repository);

        File testFile = new File("/home/smug/Downloads/Chapitre 4 Gestion Mémoire Secondaire_copy.pdf");

        if (testFile.exists()) {
            try {
                System.out.println("\n[System] Step 1: Processing book import...");
                NovelModel newNovel = bookImportService.importBook(testFile);

                Integer bookId = newNovel.getId();

                System.out.println("\n[System] Step 3: Simulating UI interaction (Faved & read to page 12)...");
                libraryService.toggleFavorite(bookId, true);
                libraryService.updateReadingProgress(bookId, 12);

            } catch (Exception e) {
                System.err.println("[System] Error during simulation: " + e.getMessage());
            }
        } else {
            System.out.println("\n[System] Skipping import test: Specify a valid local PDF file path to test parsing.");
        }

        System.out.println("\n[System] Step 4: Fetching updated library overview...");
        List<NovelModel> currentLibrary = libraryService.getLibrary();

        System.out.println("\n--- FINAL VERIFIED LIBRARY STATE ---");
        for (NovelModel book : currentLibrary) {
            System.out.println(book);
        }
        System.out.println("------------------------------------");
    }
}
