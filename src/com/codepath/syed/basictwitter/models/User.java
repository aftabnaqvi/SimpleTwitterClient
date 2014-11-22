package com.codepath.syed.basictwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "User")
public class User extends Model implements Parcelable{

	@Column(name = "name")
	private String mName;
	
	@Column(name = "screeNname")
	private String mScreenName;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long mUid;
	
	@Column(name = "profileImageUrl")
	private String mProfileImageUrl;
	
	@Column(name = "description")
	private String mDescription;
	
	@Column(name = "followersCount")
	private long mFollowersCount;
	
	@Column(name = "followingCount")
	private long mFollowingCount;
	
	@Column(name = "tweetsCount")
	private long mTweetsCount;
	
	@Column(name = "profileBackgroundImageUrl")
	private String mProfileBackgroundImageUrl;

	public String getDescription() {
		return mDescription;
	}

	public long getFollowersCount() {
		return mFollowersCount;
	}
	
	public String getProfileBackgroundImageUrl() {
		// TODO Auto-generated method stub
		return mProfileBackgroundImageUrl;
	}
	
	public long getFollowingCount() {
		return mFollowingCount;
	}

	public long getTweetsCount() {
		return mTweetsCount;
	}

    public User() {
        super();
    }
	
	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return mScreenName;
	}

	/**
	 * @return the uid
	 */
	public long getUid() {
		return mUid;
	}

	/**
	 * @return the profileImageUrl
	 */
	public String getProfileImageUrl() {
		return mProfileImageUrl;
	}

	// factory method.
	public static User fromJson(JSONObject json) {
		User user = new User();
		try{
			user.mName = json.getString("name");
			user.mUid = json.getLong("id");
			user.mScreenName = json.getString("screen_name");
			user.mProfileImageUrl = json.getString("profile_image_url");
			user.mFollowersCount = json.getLong("followers_count");
			user.mDescription = json.getString("description");
			user.mFollowingCount = json.getLong("friends_count");
			user.mTweetsCount = json.getLong("statuses_count");
			user.mProfileBackgroundImageUrl = json.getString("profile_background_image_url");
			
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return user;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(this.mUid);
        dest.writeString(this.mScreenName);
        dest.writeString(this.mProfileImageUrl);
        dest.writeLong(this.mFollowersCount);
        dest.writeString(this.mDescription);
        dest.writeLong(this.mFollowingCount);
        dest.writeLong(this.mTweetsCount);
        dest.writeString(this.mProfileBackgroundImageUrl);
    }

    private User(Parcel in) {
        this.mName = in.readString();
        this.mUid = in.readLong();
        this.mScreenName = in.readString();
        this.mProfileImageUrl = in.readString();
        this.mFollowersCount = in.readLong();
        this.mDescription = in.readString();
        this.mFollowingCount = in.readLong();
        this.mTweetsCount = in.readLong();
        this.mProfileBackgroundImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// persistence methods
	public void saveUser(){
		this.save();
	}

}
