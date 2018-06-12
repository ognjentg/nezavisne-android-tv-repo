/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.telegroup.nezavisnetvapp;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.telegroup.nezavisnetvapp.model.Image;
import com.telegroup.nezavisnetvapp.model.Video;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/*
 * Article class represents video entity with title, description, image thumbs and video url.
 */
public class Article implements Serializable {
    static final long serialVersionUID = 727566175075960653L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("vijestID")
    private long id;
    @SerializedName("Naslov")
    private String title;
    @SerializedName("Lid")
    private String description;
    @SerializedName("Tjelo")
    private String body;
    @SerializedName("Slika")
    private Image[] images;
    @SerializedName("Galerija")
    private String galleryFlag;
    @SerializedName("meniRoditelj")
    private String categoryId;
    @SerializedName("Video")
    private Video[] videos;

    public Video[] getVideos() {
        return videos;
    }

    public void setVideos(Video[] videos) {
        this.videos = videos;
    }

    public Article(long id, String title, String description, String body, Image[] images, String galleryFlag, String categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.images = images;
        this.galleryFlag = galleryFlag;
        this.categoryId = categoryId;
    }
    public Article(long id, String title, String description, String body, Image[] images, String galleryFlag, String categoryId,Video[] videos) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.images = images;
        this.galleryFlag = galleryFlag;
        this.categoryId = categoryId;
        this.videos=videos;
    }
    public Article(){

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getGalleryFlag() {
        return galleryFlag;
    }

    public void setGalleryFlag(String galleryFlag) {
        this.galleryFlag = galleryFlag;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
