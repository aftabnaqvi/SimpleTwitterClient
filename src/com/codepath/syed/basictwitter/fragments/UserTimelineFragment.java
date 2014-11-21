package com.codepath.syed.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetListFragment {
    private static final String TAG = UserTimelineFragment.class.getName();
    private User user;
    private int resultCount;
    
    public static UserTimelineFragment newInstance(User user, int resultCount){
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putInt("result_count", resultCount);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Override
    protected void populateTimeline(boolean refresh) {
    	// Check if we have already fetch tweets once.
        // if we already fetched than fetch the next set of tweets
        // from (max_id - 1). max_id is inclusive so you need decrement it one.
        if (mTweets.size() > 0){
        	mLastTweetId = String.valueOf(mTweets.get(mTweets.size() - 1).getUid() - 1) ;
        }
        
        if(refresh == true){
        	mTweets.clear();
			mTweetsAdapter.clear();
			mLastTweetId = null;
        }
        
        long userId = 0;
        if (user != null){
            userId = user.getUid();
        }
        mTwitterClient.getUserTimeline(userId, mLastTweetId, resultCount, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                addAll(Tweet.fromJSONArray(jsonArray));
                mLvTweets.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                Log.d(TAG, throwable.toString());
                Log.d(TAG, s);
            }
        });
    }
}

