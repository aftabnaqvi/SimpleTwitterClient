package com.codepath.syed.basictwitter.fragments;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.codepath.syed.basictwitter.R;
import com.codepath.syed.basictwitter.ViewProfileActivity;
import com.codepath.syed.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsTimelineFragment extends TweetListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		populateTimeline(false);
	}
	
	protected void populateTimeline(boolean bRefresh) {
		if( !isDeviceConnected() ){
			getFromDB();
			return;
		}
		
		if(requestFetchDataInProgress == true){
			Log.i("debug: ", "A request to fetch data from network resource is already in progress.");
			return;
		}
		if(bRefresh){
			tweets.clear();
			aTweets.clear();
			lastTweetId = null;
		}
		requestFetchDataInProgress = true;
		client.getMentionsTimeline(lastTweetId, new JsonHttpResponseHandler(){

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
                requestFetchDataInProgress = false;
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
	
    private void getFromDB(){
        aTweets.clear();
        aTweets.addAll(Tweet.getAll());
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
	      
	    case R.id.view_profile:
	    	Intent i = new Intent(getActivity(), ViewProfileActivity.class);
	    	i.putExtra("user", currentUser);
	    	startActivity(i);
	    	break;

	    default:
	      break;
	    }

	    return true;
	}
}
