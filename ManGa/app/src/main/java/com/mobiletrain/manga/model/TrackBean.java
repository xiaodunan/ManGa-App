package com.mobiletrain.manga.model;

/**
 * Created by qf on 2016/10/29.
 */
public class TrackBean {
    private String type;
    private String title;
    private String link;
    private String mangaId;
    private String thumbnail;
    private String time;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TrackBean(int id,String type, String title, String link, String mangaId, String thumbnail, String time) {
        this.type = type;
        this.title = title;
        this.link = link;
        this.mangaId = mangaId;
        this.thumbnail = thumbnail;
        this.time=time;
        this.id=id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
