package com.codepath.syed.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.syed.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsTimelineFragment extends TweetListFragment {

	public MentionsTimelineFragment(){
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void populateTimeline(boolean bRefresh) {
		mProgressBar.setVisibility(View.VISIBLE);
		if( !isDeviceConnected() ){
			getFromDB();
			mProgressBar.setVisibility(View.GONE);
			return;
		}
		// Check if we have already fetch tweets once.
        // if we already fetched than fetch the next set of tweets
        // from (max_id - 1). max_id is inclusive so you need decrement it one.
        if (mTweets.size() > 0){
        	mLastTweetId = String.valueOf(mTweets.get(mTweets.size() - 1).getUid() - 1) ;
        }
        
		if(mRequestFetchDataInProgress == true){
			Log.i("debug: ", "A request to fetch data from network resource is already in progress.");
			return;
		}
		if(bRefresh){
			mTweets.clear();
			mTweetsAdapter.clear();
			mLastTweetId = null;
		}
		mRequestFetchDataInProgress = true;
		mTwitterClient.getMentionsTimeline(mLastTweetId, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONArray jsonArray) {
				addAll(Tweet.fromJSONArray(jsonArray));
				mProgressBar.setVisibility(View.GONE);
				mLvTweets.onRefreshComplete(); 
				
				for (Tweet tweet : mTweets){
                    tweet.saveTweet();
                }
				mRequestFetchDataInProgress = false;
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:", e.toString());
				Log.d("debug:", s);
				mProgressBar.setVisibility(View.GONE);
			}
		});
	}
	
    private void getFromDB(){
    	mTweetsAdapter.clear();
    	mTweetsAdapter.addAll(Tweet.getAll());
    	mProgressBar.setVisibility(View.GONE);
    }
}
