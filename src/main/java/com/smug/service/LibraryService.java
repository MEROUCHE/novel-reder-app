package com.smug.service;

import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;

import java.util.List;

public class LibraryService {

    private final NovelRepository repository;

    public LibraryService(NovelRepository repository) {
        this.repository = repository;
    }

    public List<NovelModel> getLibrary() {
        return repository.getAllNovels();
    }

    public void updateReadingProgress(
            String filePath,
            Integer currentPage) {

        repository.updateReadingProgress(
                filePath,
                currentPage
        );
    }

    public void toggleFavorite(
            String filePath,
            boolean isFavorite) {

        repository.toggleFavorite(
                filePath,
                isFavorite
        );
    }
}