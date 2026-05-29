package com.smug;

import com.smug.database.DataBaseManager;
import com.smug.model.NovelModel;
import com.smug.service.BookImportService;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] Starting Novel Reader Backend Test...");

        DataBaseManager.initializeDatabase();

        System.out.println("\n[System] Testing database insertion...");
        NovelModel testNovel = new NovelModel(
                "The Hobbit",
                "/home/user/books/the_hobbit.pdf", // Dummy path
                "data/covers/hobbit_cover.png",
                0,
                310,
                false
        );
        DataBaseManager.insertNovel(testNovel);


        System.out.println("\n[System] Testing database retrieval...");
        List<NovelModel> library = DataBaseManager.fetchAllNovels();

        System.out.println("\n--- CURRENT DATABASE CONTENTS ---");
        for (NovelModel novel : library) {
            System.out.println(novel);
        }
        System.out.println("---------------------------------");

        /*File realPdf = new File("/home/smug/Downloads/Computer_Ethics_Course_EN.pdf");

        try {
            System.out.println("\n[System] Parsing real PDF file...");
            NovelModel realNovel = BookImportService.processNewPdf(realPdf);

            System.out.println("[System] Inserting parsed novel into Postgres...");
            DataBaseManager.insertNovel(realNovel);
        } catch (Exception e) {
            System.err.println("[System] Failed to parse file: " + e.getMessage());
        }*/
    }
}
