package com.codepath.syed.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="TwitterUrl")
public class TwitterUrl implements Parcelable {
	@Column (name = "Url")
	private String url;
    
	@Column (name = "expandedUrl")
	private String expandedUrl;
    
	@Column (name = "displayUrl")
    private String displayUrl;

    public static ArrayList<TwitterUrl> fromJSONArray(JSONArray jsonArray) {
        ArrayList<TwitterUrl> twitterUrls = new ArrayList<TwitterUrl>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject JSONUrl = null;
            try {
            	JSONUrl = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            TwitterUrl twitterUrl = TwitterUrl.fromJSON(JSONUrl);
            if (twitterUrl != null) {
                twitterUrls.add(twitterUrl);
            }
        }

        return twitterUrls;
    }
    public static TwitterUrl fromJSON(JSONObject jsonObject) {
        TwitterUrl twitterUrl = new TwitterUrl();
        try {
            twitterUrl.url = jsonObject.getString("url");
            twitterUrl.expandedUrl = jsonObject.getString("expanded_url");
            twitterUrl.displayUrl = jsonObject.getString("display_url");
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return twitterUrl;
    }

    public String gethtmlUrl(){
        return "<a href=\"" + expandedUrl + "\">" + displayUrl + "</a>";
    }

    public String getUrl() {
        return url;
    }

    public String getexpandedUrl() {
        return expandedUrl;
    }

    public String getdisplayUrl() {
        return displayUrl;
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
    }

    public TwitterUrl() {
    	super();
    }

    private TwitterUrl(Parcel in) {
    	this();
        this.url = in.readString();
        this.expandedUrl = in.readString();
        this.displayUrl = in.readString();
    }
    
    public static final Parcelable.Creator<TwitterUrl> CREATOR = new Parcelable.Creator<TwitterUrl>() {
        public TwitterUrl createFromParcel(Parcel source) {
            return new TwitterUrl(source);
        }

        public TwitterUrl[] newArray(int size) {
            return new TwitterUrl[size];
        }
    };
}
