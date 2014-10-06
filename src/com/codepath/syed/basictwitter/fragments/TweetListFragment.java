package com.codepath.syed.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.syed.basictwitter.ComposeTweetFragment;
import com.codepath.syed.basictwitter.EndlessScrollListener;
import com.codepath.syed.basictwitter.R;
import com.codepath.syed.basictwitter.TweetArrayAdapter;
import com.codepath.syed.basictwitter.TweetDetailActivity;
import com.codepath.syed.basictwitter.TwitterApplication;
import com.codepath.syed.basictwitter.TwitterClient;
import com.codepath.syed.basictwitter.ViewProfileActivity;
import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetListFragment extends Fragment {
	protected TwitterClient client;
	protected ArrayList<Tweet> tweets;
	protected  TweetArrayAdapter aTweets;
	protected  PullToRefreshListView lvTweets;
	protected  String lastTweetId = null;
	protected  long max_id;
	protected  User currentUser;
	protected  static int REQUEST_CODE = 200;
	protected boolean requestFetchDataInProgress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		requestFetchDataInProgress = false;
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), tweets); // use getActivity very carefully... 
		
		 setHasOptionsMenu(true);
		 getCurrentUserDetails();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate the layout
		View view =	inflater.inflate(R.layout.fragment_tweet_list, container, false); // false mean, don't attach the view but do it later.

		// Assign our view references		
		lvTweets = (PullToRefreshListView)view.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);

		
		getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.mobile_banner));
		
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
		
		// retrieves current user and saves in the shared preferences.
		//getCurrentUserDetails();
		
		//OnItemClickListener
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			     	long viewId = view.getId();
			     	final Tweet tweet = (Tweet)aTweets.getItem(position+1); // I don't know why position is coming as -1. but it is working for now.
			         if (viewId == R.id.tvReply) {
			         	
			             Log.d("debug:", "tvReply Clicked");
			             //if(tweet!=null)
			             	//showComposeTweetFragment("@"+tweet.getUser().getScreenName()+" ");
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
			         } else if(viewId == R.id.ivProfileImage){
			        	 Intent i = new Intent(getActivity(), ViewProfileActivity.class);
			             i.putExtra("user_name", (String)view.getTag());
			             startActivity(i);
			         } else {
			        	 // Note: I will do it later on... not finished yet.
			         	Intent i = new Intent(getActivity(), TweetDetailActivity.class);
		                Tweet tweet1 = tweets.get(position);
		                i.putExtra("tweet", tweet1);
		                startActivity(i);
			         }
			     }
        });

		
		// return the layout
		return view;
	}
	
	// Network handling helpers
    protected boolean isDeviceConnected(){
    	if(Utility.isNetworkAvailable(getActivity()) == false){
    		getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
    		getActivity().getActionBar().setTitle(R.string.network_error);
			Toast.makeText(getActivity().getApplicationContext(), "Network Error.", Toast.LENGTH_SHORT).show();
        	return false; 
        }
    	
    	return true;
    }
    
    private void getCurrentUserDetails(){
		if( !isDeviceConnected() ){
			Toast.makeText(getActivity(), "Netowrk error", Toast.LENGTH_SHORT).show();
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

    //---------------------- store current user settings in share preferences
    private void saveUser(){
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
		
		editor.putString("name", currentUser.getName());
		editor.putString("screen_name", currentUser.getScreenName());
		editor.putString("profile_image_url", currentUser.getProfileImageUrl());
		editor.commit();
	}
    
	//---------------------- action selection handlers
//    @Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//    	//getMenuInflater().inflate(R.menu.menu_timeline_activity, menu);
//        return true;
//    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.compose_tweet:
//	    	Intent i = new Intent(this, ComposeTweetActivity.class);
//	    	startActivityForResult(i, REQUEST_CODE);
	    	//showComposeTweetFragment("");
	      break;
	    default:
	      break;
	    }

	    return true;
	}
    
//    private void showComposeTweetFragment(String tweetReplyTo){
//    	FragmentManager fm = getSupportFragmentManager();
//    	ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetReplyTo, currentUser);
//    	fragment.show(fm, "compose_tweet_fragment");
//    }
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d("debug:", "data update received");
//		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//			String tweetText = (String) data.getStringExtra("tweet");
//			aTweets.clear();
//			populateTimeline(false);
//			Log.d("debug","Tweet is " + tweetText);
//		}
//	}
//	
//	@Override
//	public void onBackPressed() {
//		finish();
//	}
//
//	@Override
//	public void dataChangeEvent(String s) {
//	}
	// returning the adapter to activity. its Okay but no recommended.
//	public TweetArrayAdapter getAdapter(){
//		return aTweets;
//	}
	
	// Delagte the adding to the internal adapter. // most recommended approach... minimize the number of codes... 
	// 
	public void addAll(ArrayList<Tweet> tweets){
		aTweets.addAll(tweets);
	}
	
	protected abstract void populateTimeline(boolean b);
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.menu_timeline_activity, menu);
    }	


    
    protected void showComposeTweetFragment(String tweetReplyTo){
    	FragmentManager fm = getFragmentManager();
    	ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetReplyTo, currentUser);
    	
    	fragment.show(fm, "compose_tweet_fragment");
    }

}
