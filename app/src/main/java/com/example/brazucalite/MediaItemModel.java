package com.example.brazucalite;

public class MediaItemModel {
    public final String id;
    public final String title;
    public final String year;
    public final String category;
    public final String poster;
    public final String streamUrl;

    public MediaItemModel(String id, String title, String year, String category, String poster, String streamUrl) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.category = category;
        this.poster = poster;
        this.streamUrl = streamUrl;
    }
}
