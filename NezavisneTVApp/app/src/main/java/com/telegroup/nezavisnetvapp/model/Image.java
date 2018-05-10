package com.telegroup.nezavisnetvapp.model;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("slikaURL")
    private String url;
    @SerializedName("Naslov")
    private String title;
    @SerializedName("Opis")
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
