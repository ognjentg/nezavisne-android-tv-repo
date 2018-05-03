package com.telegroup.nezavisnetvapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Category implements Serializable {

    @SerializedName("meniID")
    private String id;
    @SerializedName("Naziv")
    private String title;
    @SerializedName("Boja")
    private String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Category(){

    }

    public Category(String id, String title, String color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }
}
