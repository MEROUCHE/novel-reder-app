package com.smug.repository;

import com.smug.model.NovelModel;

import java.util.List;

public interface NovelRepository {

    List<NovelModel> getAllNovels(); // as it says

    NovelModel addNovel(NovelModel novel); // nothing to explain

    void updateReadingProgress(Integer id,Integer currentPage); //this one change the current page variable cunsistently

    void toggleFavorite(Integer id,boolean isFavorite);// change the state of the book from fav to not (or the converse) used like(repository.toggleFavorite(novel.getFilePath(), !novel.isFavorite());)

    public void deleteNovel(Integer id);
}
