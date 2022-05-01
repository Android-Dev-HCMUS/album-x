package com.hcmus.albumx.AllPhotos;

import androidx.annotation.NonNull;

public class ImageInfo {
    public int id;
    public String name;
    public String path;
    Boolean isSelected = false;

    public ImageInfo(int id, String name, String path, Boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
