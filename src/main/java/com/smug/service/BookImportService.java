package com.smug.service;

import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BookImportService {

    private static final String COVERS_DIR = "data/covers/";
    private final NovelRepository repository;

    public BookImportService(NovelRepository repository) {
        this.repository = repository;
    }

    public NovelModel importBook(File pdfFile) throws IOException {
        File directory = new File(COVERS_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {

            String title = document.getDocumentInformation().getTitle();

            if (title == null || title.trim().isEmpty()) {
                title = pdfFile.getName().replace(".pdf", "");
            }

            Integer totalPages = document.getNumberOfPages();

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 72);

            String coverFileName = UUID.randomUUID().toString() + "_cover.png";
            File outputFile = new File(COVERS_DIR + coverFileName);

            ImageIO.write(bufferedImage, "png", outputFile);

            System.out.println("[Service] Successfully parsed: " + title + " (" + totalPages + " pages)");

            NovelModel novel = new NovelModel(title, pdfFile.getAbsolutePath(), outputFile.getPath(), 0, totalPages, false);
            repository.addNovel(novel);
            return novel;
        }
    }
}
