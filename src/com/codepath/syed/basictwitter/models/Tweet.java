package com.codepath.syed.basictwitter.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table (name = "Tweet")
public class Tweet extends Model implements Parcelable{
	@Column(name = "body")
	private String body;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name = "reTweetedCount")
	private long reTweetCount;
	
	@Column(name = "favoriteCount")
	private long favoriteCount;
	
	@Column(name = "favorited")
	private boolean favorited;
	
	@Column(name = "reTweeted")
	private boolean reTweeted;
    
	@Column(name = "reTweetedStatus")
	private Tweet reTweetedStatus;
    
	@Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
	private User user;
	
    private ArrayList<TwitterUrl> twitterUrls;
    private ArrayList<TwitterMediaUrl> twitterMediaUrls;
    
    public Tweet() {
        this.twitterUrls = new ArrayList<TwitterUrl>();
        this.twitterMediaUrls = new ArrayList<TwitterMediaUrl>();
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
                tweet.reTweetedStatus = Tweet.fromJSON(json.getJSONObject("retweeted_status"));
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
		if(twitterUrls!=null){
	        for (TwitterUrl twitterUrl : twitterUrls) {
	            result = result.replaceAll(twitterUrl.getUrl(), twitterUrl.gethtmlUrl());
	        }
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
		return reTweeted;
	}

	public Tweet getRetweetedStatus() {
		return reTweetedStatus;
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
        dest.writeParcelable(this.reTweetedStatus, 0);
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
        this.setReTweetCount(in.readLong());
        this.setRetweeted(in.readByte() != 0);
        this.reTweetedStatus = in.readParcelable(Tweet.class.getClassLoader());
        this.favoriteCount = in.readLong();
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

	public boolean isReTweeted() {
		return reTweeted;
	}

	public void setReTweeted(boolean reTweeted) {
		this.reTweeted = reTweeted;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * @param retweeted the retweeted to set
	 */
	public void setRetweeted(boolean retweeted) {
		this.reTweeted = retweeted;
	}

	public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
    
    // Persistence methods.
    public void saveTweet(){
    	user.save();
    	this.save();
    }
    
    // Get all items.
    public static List<Tweet> getAll() {
        //return new Select().from(Tweet.class).orderBy("uid DESC").execute();
        return new Select().from(Tweet.class).execute();
    }
}
