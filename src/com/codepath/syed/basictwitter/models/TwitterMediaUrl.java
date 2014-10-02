package com.codepath.syed.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class TwitterMediaUrl implements Parcelable {
    private String url;
    private String expandedUrl;
    private String displayUrl;
    private String mediaUrlHTTPS;

    public TwitterMediaUrl() {
    	
    }

    private TwitterMediaUrl(Parcel in) {
    	this();
        this.url = in.readString();
        this.expandedUrl = in.readString();
        this.displayUrl = in.readString();
        this.mediaUrlHTTPS = in.readString();
    }
    
    public String getexpandedUrl() {
        return expandedUrl;
    }

    public String getdisplayUrl() {
        return displayUrl;
    }

    public String getMedia_url_https() {
        return mediaUrlHTTPS;
    }

    // ----- factory methods
    public static ArrayList<TwitterMediaUrl> fromJSONArray(JSONArray jsonArray) {
        ArrayList<TwitterMediaUrl> twitterMediaUrls = new ArrayList<TwitterMediaUrl>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject TwitterMediaUrlJSON = null;
            try {
                TwitterMediaUrlJSON = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            TwitterMediaUrl twitterMediaUrl = TwitterMediaUrl.fromJSON(TwitterMediaUrlJSON);
            if (twitterMediaUrl != null) {
            	twitterMediaUrls.add(twitterMediaUrl);
            }
        }

        return twitterMediaUrls;
    }
    public static TwitterMediaUrl fromJSON(JSONObject jsonObject) {
        TwitterMediaUrl TwitterMediaUrl = new TwitterMediaUrl();
        try {
            TwitterMediaUrl.url = jsonObject.getString("url");
            TwitterMediaUrl.expandedUrl = jsonObject.getString("expanded_url");
            TwitterMediaUrl.displayUrl = jsonObject.getString("display_url");
            TwitterMediaUrl.mediaUrlHTTPS = jsonObject.getString("media_url_https");
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return TwitterMediaUrl;
    }

    public String gethtmlUrl(){
        return "<a href=\"" + mediaUrlHTTPS + "\">" + displayUrl + "</a>";
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.url);
        dest.writeString(this.expandedUrl);
        dest.writeString(this.displayUrl);
        dest.writeString(this.mediaUrlHTTPS);
    }
    
    public static final Parcelable.Creator<TwitterMediaUrl> CREATOR = new Parcelable.Creator<TwitterMediaUrl>() {
        public TwitterMediaUrl createFromParcel(Parcel source) {
            return new TwitterMediaUrl(source);
        }

        public TwitterMediaUrl[] newArray(int size) {
            return new TwitterMediaUrl[size];
        }
    };
}