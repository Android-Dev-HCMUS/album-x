package com.hcmus.albumx.AllPhotos;

public class ImageInfo {
    public int id;
    public String name;
    public String path;
    public String createDate;
    public String removeDate;
    Boolean isSelected = false;

    public ImageInfo(int id, String name, String path, String createDate, String removeDate) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.createDate = createDate;
        this.removeDate = removeDate;
    }
}
