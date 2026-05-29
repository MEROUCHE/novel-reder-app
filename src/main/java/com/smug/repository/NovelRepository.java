package com.smug.repository;

import com.smug.model.NovelModel;

import java.util.List;

public interface NovelRepository {

    List<NovelModel> getAllNovels(); // as it says

    void addNovel(NovelModel novel); // nothing to explain

    void updateReadingProgress(String filePath,Integer currentPage); //this one change the current page variable cunsistently

    void toggleFavorite(String filePath,boolean isFavorite); // change the state of the book from fav to not (or the converse) used like(repository.toggleFavorite(novel.getFilePath(), !novel.isFavorite());)
}
