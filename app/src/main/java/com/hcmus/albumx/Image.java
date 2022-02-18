package com.hcmus.albumx;

import android.os.Parcel;

public class Image extends Media{
    public Image(String itemName, String itemLocation, String itemSize, String itemDateAdded, String itemDateModified, String itemWidth, String itemHeight, String itemDuration, byte isVideo) {
        super(itemName, itemLocation, itemSize, itemDateAdded, itemDateModified, itemWidth, itemHeight, itemDuration, isVideo);
    }

    protected Image(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }
}
