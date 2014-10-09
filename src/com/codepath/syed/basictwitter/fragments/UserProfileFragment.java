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
	protected TwitterClient client;
	protected User user;
    private ImageView ivProfileImage;
    private TextView tvProfileInfo;
    private TextView tvTweets;
    private TextView tvFollowing;
    private TextView tvFollowers;
    private ImageView ivProfileBackgroundImage;
    private String userName;

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
		client = TwitterApplication.getRestClient();
		user = getArguments().getParcelable("user");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view =	inflater.inflate(R.layout.user_profile_fragment, container, false); // false mean, don't attach the view but do it later.
		setupView(view);
		getUserBanner(user.getScreenName(), view);
		
		return view;
	}

	private void getUserBanner(String userName, final View view){
		client.getUserProfileBanner(userName, new JsonHttpResponseHandler()
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
		ivProfileBackgroundImage = (ImageView)view.findViewById(R.id.ivBanner);
		if(ivProfileBackgroundImage != null){
			Log.d("debug: ", "Setting Banner Image....with URL " + bannerUrl);
			ImageLoader.getInstance().displayImage(bannerUrl, ivProfileBackgroundImage);
		}
	}

	private void setupView(View view) {
		ivProfileImage 	= (ImageView)view.findViewById(R.id.ivProfileImg);
		tvProfileInfo 	= (TextView)view.findViewById(R.id.tvProfileInfo);
		tvTweets	 	= (TextView)view.findViewById(R.id.tvTweets);
		tvFollowing 	= (TextView)view.findViewById(R.id.tvFollowing);
		tvFollowers 	= (TextView)view.findViewById(R.id.tvFollowers);
		

		if(ivProfileImage != null && user.getProfileImageUrl ()!= null){
			ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
		}

		if(tvProfileInfo != null ){
			tvProfileInfo.setText("");
			SpannableString count = new SpannableString(String.valueOf(user.getName())); 
			count.setSpan(new ForegroundColorSpan(Color.WHITE), 0, count.length(), 0);
			count.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), 0);
			tvProfileInfo.append(count);
			
			tvProfileInfo.append("\n" + "@" + user.getScreenName() + "\n" + user.getDescription());
		}
		
		if(tvTweets != null){
			tvTweets.setText("");
			tvTweets.append(getFormattedStyle(user.getTweetsCount()));
			tvTweets.append("\n" + "TWEETS");
		}
		
		if(tvFollowers != null){
			tvFollowers.setText("");
			tvFollowers.append(getFormattedStyle(user.getFollowersCount()));
			tvFollowers.append("\n" + "FOLLOWERS");
		}
		
		if(tvFollowing != null){
			tvFollowing.setText("");
			tvFollowing.append(getFormattedStyle(user.getFollowingCount()));
			tvFollowing.append("\n" + "FOLLOWING");
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
