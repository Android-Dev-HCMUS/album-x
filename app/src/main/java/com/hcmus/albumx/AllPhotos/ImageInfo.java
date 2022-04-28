package com.hcmus.albumx.AllPhotos;

public class ImageInfo {
    public int id;
    public String name;
    public String path;
    public String createdDate;
    public String modifiedDate;
    public String mimeType;
    public String size;
    Boolean isSelected = false;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
