package com.smug.repository;

import com.smug.model.NovelModel;

import java.util.List;

public interface NovelRepository {

    List<NovelModel> getAllNovels(); // as it says

    void addNovel(NovelModel novel); // nothing to explain

    void updateReadingProges(String filePath,int currentPage); //this one change the current page variable cunsistently

    void changIsFavorit(String filePath,boolean isFavorit); // change the state of the book from fav to not (or the converse) used like(repository.toggleFavorite(novel.getFilePath(), !novel.isFavorite());)
}
