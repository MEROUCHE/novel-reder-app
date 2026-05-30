package com.smug.service;

import com.smug.model.NovelModel;
import com.smug.repository.NovelRepository;

import java.util.List;

public class LibraryService {

    private final NovelRepository repository;

    public LibraryService(NovelRepository repository) {
        this.repository = repository;
    }

    // get all novels
    public List<NovelModel> getLibrary() {
        return repository.getAllNovels();
    }

    //update the current page
    public void updateReadingProgress(Integer id, Integer currentPage) {
        repository.updateReadingProgress(id, currentPage);
    }

    // change the state of the book from fav to not (or the converse) used like(repository.toggleFavorite(novel.getFilePath(), !novel.isFavorite());)
    public void toggleFavorite(Integer id, boolean isFavorite) {
        repository.toggleFavorite(id, isFavorite);
    }

    // remove novel from db
    public void deleteNovel(Integer id){
        repository.deleteNovel(id);
    }
}