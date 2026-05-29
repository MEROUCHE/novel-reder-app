package com.smug.repository;

import com.smug.database.DataBaseManager;
import com.smug.model.NovelModel;

import java.util.List;

public class PostgresNovelRepository implements NovelRepository {

    public PostgresNovelRepository() {
        DataBaseManager.initializeDatabase();
    }

    @Override
    public List<NovelModel> getAllNovels() {
        return DataBaseManager.fetchAllNovels();
    }

    @Override
    public void addNovel(NovelModel novel) {
        DataBaseManager.insertNovel(novel);
    }

    @Override
    public void updateReadingProgress(String filePath, Integer currentPage) {
        DataBaseManager.updateProgress(filePath, currentPage);
    }

    @Override
    public void toggleFavorite(String filePath, boolean isFavorite) {
        DataBaseManager.updateFavorite(filePath,isFavorite);
    }
}
