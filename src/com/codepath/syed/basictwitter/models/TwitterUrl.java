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
	private String mUrl;
   
	@Column (name = "expandedUrl")
	private String mExpandedUrl;
    
	@Column (name = "displayUrl")
    private String mDisplayUrl;

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
            twitterUrl.mUrl = jsonObject.getString("url");
            twitterUrl.mExpandedUrl = jsonObject.getString("expanded_url");
            twitterUrl.mDisplayUrl = jsonObject.getString("display_url");
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return twitterUrl;
    }

    public String gethtmlUrl(){
        return "<a href=\"" + mExpandedUrl + "\">" + mDisplayUrl + "</a>";
    }

    public String getUrl() {
        return mUrl;
    }

    public String getexpandedUrl() {
        return mExpandedUrl;
    }

    public String getdisplayUrl() {
        return mDisplayUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
        dest.writeString(this.mExpandedUrl);
        dest.writeString(this.mDisplayUrl);
    }

    public TwitterUrl() {
    	super();
    }

    private TwitterUrl(Parcel in) {
    	this();
        this.mUrl = in.readString();
        this.mExpandedUrl = in.readString();
        this.mDisplayUrl = in.readString();
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
