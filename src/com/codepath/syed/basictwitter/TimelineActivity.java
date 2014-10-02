package com.codepath.syed.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements OnDataChangeEventListener{//, OnItemClickListener{
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private TweetArrayAdapter aTweets;
	private PullToRefreshListView lvTweets;
	private String lastTweetId = null;
	private long max_id;
	private User currentUser;
	private static int REQUEST_CODE = 200;
	private ProgressBar progressbar;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		progressbar = (ProgressBar)findViewById(R.id.progressBar);	
		progressbar.setVisibility(View.VISIBLE);
		
		client = TwitterApplication.getRestClient();
		populateTimeline(false);
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		// Attach the listener to the AdapterView onCreate
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
		        // Triggered only when new data needs to be appended to the list
		        // Add whatever code is needed to append new items to your AdapterView
		    	
		    	populateTimeline(false);
		    }
	    });
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call listView.onRefreshComplete() when
                // once the network request has completed successfully.
            	populateTimeline(true);
            }
        });
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.mobile_banner));
		
		// retrieves current user and saves in the shared preferences.
		getCurrentUserDetails();
		
		//OnItemClickListener
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			     	long viewId = view.getId();
			     	final Tweet tweet = (Tweet)aTweets.getItem(position+1); // I don't know why position is coming as -1. but it is working for now.
			         if (viewId == R.id.tvReply) {
			         	
			             Log.d("debug:", "tvReply Clicked");
			             if(tweet!=null)
			             	showComposeTweetFragment("@"+tweet.getUser().getScreenName()+" ");
			         } else if (viewId == R.id.tvRetweet) {
			        	 if(tweet!=null){
			        		 TextView tvRetweet = (TextView)view.findViewById(R.id.tvRetweet);
			        		 tvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
			        		 tweet.setReTweetCount(tweet.getReTweetCount() + 1);
			        		 tvRetweet.setText(String.valueOf(tweet.getReTweetCount()));
			        	 }
			         	Log.d("debug:", "tvRetweet Clicked");
			         	
			         } else if(viewId == R.id.tvFavorite){
			        	 if(tweet!=null){
			        		 TextView tvFavorite = (TextView)view.findViewById(R.id.tvFavorite);
			        		 tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
			        		 tweet.setReTweetCount(tweet.getFavoriteCount() + 1);
			        		 tvFavorite.setText(String.valueOf(tweet.getFavoriteCount()));
			        	 }
			        	 Log.d("debug:", "tvFavorite Clicked");
			         } else {
			        	 // Note: I will do it later on... not finished yet.
//			         	Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
//		                Tweet tweet = tweets.get(position);
//		                i.putExtra("tweet", tweet);
//		                startActivity(i);
			         }
			     }
        });
	}

	private void getCurrentUserDetails(){
		if( !isDeviceConnected() ){
			Toast.makeText(this, "Netowrk error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		client.getAccountDetails(new JsonHttpResponseHandler(){

			/* (non-Javadoc)
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onSuccess(org.json.JSONArray)
			 */
			@Override
			public void onSuccess(JSONObject json) {
				currentUser = User.fromJson(json);
				saveUser();
				Log.d("debug","User -->> " + json.toString());
			}

			/* (non-Javadoc)
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFailure(java.lang.Throwable, java.lang.String)
			 */
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:", e.toString());
				Log.d("debug:", s);
			}
		});
	}
	private void populateTimeline(boolean bRefresh) {
		if( !isDeviceConnected() )
			return;
		
		if(bRefresh){
			tweets.clear();
			aTweets.clear();
			lastTweetId = null;
		}
		
		client.getHomeTimeline(lastTweetId, new JsonHttpResponseHandler(){

			/* (non-Javadoc)
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onSuccess(org.json.JSONArray)
			 */
			@Override
			public void onSuccess(JSONArray jsonArray) {
				progressbar.setVisibility(View.GONE);
				aTweets.addAll(Tweet.fromJSONArray(jsonArray));
				lastTweetId = String.valueOf(tweets.get(tweets.size()-1).getUid());
				Log.d("debug:", tweets.get(tweets.size()-1).getBody());
				lvTweets.onRefreshComplete(); 
			}

			/* (non-Javadoc)
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFailure(java.lang.Throwable, java.lang.String)
			 */
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:", e.toString());
				Log.d("debug:", s);
			}
		});
	}
	
    // Network handling helpers
    private boolean isDeviceConnected(){
    	if(Utility.isNetworkAvailable(this) == false){
        	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
			getActionBar().setTitle(R.string.network_error);
			Toast.makeText(getApplicationContext(), "Network Error.", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.compose_tweet:
//	    	Intent i = new Intent(this, ComposeTweetActivity.class);
//	    	startActivityForResult(i, REQUEST_CODE);
	    	showComposeTweetFragment("");
	      break;
	    default:
	      break;
	    }

	    return true;
	}
    
    private void showComposeTweetFragment(String tweetReplyTo){
    	FragmentManager fm = getSupportFragmentManager();
    	ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetReplyTo, currentUser);
    	fragment.show(fm, "compose_tweet_fragment");
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("debug:", "data update received");
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			String tweetText = (String) data.getStringExtra("tweet");
			aTweets.clear();
			populateTimeline(false);
			Log.d("debug","Tweet is " + tweetText);
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void dataChangeEvent(String s) {
	}

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//		long id) {
//     	long viewId = view.getId();
//     	 
//         if (viewId == R.id.tvReply) {
//         	final Tweet tweet = (Tweet)aTweets.getItem(position+1); // I don't know why position is coming as -1. but it is working for now.
//             Log.d("debug:", "tvReply Clicked");
//             if(tweet!=null)
//             	showComposeTweetFragment("@"+tweet.getUser().getScreenName()+" ");
//         } else if (viewId == R.id.tvRetweet) {
//         	Log.d("debug:", "tvRetweet Clicked");
//         	
//         } else {
////             	TweetDetailActivity detailActivity
//         	Intent i = new Intent(this, TweetDetailActivity.class);
//         	startActivityForResult(i, RESULT_OK);
//         }
//     }

}
