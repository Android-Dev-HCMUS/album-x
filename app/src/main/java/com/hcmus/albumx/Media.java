package com.hcmus.albumx;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Media implements Parcelable {
    protected String itemName;
    protected String itemLocation;
    protected String itemSize;
    protected String itemDateAdded;
    protected String itemDateModified;
    protected String itemWidth;
    protected String itemHeight;
    protected String itemDuration;
    protected byte isVideo;

    /*-------------------------Constructor-------------------*/

    public Media(String itemName, String itemLocation, String itemSize, String itemDateAdded, String itemDateModified, String itemWidth, String itemHeight, String itemDuration, byte isVideo) {
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.itemSize = convertMediaSize(itemSize);
        this.itemDateAdded = convertDate(itemDateAdded);
        this.itemDateModified = convertDate(itemDateModified);
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.itemDuration = itemDuration.isEmpty() ? null : convertDuration(itemDuration);
        this.isVideo = isVideo;
    }

    protected Media(Parcel in) {
        itemName = in.readString();
        itemLocation = in.readString();
        itemSize = in.readString();
        itemDateAdded = in.readString();
        itemDateModified = in.readString();
        itemWidth = in.readString();
        itemHeight = in.readString();
        itemDuration = in.readString();
        isVideo = in.readByte();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) { return new Media(in); }

        @Override
        public Media[] newArray(int size) { return new Media[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(itemName);
        parcel.writeString(itemLocation);
        parcel.writeString(itemSize);
        parcel.writeString(itemDateAdded);
        parcel.writeString(itemDateModified);
        parcel.writeString(itemWidth);
        parcel.writeString(itemHeight);
        parcel.writeString(itemDuration);
        parcel.writeByte(isVideo);
    }

    /*--------------------Getter---------------------*/
    public String getItemName() { return itemName; }
    public String getItemLocation() { return itemLocation; }
    public String getItemSize() { return itemSize; }
    public String getItemDateAdded() { return itemDateAdded; }
    public String getItemDateModified() { return itemDateModified; }
    public String getItemWidth() { return itemWidth; }
    public String getItemHeight() { return itemHeight; }
    public String getItemDuration() { return itemDuration; }
    public byte getIsVideo() { return isVideo; }
    public static Creator<Media> getCREATOR() { return CREATOR; }
    /*--------------------Setter-----------------------*/
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemLocation(String itemLocation) { this.itemLocation = itemLocation; }
    public void setItemSize(String itemSize) { this.itemSize = itemSize; }
    public void setItemDateAdded(String itemDateAdded) { this.itemDateAdded = itemDateAdded; }
    public void setItemDateModified(String itemDateModified) { this.itemDateModified = itemDateModified; }
    public void setItemWidth(String itemWidth) { this.itemWidth = itemWidth; }
    public void setItemHeight(String itemHeight) { this.itemHeight = itemHeight; }
    public void setItemDuration(String itemDuration) { this.itemDuration = itemDuration; }
    public void setIsVideo(byte isVideo) { this.isVideo = isVideo; }




    /*---------------------------CONVERT SIZE / DATE / DURATION-------------------------------------------------*/
    //not done

    private String convertMediaSize(String size) {
        ArrayList<String> sizeLevel = new ArrayList<>(Arrays.asList("B", "KB", "MB", "GB", "TB"));
        double sizeLong = Double.parseDouble(size);
        double sizeThreshold = 1024;
        int numberLoop = 0;
        do {
            numberLoop++;
            sizeLong = sizeLong / sizeThreshold;
        } while (sizeLong > sizeThreshold);
        DecimalFormat df = new DecimalFormat("###.#");
        return df.format(sizeLong) + sizeLevel.get(numberLoop);
    }

    private String convertDate(String date) {
        long milli = Long.parseLong(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli*1000);
        String day = modifiedTime(calendar.get(Calendar.DAY_OF_MONTH)) + "/";
        String month = modifiedTime(calendar.get(Calendar.MONTH)) + "/";
        String year = calendar.get(Calendar.YEAR) + " ";
        String hour = modifiedTime(calendar.get(Calendar.HOUR)) + ":";
        String minute = modifiedTime(calendar.get(Calendar.MINUTE));
        String meridiem = (calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " PM" : " AM");
        return day + month + year + hour + minute + meridiem;
    }

    private String convertDuration(String duration) {
        long milli = Long.parseLong(duration);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(milli));
    }

    private String modifiedTime(int time) {
        return (time >= 10 ? String.valueOf(time) : "0" + time);
    }


}
