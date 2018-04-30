package com.telegroup.nezavisnetvapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Macbook on 3/28/2018.
 */

public class NewsCard implements Serializable {


    @SerializedName("Naslov")
    private String title;
    @SerializedName("vijestID")
    private String newsId;
    @SerializedName("Slika")
    private String imageUrl;
    private String color;
    @SerializedName("meniNaziv")
    private String subCategory;

    public NewsCard(){}
    public NewsCard(String title, String newsId, String imageUrl, String color, String subCategory) {
        this.title = title;
        this.newsId = newsId;
        this.imageUrl = imageUrl;
        this.color = color;
        this.subCategory = subCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public String toString() {
        return "NewsCard{" +
                "title='" + title + '\'' +
                ", newsId='" + newsId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", color='" + color + '\'' +
                ", subCategory='" + subCategory + '\'' +
                '}';
    }
}
