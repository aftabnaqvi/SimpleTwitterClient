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
	public String getDescription() {
		return description;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	@Column(name = "name")
	private String name;
	
	@Column(name = "screeNname")
	private String screenName;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "profileImageUrl")
	private String profileImageUrl;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "followersCount")
	private long followersCount;
	
	@Column(name = "followingCount")
	private long followingCount;
	
	@Column(name = "tweetsCount")
	private long tweetsCount;
	
	@Column(name = "profileBackgroundImageUrl")
	private String profileBackgroundImageUrl;

	public String getProfileBackgroundImageUrl() {
		// TODO Auto-generated method stub
		return profileBackgroundImageUrl;
	}
	
	public long getFollowingCount() {
		return followingCount;
	}

	public long getTweetsCount() {
		return tweetsCount;
	}

    public User() {
        super();
    }
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}


	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}


	/**
	 * @return the profileImageUrl
	 */
	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	// factory method.
	public static User fromJson(JSONObject json) {
		User user = new User();
		try{
			user.name = json.getString("name");
			user.uid = json.getLong("id");
			user.screenName = json.getString("screen_name");
			user.profileImageUrl = json.getString("profile_image_url");
			user.followersCount = json.getLong("followers_count");
			user.description = json.getString("description");
			user.followingCount = json.getLong("friends_count");
			user.tweetsCount = json.getLong("statuses_count");
			user.profileBackgroundImageUrl = json.getString("profile_background_image_url");
			
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return user;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeLong(this.followersCount);
        dest.writeString(this.description);
        dest.writeLong(this.followingCount);
        dest.writeLong(this.tweetsCount);
        dest.writeString(this.profileBackgroundImageUrl);
    }

    private User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.followersCount = in.readLong();
        this.description = in.readString();
        this.followingCount = in.readLong();
        this.tweetsCount = in.readLong();
        this.profileBackgroundImageUrl = in.readString();
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
