package com.hcmus.albumx;

import android.os.Parcel;

public class Video extends Media{
    public Video(String itemName, String itemLocation, String itemSize, String itemDateAdded, String itemDateModified, String itemWidth, String itemHeight, String itemDuration, byte isVideo) {
        super(itemName, itemLocation, itemSize, itemDateAdded, itemDateModified, itemWidth, itemHeight, itemDuration, isVideo);
    }

    protected Video(Parcel in) { super(in); }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }
}
