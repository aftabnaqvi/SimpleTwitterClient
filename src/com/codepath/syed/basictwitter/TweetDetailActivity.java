package com.codepath.syed.basictwitter;



import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.syed.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.syed.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.syed.basictwitter.fragments.UserTimelineFragment;
import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.basictwitter.models.User;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends FragmentActivity implements ComposeFragmentListener{//implements OnItemClickListener{

    private Tweet tweet;
    private User user;
    private ImageView ivRetweetedIcon;
    private TextView tvRetweetBy;
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvScreenname;
    private TextView tvStatus;
    private TextView tvCreatedAt;
    private ImageView ivMedia;
    private TextView tvRetweet;
    private TextView tvFavCount;
    private ImageView ivRetweet;
    private ImageView ivFavCount;
    private TwitterClient client;
    private final int REQUEST_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        setupViews();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        client = TwitterApplication.getRestClient();
    }

    private void setupViews() {
    	try{
    		tweet = (Tweet) getIntent().getParcelableExtra("tweet"); //- still working on it.
    	} catch(Exception e){
    		e.printStackTrace();
    	}
        ivRetweetedIcon = (ImageView)findViewById(R.id.ivRetweetedIcon);
        tvRetweetBy = (TextView)findViewById(R.id.tvRetweetedBy);
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvScreenname = (TextView)findViewById(R.id.tvScreenname);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvCreatedAt = (TextView)findViewById(R.id.tvCreatedAt);
        ivMedia = (ImageView)findViewById(R.id.ivMedia);
        ivRetweet = (ImageView)findViewById(R.id.ivRetweet);
        tvRetweet = (TextView)findViewById(R.id.tvRetweet);
        ivFavCount = (ImageView)findViewById(R.id.ivFavCount);
        tvFavCount = (TextView)findViewById(R.id.tvFavCount);

        if (tweet.getRetweetedStatus() != null){
            ivRetweetedIcon.setVisibility(View.VISIBLE);
            tvRetweetBy.setText(tweet.getUser().getName());
            tvRetweetBy.setVisibility(View.VISIBLE);
            tweet = tweet.getRetweetedStatus();
        } else {
            ivRetweetedIcon.setVisibility(View.GONE);
            tvRetweetBy.setVisibility(View.GONE);

        }

        ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
        tvUsername.setText(tweet.getUser().getName());
        tvScreenname.setText(tweet.getUser().getScreenName());
        tvStatus.setText(Html.fromHtml(tweet.getBodyWithoutMediaUrl()));
        tvStatus.setMovementMethod(LinkMovementMethod.getInstance());
        tvCreatedAt.setText(Utility.getRelativeTimeAgo(tweet.getCreatedAt()));

        if (tweet.isRetweeted()){
            ivRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_on);
        } else {
            ivRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_off);
        }
        tvRetweet.setText(String.valueOf(tweet.getReTweetCount()));

        if (tweet.isFavorited()){
            ivFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_on);
        } else {
            ivFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_off);
        }
        tvFavCount.setText(String.valueOf(tweet.getFavoriteCount()));

        if (tweet.getTwitterMediaUrls() != null && tweet.getTwitterMediaUrls().size() > 0){
        	ivMedia.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(tweet.getTwitterMediaUrls().get(0).getMediaUrlHttps(), ivMedia);
            
        } else {
            ivMedia.setVisibility(View.GONE);
        }

    }

    public void replyTweet(View view) {
        String screenName = "@" + tweet.getUser().getScreenName() + " ";
        ComposeTweetFragment composeFragment =  ComposeTweetFragment.newInstance(screenName, tweet.getUid(), tweet.getUser());
        composeFragment.show(getSupportFragmentManager(),"compose_fragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			break;
		default:
			break;
		}
		return true;
    }

	private void updateRetweet(){
        if (tweet.isRetweeted()) {
            ivRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_on);
        } else {
            ivRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_off);
        }
        tvRetweet.setText(String.valueOf(tweet.getReTweetCount()));
    }

    private void updateFavorites(){
        if (tweet.isFavorited()) {
            ivFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_on);
        } else {
            ivFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_off);
        }
        tvFavCount.setText(String.valueOf(tweet.getFavoriteCount()));
    }

    public void onReTweetClick(View view) {
        if (!tweet.isRetweeted()) {
            client.postRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    tweet = Tweet.fromJSON(jsonObject);
                    updateRetweet();
                }
            });

        }
    }

    public void onFavoriteClick(View view) {

        if (tweet.isFavorited()){
            client.postFavoriteRemoved(tweet.getUid(),new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    tweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        } else {
            client.postFavoriteCreate(tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    tweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        }
    }

    public void onProfileImageClick(View view) {
        Intent i = new Intent(this, UserProfileActivity.class);
        i.putExtra("user", tweet.getUser());
        startActivity(i);
    }

	@Override
	public void onPostTweet(boolean bPosted, Tweet newTweet) {
		getSupportFragmentManager().executePendingTransactions();
		HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentByTag("HomeTimelineFragment");
		if(bPosted && homeTimelineFragment!= null){
			homeTimelineFragment.insert(newTweet);
		}
	}
}
