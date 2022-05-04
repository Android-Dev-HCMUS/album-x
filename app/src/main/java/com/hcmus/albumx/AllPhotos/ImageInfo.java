package com.hcmus.albumx.AllPhotos;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    public int id;
    public String name;
    public String path;
    public String createdDate;
    public String modifiedDate;
    public String mimeType;
    public String size;
    public Boolean isSelected = false;

    public ImageInfo() {

    }

    public ImageInfo(int id, String name, String path, Boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    public ImageInfo(int id, String name, String path, String createdDate, String modifiedDate) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }


    public ImageInfo(String name, String createdDate, String mimeType, String size) {
        this.name = name;
        this.createdDate = createdDate;
        this.mimeType = mimeType;
        this.size = size;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
