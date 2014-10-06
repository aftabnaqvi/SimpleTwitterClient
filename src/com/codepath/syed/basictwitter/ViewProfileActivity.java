package com.codepath.syed.basictwitter;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.syed.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewProfileActivity extends Activity {
	
    private ImageView ivProfileImage;
    private TextView tvProfileInfo;
    private TextView tvTweets;
    private TextView tvFollowing;
    private TextView tvFollowers;
    
    
    private ListView lvUserTweets;

    private final int REQUEST_CODE = 20;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_profile_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupView();
	}


	private void setupView() {
		User user = (User)getIntent().getParcelableExtra("user");

		ivProfileImage 	= (ImageView)findViewById(R.id.ivProfileImg);
		tvProfileInfo 	= (TextView)findViewById(R.id.tvProfileInfo);
		tvTweets	 	= (TextView)findViewById(R.id.tvTweets);
		tvFollowing 	= (TextView)findViewById(R.id.tvFollowing);
		tvFollowers 	= (TextView)findViewById(R.id.tvFollowers);
		
		if(ivProfileImage != null && user.getProfileImageUrl ()!= null){
			ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
		}
		
		if(tvProfileInfo != null ){
			tvProfileInfo.setText(user.getName() + "\n" + "@" + user.getScreenName() + "\n" + user.getDescription());
		}
		
		if(tvTweets != null){
			tvTweets.setText(String.valueOf(user.getTweetsCount()) + "\n" + "TWEETS");
		}
		
		if(tvFollowers != null){
			tvFollowers.setText(String.valueOf(user.getFollowersCount()) + "\n" + "FOLLOWERS");
		}
		
		if(tvFollowing != null){
			tvFollowing.setText(String.valueOf(user.getFollowingCount()) + "\n" + "FOLLOWING");
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
		switch(item.getItemId()){
		case android.R.id.home:
            // app icon in action bar clicked; goto parent activity.
            this.finish();
            break;
		default:
			break;
		}
		return true;
	}
}
