package com.codepath.syed.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.syed.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetListFragment {

	public HomeTimelineFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void populateTimeline(boolean bRefresh) {
		if( !isDeviceConnected() ){
			getFromDB();
			return;
		}
		
		// Check if we have already fetch tweets once.
        // if we already fetched than fetch the next set of tweets
        // from (max_id - 1). max_id is inclusive so you need decrement it one.
        if (tweets.size() > 0){
        	lastTweetId = String.valueOf(tweets.get(tweets.size() - 1).getUid() - 1) ;
        }
        
		if(bRefresh){
			tweets.clear();
			aTweets.clear();
			lastTweetId = null;
		}
		
		client.getHomeTimeline(lastTweetId, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray jsonArray) {
				addAll(Tweet.fromJSONArray(jsonArray));
				progressBar.setVisibility(View.GONE);
				Log.d("debug: tweets size", String.valueOf(tweets.size()));
				lvTweets.onRefreshComplete(); 
				
				for (Tweet tweet : tweets){
                    tweet.saveTweet();
                }
			}

			/* (non-Javadoc)
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFailure(java.lang.Throwable, java.lang.String)
			 */
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:", e.toString());
				Log.d("debug:", s);
				progressBar.setVisibility(View.GONE);
			}
		});
	}
    
	private void getFromDB(){
        aTweets.clear();
        aTweets.addAll(Tweet.getAll());
    }
}
