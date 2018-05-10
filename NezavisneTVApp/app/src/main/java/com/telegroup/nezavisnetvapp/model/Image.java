package com.telegroup.nezavisnetvapp.model;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("slikaURL")
    private String url;
    @SerializedName("Naslov")
    private String title;
    @SerializedName("Opis")
    private String description;
}
