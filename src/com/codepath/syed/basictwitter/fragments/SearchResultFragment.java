package com.codepath.syed.basictwitter.fragments;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.syed.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchResultFragment extends TweetListFragment {
	private String mQuery;
	
	public SearchResultFragment(){
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mQuery = args.getString("query");
	}

	protected void populateTimeline(boolean bRefresh) {
		if( !isDeviceConnected() ){
			return;
		}
        // Check if we have already fetch tweets once.
        // if we already fetched than fetch the next set of tweets
        // from (max_id - 1). max_id is inclusive so you need decrement it one.
        if (mTweets.size() > 0){
        	mLastTweetId = String.valueOf(mTweets.get(mTweets.size() - 1).getUid() - 1) ;
        }
        
		if(bRefresh){
			mTweets.clear();
			mTweetsAdapter.clear();
			mLastTweetId = null;
		}
		
		mTwitterClient.getSearchTweets(mQuery, mLastTweetId, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				if (!jsonObject.isNull("statuses")){
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("statuses");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addAll(Tweet.fromJSONArray(jsonArray));
                    mProgressBar.setVisibility(View.GONE);
	                for (Tweet tweet : mTweets) {
	                    tweet.saveTweet();
	                }
				}
				mLvTweets.onRefreshComplete(); 
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:", e.toString());
				Log.d("debug:", s);
				mProgressBar.setVisibility(View.GONE);
			}
		});
	}

	public static SearchResultFragment newInstance(String query) {
		SearchResultFragment searchResultFragment = new SearchResultFragment();
		Bundle args = new Bundle();
		args.putString("query", query);
		searchResultFragment.setArguments(args);
		return searchResultFragment;
	}
}
