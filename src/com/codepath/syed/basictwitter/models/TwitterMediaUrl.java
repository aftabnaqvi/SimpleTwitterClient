package com.codepath.syed.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "TwitterMediaUrl")
public class TwitterMediaUrl implements Parcelable {
	@Column(name = "url")
    private String mUrl;
	
	@Column(name = "expandedUrl")
    private String mExpandedUrl;
	
	@Column(name = "displayUrl")
    private String mDisplayUrl;
	
	@Column(name = "mediaUrlHTTPS")
    private String mMediaUrlHTTPS;

    public TwitterMediaUrl() {
    	super();
    }
    
    public String getExpandedUrl() {
        return mExpandedUrl;
    }

    public String getDisplayUrl() {
        return mDisplayUrl;
    }

    public String getMediaUrlHttps() {
        return mMediaUrlHTTPS;
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
            TwitterMediaUrl.mUrl = jsonObject.getString("url");
            TwitterMediaUrl.mExpandedUrl = jsonObject.getString("expanded_url");
            TwitterMediaUrl.mDisplayUrl = jsonObject.getString("display_url");
            TwitterMediaUrl.mMediaUrlHTTPS = jsonObject.getString("media_url_https");
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return TwitterMediaUrl;
    }

    public String gethtmlUrl(){
        return "<a href=\"" + mMediaUrlHTTPS + "\">" + mDisplayUrl + "</a>";
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private TwitterMediaUrl(Parcel in) {
    	this();
        this.mUrl = in.readString();
        this.mExpandedUrl = in.readString();
        this.mDisplayUrl = in.readString();
        this.mMediaUrlHTTPS = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.mUrl);
        dest.writeString(this.mExpandedUrl);
        dest.writeString(this.mDisplayUrl);
        dest.writeString(this.mMediaUrlHTTPS);
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