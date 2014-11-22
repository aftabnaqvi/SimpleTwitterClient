package com.codepath.syed.basictwitter.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.syed.basictwitter.R;
import com.codepath.syed.basictwitter.TwitterApplication;
import com.codepath.syed.basictwitter.TwitterClient;
import com.codepath.syed.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserProfileFragment extends Fragment {
	private TwitterClient 	mTwitterClient;
	private User 			mUser;
    private ImageView 		mIvProfileImage;
    private TextView 		mTvProfileInfo;
    private TextView 		mTvTweets;
    private TextView	 	mTvFollowing;
    private TextView 		mTvFollowers;
    private ImageView 		mIvProfileBackgroundImage;

    UserProfileFragment(){
    	// empty constructor
    }

    public static UserProfileFragment newInstance(User user){
    	UserProfileFragment profileFragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        profileFragment.setArguments(args);
        return profileFragment;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mTwitterClient = TwitterApplication.getRestClient();
		mUser = getArguments().getParcelable("user");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view =	inflater.inflate(R.layout.user_profile_fragment, container, false); // false mean, don't attach the view but do it later.
		setupView(view);
		getUserBanner(mUser.getScreenName(), view);
		
		return view;
	}

	private void getUserBanner(String userName, final View view){
		mTwitterClient.getUserProfileBanner(userName, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(JSONObject json) {
				try {
					String bannerUrl = json.getJSONObject("sizes").getJSONObject("mobile").getString("url");
					Log.d("debug","bannerUrl -->> " + bannerUrl);
					setBanner(bannerUrl, view);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug: onFailure", e.toString());
				Log.d("debug: onFailure", s);
			}
		});
	}
	
	protected void setBanner(String bannerUrl, View view) {
		mIvProfileBackgroundImage = (ImageView)view.findViewById(R.id.ivBanner);
		if(mIvProfileBackgroundImage != null){
			Log.d("debug: ", "Setting Banner Image....with URL " + bannerUrl);
			ImageLoader.getInstance().displayImage(bannerUrl, mIvProfileBackgroundImage);
		}
	}

	private void setupView(View view) {
		mIvProfileImage = (ImageView)view.findViewById(R.id.ivProfileImg);
		mTvProfileInfo 	= (TextView)view.findViewById(R.id.tvProfileInfo);
		mTvTweets	 	= (TextView)view.findViewById(R.id.tvTweets);
		mTvFollowing 	= (TextView)view.findViewById(R.id.tvFollowing);
		mTvFollowers 	= (TextView)view.findViewById(R.id.tvFollowers);
		
		if(mIvProfileImage != null && mUser.getProfileImageUrl ()!= null){
			ImageLoader.getInstance().displayImage(mUser.getProfileImageUrl(), mIvProfileImage);
		}

		if(mTvProfileInfo != null ){
			mTvProfileInfo.setText("");
			SpannableString count = new SpannableString(String.valueOf(mUser.getName())); 
			count.setSpan(new ForegroundColorSpan(Color.WHITE), 0, count.length(), 0);
			count.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), 0);
			mTvProfileInfo.append(count);
			
			mTvProfileInfo.append("\n" + "@" + mUser.getScreenName() + "\n" + mUser.getDescription());
		}
		
		if(mTvTweets != null){
			mTvTweets.setText("");
			mTvTweets.append(getFormattedStyle(mUser.getTweetsCount()));
			mTvTweets.append("\n" + "TWEETS");
		}
		
		if(mTvFollowers != null){
			mTvFollowers.setText("");
			mTvFollowers.append(getFormattedStyle(mUser.getFollowersCount()));
			mTvFollowers.append("\n" + "FOLLOWERS");
		}
		
		if(mTvFollowing != null){
			mTvFollowing.setText("");
			mTvFollowing.append(getFormattedStyle(mUser.getFollowingCount()));
			mTvFollowing.append("\n" + "FOLLOWING");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
		switch(item.getItemId()){
		case android.R.id.home:
            // app icon in action bar clicked; goto parent activity.
            //this.finish();
            break;
		default:
			break;
		}
		return true;
	}
	
	private SpannableString getFormattedStyle(long number){
		SpannableString count = new SpannableString(String.valueOf(number)); 
		count.setSpan(new ForegroundColorSpan(Color.BLACK), 0, count.length(), 0);
		count.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), 0);
		
		return count;
	}
	
	public void onTweetsClick(View view){
		
	}
	
	public void onFollowingClick(View view){
		
	}

	public void onFollowersClick(View view){
	
	}
}
