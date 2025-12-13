package com.example.comicreaderapp.manga_model;

public class CategoryItem {
    public String id;
    public String name;

    public CategoryItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "CategoryItem{id='" + id + "', name='" + name + "'}";
    }
}
