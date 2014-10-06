package com.codepath.syed.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.codepath.syed.basictwitter.R;
import com.codepath.syed.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetListFragment {
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		populateTimeline(false);
	}
	


	//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// Attach the listener to the AdapterView onCreate
//				lvTweets.setOnScrollListener(new EndlessScrollListener() {
//				    @Override
//				    public void onLoadMore(int page, int totalItemsCount) {
//				        // Triggered only when new data needs to be appended to the list
//				        // Add whatever code is needed to append new items to your AdapterView
//				    	
//				    	populateTimeline(false);
//				    }
//			    });
//				lvTweets.setOnRefreshListener(new OnRefreshListener() {
//		            @Override
//		            public void onRefresh() {
//		                // Your code to refresh the list here.
//		                // Make sure you call listView.onRefreshComplete() when
//		                // once the network request has completed successfully.
//		            	populateTimeline(true);
//		            }
//		        });
//				
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}
	protected void populateTimeline(boolean bRefresh) {
		if( !isDeviceConnected() ){
			getFromDB();
			return;
		}
		
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
				addAll(Tweet.fromJSONArray(jsonArray));
				lastTweetId = String.valueOf(tweets.get(tweets.size()-1).getUid()-1);
				Log.d("debug:", tweets.get(tweets.size()-1).getBody());
				lvTweets.onRefreshComplete(); 
				
				for (Tweet tweet : tweets){
                    tweet.saveTweet();
                }
                Tweet.getAll().size();
                
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
    
	private void getFromDB(){
        aTweets.clear();
        aTweets.addAll(Tweet.getAll());
    }
    
    
}
