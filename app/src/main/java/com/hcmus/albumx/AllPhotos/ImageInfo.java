package com.hcmus.albumx.AllPhotos;

import java.util.Date;

public class ImageInfo {
    public int id;
    public String name;
    public String path;
    public Date createDate;
    public Date removeDate;
    Boolean isSelected = false;

    public ImageInfo(int id, String name, String path, Date createDate, Date removeDate) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.createDate = createDate;
        this.removeDate = removeDate;
    }
}
