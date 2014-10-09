package com.codepath.syed.basictwitter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.codepath.syed.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.syed.basictwitter.fragments.UserProfileFragment;
import com.codepath.syed.basictwitter.fragments.UserTimelineFragment;
import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;

public class UserProfileActivity extends FragmentActivity implements ComposeFragmentListener{
	protected TwitterClient client;
	protected User currUser;
	protected UserProfileFragment mUserProfileFragment;
	protected UserTimelineFragment mUserTimelineFragment;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile_activity);
		client = TwitterApplication.getRestClient();
		getActionBar().setDisplayHomeAsUpEnabled(true);

		currUser = getIntent().getParcelableExtra("user");
				
		FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
        mUserProfileFragment = UserProfileFragment.newInstance(currUser);
        ft.replace(R.id.flUserProfileInfo, mUserProfileFragment);
        
        mUserTimelineFragment = UserTimelineFragment.newInstance(currUser, 0);
        ft.replace(R.id.flUserTimeline, mUserTimelineFragment, "user_time_line_fragment");
        
        ft.commit();
        fm.executePendingTransactions();
        
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

	@Override
	public void onPostTweet(boolean bPosted, Tweet newTweet) {
//		getSupportFragmentManager().executePendingTransactions();
//		UserTimelineFragment userTimelineFragment = (UserTimelineFragment)getSupportFragmentManager().findFragmentByTag("user_time_line_fragment");
		if(bPosted && mUserTimelineFragment != null){
			mUserTimelineFragment.insert(newTweet);
		}
	}
}
