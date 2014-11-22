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
	private String mBody;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long mUid;
	
	@Column(name = "createdAt")
	private String mCreatedAt;
	
	@Column(name = "reTweetedCount")
	private long mReTweetCount;
	
	@Column(name = "favoriteCount")
	private long mFavoriteCount;
	
	@Column(name = "favorited")
	private boolean mFavorited;
	
	@Column(name = "reTweeted")
	private boolean mReTweeted;
    
	@Column(name = "reTweetedStatus")
	private Tweet mReTweetedStatus;
    
	@Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
	private User user;
	
    private ArrayList<TwitterUrl> mTwitterUrls;
    private ArrayList<TwitterMediaUrl> mTwitterMediaUrls;
    
    public Tweet() {
        this.mTwitterUrls = new ArrayList<TwitterUrl>();
        this.mTwitterMediaUrls = new ArrayList<TwitterMediaUrl>();
    }
    
    //Tweet.fromJSON(...) - factory method...
	public static Tweet fromJSON(JSONObject json){
		
		Tweet tweet = new Tweet();
		// Extract values form JSON
		try{
			tweet.mBody = json.getString("text");
			tweet.mUid = json.getLong("id");
			tweet.mCreatedAt = json.getString("created_at");
			tweet.setReTweetCount(json.getInt("retweet_count"));
			tweet.mFavoriteCount = json.getInt("favorite_count");
			tweet.setRetweeted(json.getBoolean("retweeted"));
			tweet.setFavorited(json.getBoolean("favorited"));
			tweet.mUid = json.getLong("id");
			tweet.user = User.fromJson(json.getJSONObject("user"));
			
			if (!json.isNull("retweeted_status")){
                tweet.mReTweetedStatus = Tweet.fromJSON(json.getJSONObject("retweeted_status"));
            }
            if (!json.getJSONObject("entities").isNull("urls")) {
                tweet.mTwitterUrls = TwitterUrl.fromJSONArray(json.getJSONObject("entities").getJSONArray("urls"));
            }
            if (!json.getJSONObject("entities").isNull("media")) {
                tweet.mTwitterMediaUrls = TwitterMediaUrl.fromJSONArray(json.getJSONObject("entities").getJSONArray("media"));
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
		return mFavoriteCount;
	}

	/**
	 * @return the reTweetCount
	 */
	public long getReTweetCount() {
		return mReTweetCount;
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
		return mBody;
	}
	
	// replacing all the media urls with empty string, so i wouldn't be displayed in the tweet area
	// instead, will display the media contents. Initially, display the image.
	
	// for regular URL, remove http.
	public String getBodyWithoutMediaUrl() {
		String result = mBody;
		if(mTwitterUrls!=null){
	        for (TwitterUrl twitterUrl : mTwitterUrls) {
	            result = result.replaceAll(twitterUrl.getUrl(), twitterUrl.gethtmlUrl());
	        }
        }
		
        if(mTwitterMediaUrls != null){
        	for (TwitterMediaUrl twitterMediaUrl : mTwitterMediaUrls){
        		result = result.replaceAll(twitterMediaUrl.getUrl(), "");
        	}
        }
        return result;
    }
	
	/**
	 * @return the uid
	 */
	public long getUid() {
		return mUid;
	}

	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return mCreatedAt;
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
		return mReTweeted;
	}

	public Tweet getRetweetedStatus() {
		return mReTweetedStatus;
	}

	public boolean isFavorited() {
		return mFavorited;
	}

	public ArrayList<TwitterUrl> getTwitterUrls() {
		return mTwitterUrls;
	}

	public ArrayList<TwitterMediaUrl> getTwitterMediaUrls() {
		return mTwitterMediaUrls;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mBody);
        dest.writeLong(this.mUid);
        dest.writeString(this.mCreatedAt);
        dest.writeParcelable(this.user, 0);
        dest.writeLong(this.getReTweetCount());
        dest.writeByte(isRetweeted() ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mReTweetedStatus, 0);
        dest.writeLong(this.mFavoriteCount);
        dest.writeByte(isFavorited() ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mTwitterUrls);
        dest.writeTypedList(this.mTwitterMediaUrls);
    }

    private Tweet(Parcel in) {
        this();
        this.mBody = in.readString();
        this.mUid = in.readLong();
        this.mCreatedAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.setReTweetCount(in.readLong());
        this.setRetweeted(in.readByte() != 0);
        this.mReTweetedStatus = in.readParcelable(Tweet.class.getClassLoader());
        this.mFavoriteCount = in.readLong();
        this.setFavorited(in.readByte() != 0);
        in.readTypedList(this.mTwitterUrls, TwitterUrl.CREATOR);
        in.readTypedList(this.mTwitterMediaUrls, TwitterMediaUrl.CREATOR);
    }
    
    /**
	 * @param reTweetCount the reTweetCount to set
	 */
	public void setReTweetCount(long reTweetCount) {
		this.mReTweetCount = reTweetCount;
	}

	/**
	 * @param favorited the favorited to set
	 */
	public void setFavorited(boolean favorited) {
		this.mFavorited = favorited;
	}

	public boolean isReTweeted() {
		return mReTweeted;
	}

	public void setReTweeted(boolean reTweeted) {
		this.mReTweeted = reTweeted;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.mFavoriteCount = favoriteCount;
	}

	/**
	 * @param retweeted the retweeted to set
	 */
	public void setRetweeted(boolean retweeted) {
		this.mReTweeted = retweeted;
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
