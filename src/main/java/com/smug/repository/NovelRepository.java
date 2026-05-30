package com.smug.repository;

import com.smug.model.NovelModel;

import java.util.List;

public interface NovelRepository {

    // get all novels
    List<NovelModel> getAllNovels(); // as it says

    // add a novel
    NovelModel addNovel(NovelModel novel); // nothing to explain

    //update the current page
    void updateReadingProgress(Integer id,Integer currentPage); //this one change the current page variable cunsistently

    // change the state of the book from fav to not (or the converse) used like(repository.toggleFavorite(novel.getFilePath(), !novel.isFavorite());)
    void toggleFavorite(Integer id,boolean isFavorite);

    // remove novel from db
    public void deleteNovel(Integer id);
}
