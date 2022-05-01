package com.hcmus.albumx.AllPhotos;

public class GroupImageItem extends ListItem{
    ImageInfo imageInfo;

    public GroupImageItem(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    @Override
    public int getType() {
        return TYPE_IMAGE;
    }
}
