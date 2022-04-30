package com.hcmus.albumx.CloudStorage;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mAccessToken;
    private String mKey;

    public Upload() { }

    public Upload(String name, String imageUrl, String accessToken) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mAccessToken = accessToken;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getAccessToken() { return mAccessToken; }

    public void setAccessToken(String accessToken) { this.mAccessToken = accessToken; }

    @Exclude
    public String getKey() { return mKey; }

    @Exclude
    public void setKey(String key) { mKey = key; }
}
