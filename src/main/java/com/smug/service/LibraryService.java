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

    public void updateReadingProgress(Integer id, Integer currentPage) {
        repository.updateReadingProgress(id, currentPage);
    }

    public void toggleFavorite(Integer id, boolean isFavorite) {
        repository.toggleFavorite(id, isFavorite);
    }

    public void deleteNovel(Integer id){
        repository.deleteNovel(id);
    }
}