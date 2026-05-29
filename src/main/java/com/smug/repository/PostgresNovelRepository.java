package com.smug.repository;

import com.smug.database.DataBaseManager;
import com.smug.model.NovelModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.smug.database.DataBaseManager.getConnection;

public class PostgresNovelRepository implements NovelRepository {

    public PostgresNovelRepository() {
        DataBaseManager.initializeDatabase();
    }

/*    @Override
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
    }*/
@Override
    public NovelModel addNovel(NovelModel novel) {
        String insertSql = "INSERT INTO novels(title, file_path, cover_path, total_pages) " +
                "VALUES(?,?,?,?) ON CONFLICT (file_path) DO NOTHING";

        String selectSql = "SELECT id FROM novels WHERE file_path = ?";

        try (Connection conn = getConnection()) {

            // Try to insert
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, new String[]{"id"})) {
                pstmt.setString(1, novel.getTitle());
                pstmt.setString(2, novel.getFilePath());
                pstmt.setString(3, novel.getCoverPath());
                if (novel.getTotalPages() != null) {
                    pstmt.setInt(4, novel.getTotalPages());
                } else {
                    pstmt.setNull(4, Types.INTEGER);
                }
                pstmt.executeUpdate();

                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        novel.setId(keys.getInt(1));
                        return novel; // new book, we have the id
                    }
                }
            }

            // Book already existed — fetch its id
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setString(1, novel.getFilePath());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        novel.setId(rs.getInt("id"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[Backend] Error inserting novel: " + e.getMessage());
        }
        return novel;
    }

    @Override
    public List<NovelModel> getAllNovels() {
        List<NovelModel> novels = new ArrayList<>();
        String querySQL = "SELECT * FROM novels";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                String title = rs.getString("title");
                String filePath = rs.getString("file_path");
                String coverPath = rs.getString("cover_path");
                Integer currentPage = (Integer) rs.getObject("current_page");
                Integer totalPages = (Integer) rs.getObject("total_pages");
                boolean isFavorite = rs.getBoolean("is_favorite"); // Directly fetch boolean

                novels.add(new NovelModel(title, filePath, coverPath, currentPage, totalPages, isFavorite));
            }
        } catch (SQLException e) {
            System.err.println("[Backend] Error fetching novels: " + e.getMessage());
        }
        return novels;

    }

    @Override
    public void updateReadingProgress(Integer id, Integer currentPage) {
        String sql = "UPDATE novels SET current_page = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (currentPage != null) {
                pstmt.setInt(1, currentPage);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[Database] Error updating progress: " + e.getMessage());
        }
    }


    @Override
    public void toggleFavorite(Integer id, boolean isFavorite) {
        String sql = "UPDATE novels SET is_favorite = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id"})) {

            pstmt.setBoolean(1, isFavorite);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[Database] Error updating favorite: " + e.getMessage());
        }
    }

    @Override
    public void deleteNovel(Integer id) {
        String sql = "DELETE FROM novels WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[Database] Error deleting novel: " + e.getMessage());
        }
    }
}
