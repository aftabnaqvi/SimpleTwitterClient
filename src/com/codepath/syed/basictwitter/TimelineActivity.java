package com.codepath.syed.basictwitter;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.codepath.syed.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.syed.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.syed.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.syed.basictwitter.listeners.FragmentTabListener;
import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;

// should use FragmentActivity or ActionBarActivity
public class TimelineActivity extends FragmentActivity implements ComposeFragmentListener{//, OnItemClickListener{
	protected TwitterClient client;
	private User currentUser;
	private SearchView searchView;;
	private HomeTimelineFragment mHomeTimelineFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		getCurrentUserDetails();
		setupTabs();
		getSupportFragmentManager().executePendingTransactions();
		mHomeTimelineFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag("home"); // fragment name is "home" here. be careful....
		Log.d("Fragment TAG: ", mHomeTimelineFragment.getTag());
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab home = actionBar
			.newTab()
			.setText("Home")
			.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home",
						HomeTimelineFragment.class));

		actionBar.addTab(home);
		actionBar.selectTab(home); // default selection

		Tab mentions = actionBar
			.newTab()
			.setText("Mentions")
			.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions",
			    		MentionsTimelineFragment.class));

		actionBar.addTab(mentions);
	}

	 private void getCurrentUserDetails(){
			if( !isDeviceConnected() ){
				Toast.makeText(this, "Netowrk error", Toast.LENGTH_SHORT).show();
				return;
			}
			
			client.getAccountDetails(new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					currentUser = User.fromJson(json);
					saveUser();
					Log.d("debug","User -->> " + json.toString());
				}

				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug:", e.toString());
					Log.d("debug:", s);
				}
			});
		}
	// Network handling helpers
	    protected boolean isDeviceConnected(){
	    	if(Utility.isNetworkAvailable(this) == false){
	    		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
	    		getActionBar().setTitle(R.string.network_error);
				Toast.makeText(this.getApplicationContext(), "Network Error.", Toast.LENGTH_SHORT).show();
	        	return false; 
	        }
	    	
	    	return true;
	    }

	  //---------------------- store current user settings in share preferences
	    private void saveUser(){
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			
			editor.putString("name", currentUser.getName());
			editor.putString("screen_name", currentUser.getScreenName());
			editor.putString("profile_image_url", currentUser.getProfileImageUrl());
			editor.commit();
		}

	//---------------------- action selection handlers
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_timeline_activity, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            	//searchView.setIconified(true); // stopped calling twice... http://stackoverflow.com/questions/17874951/searchview-onquerytextsubmit-runs-twice-while-i-pressed-once
            	searchView.clearFocus();
            	Log.i("INFO: query....", query);
            	
            	// FOLLOWING TWO LINE ARE ALOS WORKING...
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
               
            	// check network availability 
        		if(!isDeviceConnected())
        			return false;

                searchTweets(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }
    
    private void searchTweets(String query) {
    	Intent seach = new Intent(this, SearchResultActivity.class);
    	seach.putExtra("query", query);
    	startActivity(seach);
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.compose_tweet:
	    	showComposeTweetFragment("");
	      break;
	      
	    case R.id.view_profile:
	    	Intent i = new Intent(this, UserProfileActivity.class);
	    	i.putExtra("user", currentUser);
	    	startActivity(i);
	    	break;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }

	    return true;
	}
    
    protected void showComposeTweetFragment(String tweetReplyTo){
    	FragmentManager fm = getSupportFragmentManager();
    	ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetReplyTo, 0, currentUser);
    	
    	fragment.show(fm, "compose_tweet_fragment");
    }

	@Override
	public void onPostTweet(boolean bPosted, Tweet newTweet) {
		//HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentByTag("HomeTimelineFragment");
		if(bPosted && mHomeTimelineFragment != null){
			mHomeTimelineFragment.insert(newTweet);
		}
	}
}
