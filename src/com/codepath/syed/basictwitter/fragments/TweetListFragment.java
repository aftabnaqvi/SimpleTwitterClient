package com.codepath.syed.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.syed.basictwitter.EndlessScrollListener;
import com.codepath.syed.basictwitter.R;
import com.codepath.syed.basictwitter.TweetArrayAdapter;
import com.codepath.syed.basictwitter.TweetDetailActivity;
import com.codepath.syed.basictwitter.TwitterApplication;
import com.codepath.syed.basictwitter.TwitterClient;
import com.codepath.syed.basictwitter.UserProfileActivity;
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
	//protected  long max_id;
	protected  User user;
	protected  static int REQUEST_CODE = 200;
	protected boolean requestFetchDataInProgress;
	private TextView selectedTvRetweet;
	private TextView selectedTvFavorite;
	protected ProgressBar progressBar;
	private Tweet selectedTweet;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		requestFetchDataInProgress = false;
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), tweets); // use getActivity very carefully... 
		
		 setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate the layout
		View view =	inflater.inflate(R.layout.fragment_tweet_list, container, false); // false mean, don't attach the view but do it later.
		
		// Assign our view references		
		lvTweets = (PullToRefreshListView)view.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		//getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.mobile_banner));
		
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
		
		//OnItemClickListener
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			     	long viewId = view.getId();
			     	final Tweet tweet = (Tweet)aTweets.getItem(position+1); // I don't know why position is coming as -1. but it is working for now.
			         if (viewId == R.id.tvReply) {
			         	
			             Log.d("debug:", "tvReply Clicked");
			             if(tweet!=null){
			            	 user = tweet.getUser();
			            	 showComposeTweetFragment("@"+tweet.getUser().getScreenName()+" ", tweet.getUid());
			             }
			         } else if (viewId == R.id.tvRetweet) {
			        	 if(tweet!=null){
			        		 showRetwwetConfirmationAlert(tweet.isRetweeted() ? true : false);
			        		 selectedTvRetweet = (TextView)view.findViewById(R.id.tvRetweet);			        		 
			        		 selectedTweet = tweet;
			        		// onReTweetClick(selectedTvRetweet);
			        	 }
			         	Log.d("debug:", "tvRetweet Clicked");
			         	
			         } else if(viewId == R.id.tvFavorite){
			        	 if(tweet!=null){
			        		 selectedTvFavorite = (TextView)view.findViewById(R.id.tvFavorite);
			        		 
			        		 onFavoriteClick(selectedTvFavorite);
			        		 //favorite(tweet, tvFavorite);
			        	 }
			        	 Log.d("debug:", "tvFavorite Clicked");
			         } else if(viewId == R.id.ivProfileImage){
			        	 Intent i = new Intent(getActivity(), UserProfileActivity.class);
			             i.putExtra("user", tweet.getUser());
			             startActivity(i);
			         } else {
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
	
	private void showRetwwetConfirmationAlert(boolean retweet){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
	    alertDialog.setTitle(getResources().getString(R.string.retweet_title));
	    alertDialog.setMessage(getResources().getString(retweet ? R.string.undo_this_tweet : R.string.retweet_this_to_your_followers));
	
	    // Setting Positive "Yes" Button
	    alertDialog.setPositiveButton(retweet ? R.string.undo : R.string.retweet, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog,int which) {
	        	//reTweet();
	        	onReTweetClick(null);
	        }
	    });
	
	    // Setting Negative "NO" Button
	    alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {	        
	        dialog.cancel();
	        }
	    });

	    // Showing Alert Message
	    alertDialog.show();
	}
	
	private void updateRetweet(){
        if (selectedTweet.isRetweeted()) {
            selectedTvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
        } else {
        	selectedTvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
        }
        selectedTvRetweet.setText(String.valueOf(selectedTweet.getReTweetCount()));
    }

    private void updateFavorites(){
        if (selectedTweet.isFavorited()) {
        	selectedTvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
        } else {
        	selectedTvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
        }
        selectedTvFavorite.setText(String.valueOf(selectedTweet.getFavoriteCount()));

    }
    
	public void onReTweetClick(View view) {
        if (!selectedTweet.isRetweeted()) {
            client.postRetweet(selectedTweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	selectedTweet = Tweet.fromJSON(jsonObject);
                    updateRetweet();
                }
            });

        }
    }

    public void onFavoriteClick(View view) {

        if (selectedTweet.isFavorited()){
            client.postFavoriteRemoved(selectedTweet.getUid(), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	selectedTweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        } else {
            client.postFavoriteCreate(selectedTweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	selectedTweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        }
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
    
    protected void showComposeTweetFragment(String tweetReplyTo, long inReplyId){
    	FragmentManager fm = getActivity().getSupportFragmentManager();
    	ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetReplyTo, inReplyId, user);
    	fragment.show(fm, "compose_tweet_fragment");
    }
    
	// Delegate the adding to the internal adapter. // most recommended approach... minimize the code... 
	// 
	public void addAll(ArrayList<Tweet> tweets){
		aTweets.addAll(tweets);
	}
	
	public void insert(Tweet newTweet){
		aTweets.insert(newTweet, 0);
		newTweet.save();
	}
	
	protected abstract void populateTimeline(boolean b);
}
