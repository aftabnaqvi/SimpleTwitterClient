package com.codepath.syed.basictwitter;

import java.net.URLEncoder;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "0xdICU8KFX3Q6F69TxnGwPq66";       // Change this
	public static final String REST_CONSUMER_SECRET = "b1A8VQzpDxQigj8IRAk4pV5469aLCpOpeqOGBDpBI1XeACXd4Q"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(String lastTweetId, AsyncHttpResponseHandler handler){
		String homelineUrl = getApiUrl("statuses/home_timeline.json");
		
		RequestParams params = new RequestParams();
		if(lastTweetId != null){
			params.put("max_id", lastTweetId);
		}
		
		params.put("since_id", "1");
		client.get(homelineUrl, params, handler); // if you don't have any params, pass null.
	}
	
	// getting current user account details.
	public void getAccountDetails(AsyncHttpResponseHandler handler){
		String accountDetailsURL = getApiUrl("account/verify_credentials.json");
		client.get(accountDetailsURL, null, handler);
	}

	public void postUpdateTweet(String status, long inReplyId, AsyncHttpResponseHandler handler){
		String tweetUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams("status", status);
		params.put("status",status);
		if (inReplyId != 0) {
            params.put("in_reply_to_status_id", Long.toString(inReplyId));
        }
		
		Log.d("debug:","post url: "+tweetUrl + " " + params);
		client.post(tweetUrl, params, handler);
	}

	public void getMentionsTimeline(String lastTweetId,
			JsonHttpResponseHandler handler) {
		String homelineUrl = getApiUrl("statuses/mentions_timeline.json");
		
		RequestParams params = new RequestParams();
		if(lastTweetId != null){
			params.put("max_id", lastTweetId);
		}
		
		params.put("since_id", "1");
		client.get(homelineUrl, params, handler); // if you don't have any params, pass null.
	}
	
	// getting requested user account details.
	public void getUserProfileInfo(String username, JsonHttpResponseHandler handler){
		String accountDetailsURL = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", username);
		client.get(accountDetailsURL, params, handler);
	}
	
	// getting requested user account details.
	public void getUserProfileBanner(String username, JsonHttpResponseHandler handler){
		String accountBannerURL = getApiUrl("users/profile_banner.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", username);
		client.get(accountBannerURL, params, handler);
	}
	
	// pass count = 0 if we want default results
	public void getUserTimeline(long user_id, String lastTweetId, int count, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("since_id", "1");
        if (lastTweetId != null) {
            params.put("max_id", lastTweetId);
        }
        if (user_id > 0) {
            params.put("user_id", Long.toString(user_id));
        }
        if(count == 3)
        	params.put("count", Integer.toString(count));
        
        client.get(apiUrl, params, handler);
    }
	
    public void postFavoriteCreate(long uid, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", Long.toString(uid));
        client.post(apiUrl, params, handler);
    }

    public void postFavoriteRemoved(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", Long.toString(uid));
        client.post(apiUrl, params, handler);
    }

    public void postRetweet(long uid, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/" + uid + ".json");
        client.post(apiUrl, handler);
    }

    public void postStatusDestroy(long uid, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/destroy/" + uid + ".json");
        client.post(apiUrl, handler);
    }

    public void getSearchTweets(String query, String lastTweetId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if (query != null) {
            params.put("q", URLEncoder.encode(query));
        }
        if (lastTweetId !=  null) {
            params.put("max_id", lastTweetId);
        }
        client.get(apiUrl, params, handler);
    }
    
    public void getFriendsList(long uid, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", Long.toString(uid));
        client.get(apiUrl,params,handler);
    }

    public void getFollowersList(long uid, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", Long.toString(uid));
        client.get(apiUrl,params,handler);
    }
    //https://api.twitter.com/1.1/direct_messages/new.json
    public void postDirectMessage(long uid, JsonHttpResponseHandler handler){
    	// may check the uid before sending out...
    	String apiUrl = getApiUrl("direct_messages/new.json");
        RequestParams params = new RequestParams();
        params.put("user_id", Long.toString(uid));
        client.post(apiUrl,params,handler);
    }
		
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	/*public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}*/

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}