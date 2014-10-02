package com.codepath.syed.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable{
	private String body;
	private long uid;
	private String createdAt;
	private long reTweetCount;
	private long favoriteCount;
	private boolean retweeted;
    private Tweet retweetedStatus;
    private boolean favorited;
	private User user;
	
    private ArrayList<TwitterUrl> twitterUrls;
    private ArrayList<TwitterMediaUrl> twitterMediaUrls;
    
    public Tweet() {
        super();
    }
    
    //Tweet.fromJSON(...) - factory method...
	public static Tweet fromJSON(JSONObject json){
		
		Tweet tweet = new Tweet();
		// Extract values form JSON
		
		try{
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.setReTweetCount(json.getInt("retweet_count"));
			tweet.favoriteCount = json.getInt("favorite_count");
			tweet.setRetweeted(json.getBoolean("retweeted"));
			tweet.setFavorited(json.getBoolean("favorited"));
			tweet.uid = json.getLong("id");
			tweet.user = User.fromJson(json.getJSONObject("user"));
			
			if (!json.isNull("retweeted_status")){
                tweet.retweetedStatus = Tweet.fromJSON(json.getJSONObject("retweeted_status"));
            }
            if (!json.getJSONObject("entities").isNull("urls")) {
                tweet.twitterUrls = TwitterUrl.fromJSONArray(json.getJSONObject("entities").getJSONArray("urls"));
            }
            if (!json.getJSONObject("entities").isNull("media")) {
                tweet.twitterMediaUrls = TwitterMediaUrl.fromJSONArray(json.getJSONObject("entities").getJSONArray("media"));
            }
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	/**
	 * @return the favoriteCount
	 */
	public long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * @return the reTweetCount
	 */
	public long getReTweetCount() {
		return reTweetCount;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		
		for(int i=0; i<jsonArray.length(); i++){
			JSONObject tweetJson = null;
			try{
				//Log.d("debug: ", jsonArray.getJSONObject(i).toString());
				tweetJson = jsonArray.getJSONObject(i);
			} catch(JSONException e){
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if(tweet != null){
				tweets.add(tweet);
			}
		}
		
		return tweets;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	
	// replacing all the media urls with empty string, so i wouldn't be displayed in the tweet area
	// instead, will display the media contents. Initially, display the image.
	
	// for regular URL, remove http.
	public String getBodyWithoutMediaUrl() {
        String result = body;
        for (TwitterUrl twitterUrl : twitterUrls) {
            result = result.replaceAll(twitterUrl.getUrl(), twitterUrl.gethtmlUrl());
        }
        if(twitterMediaUrls != null){
        	for (TwitterMediaUrl twitterMediaUrl : twitterMediaUrls){
        		result = result.replaceAll(twitterMediaUrl.getUrl(), "");
        	}
        }
        return result;
    }
	
	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return getBody() + " " + getUser().getScreenName();
	}
	
	public boolean isRetweeted() {
		return retweeted;
	}

	public Tweet getRetweetedStatus() {
		return retweetedStatus;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public ArrayList<TwitterUrl> getTwitterUrls() {
		return twitterUrls;
	}

	public ArrayList<TwitterMediaUrl> getTwitterMediaUrls() {
		return twitterMediaUrls;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.user, 0);
        dest.writeLong(this.getReTweetCount());
        dest.writeByte(isRetweeted() ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.retweetedStatus, 0);
        dest.writeLong(this.favoriteCount);
        dest.writeByte(isFavorited() ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.twitterUrls);
        dest.writeTypedList(this.twitterMediaUrls);
    }

    private Tweet(Parcel in) {
        this();
        this.body = in.readString();
        this.uid = in.readLong();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.setReTweetCount(in.readInt());
        this.setRetweeted(in.readByte() != 0);
        this.retweetedStatus = in.readParcelable(Tweet.class.getClassLoader());
        this.favoriteCount = in.readInt();
        this.setFavorited(in.readByte() != 0);
        in.readTypedList(this.twitterUrls, TwitterUrl.CREATOR);
        in.readTypedList(this.twitterMediaUrls, TwitterMediaUrl.CREATOR);
    }
    
    /**
	 * @param reTweetCount the reTweetCount to set
	 */
	public void setReTweetCount(long reTweetCount) {
		this.reTweetCount = reTweetCount;
	}

	/**
	 * @param favorited the favorited to set
	 */
	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	/**
	 * @param retweeted the retweeted to set
	 */
	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
