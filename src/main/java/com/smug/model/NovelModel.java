package com.smug.model;

import java.util.Objects;

public class NovelModel {

    // my key
    private Integer id;

    private String title;
    private  String filePath;
    private String coverPath;
    private Integer currentPage;
    private  Integer totalPages;
    private boolean isFavorite;

    public NovelModel(String title, String filePath, String coverPath, Integer currentPage, Integer totalPages, boolean isFavorite) {
        this.title = title;
        this.filePath = filePath;
        this.coverPath = coverPath;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.isFavorite = isFavorite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelModel that = (NovelModel) o;
        return isFavorite == that.isFavorite && Objects.equals(title, that.title) && Objects.equals(filePath, that.filePath) && Objects.equals(coverPath, that.coverPath) && Objects.equals(currentPage, that.currentPage) && Objects.equals(totalPages, that.totalPages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, filePath, coverPath, currentPage, totalPages, isFavorite);
    }

    @Override
    public String toString() {
        return "NovelModel{" +
                "title='" + title + '\'' +
                ", filePath='" + filePath + '\'' +
                ", coverPath='" + coverPath + '\'' +
                ", currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
