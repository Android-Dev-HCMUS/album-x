package com.hcmus.albumx.AllPhotos;


public abstract class ListItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_IMAGE = 1;

    abstract public int getType();
}
