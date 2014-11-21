package com.codepath.syed.basictwitter;



import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.syed.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.syed.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends FragmentActivity implements ComposeFragmentListener{//implements OnItemClickListener{

    private Tweet 			mTweet;
    private ImageView 		mIvRetweetedIcon;
    private TextView 		mTvRetweetBy;
    private ImageView 		mIvProfileImage;
    private TextView 		mTvUsername;
    private TextView 		mTvScreenname;
    private TextView 		mTvStatus;
    private TextView 		mTvCreatedAt;
    private ImageView 		mIvMedia;
    private TextView 		mTvRetweet;
    private TextView 		mTvFavCount;
    private ImageView 		mIvRetweet;
    private ImageView 		mIvFavCount;
    private TwitterClient 	client;

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
    		mTweet = (Tweet) getIntent().getParcelableExtra("tweet"); 
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	mIvRetweetedIcon = (ImageView)findViewById(R.id.ivRetweetedIcon);
    	mTvRetweetBy 	 = (TextView)findViewById(R.id.tvRetweetedBy);
    	mIvProfileImage  = (ImageView)findViewById(R.id.ivProfileImage);
    	mTvUsername 	 = (TextView)findViewById(R.id.tvUsername);
    	mTvScreenname 	 = (TextView)findViewById(R.id.tvScreenname);
    	mTvStatus 		 = (TextView)findViewById(R.id.tvStatus);
    	mTvCreatedAt 	 = (TextView)findViewById(R.id.tvCreatedAt);
    	mIvMedia 		 = (ImageView)findViewById(R.id.ivMedia);
    	mIvRetweet 		 = (ImageView)findViewById(R.id.ivRetweet);
        mTvRetweet 		 = (TextView)findViewById(R.id.tvRetweet);
        mIvFavCount 	 = (ImageView)findViewById(R.id.ivFavCount);
        mTvFavCount 	 = (TextView)findViewById(R.id.tvFavCount);

        if (mTweet.getRetweetedStatus() != null){
        	mIvRetweetedIcon.setVisibility(View.VISIBLE);
        	mTvRetweetBy.setText(mTweet.getUser().getName());
            mTvRetweetBy.setVisibility(View.VISIBLE);
            mTweet = mTweet.getRetweetedStatus();
        } else {
        	mIvRetweetedIcon.setVisibility(View.GONE);
        	mTvRetweetBy.setVisibility(View.GONE);
        }

        ImageLoader.getInstance().displayImage(mTweet.getUser().getProfileImageUrl(), mIvProfileImage);
        mTvUsername.setText(mTweet.getUser().getName());
        mTvScreenname.setText(mTweet.getUser().getScreenName());
        mTvStatus.setText(Html.fromHtml(mTweet.getBodyWithoutMediaUrl()));
        mTvStatus.setMovementMethod(LinkMovementMethod.getInstance());
        mTvCreatedAt.setText(Utility.getRelativeTimeAgo(mTweet.getCreatedAt()));

        if (mTweet.isRetweeted()){
        	mIvRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_on);
        } else {
        	mIvRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_off);
        }
        mTvRetweet.setText(String.valueOf(mTweet.getReTweetCount()));

        if (mTweet.isFavorited()){
        	mIvFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_on);
        } else {
        	mIvFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_off);
        }
        mTvFavCount.setText(String.valueOf(mTweet.getFavoriteCount()));

        if (mTweet.getTwitterMediaUrls() != null && mTweet.getTwitterMediaUrls().size() > 0){
        	mIvMedia.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(mTweet.getTwitterMediaUrls().get(0).getMediaUrlHttps(), mIvMedia);
        } else {
        	mIvMedia.setVisibility(View.GONE);
        }
    }

    public void replyTweet(View view) {
        String screenName = "@" + mTweet.getUser().getScreenName() + " ";
        ComposeTweetFragment composeFragment =  ComposeTweetFragment.newInstance(screenName, mTweet.getUid(), mTweet.getUser());
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
        if (mTweet.isRetweeted()) {
        	mIvRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_on);
        } else {
        	mIvRetweet.setImageResource(R.drawable.ic_tweet_action_inline_retweet_off);
        }
        mTvRetweet.setText(String.valueOf(mTweet.getReTweetCount()));
    }

    private void updateFavorites(){
        if (mTweet.isFavorited()) {
        	mIvFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_on);
        } else {
        	mIvFavCount.setImageResource(R.drawable.ic_tweet_action_inline_favorite_off);
        }
        mTvFavCount.setText(String.valueOf(mTweet.getFavoriteCount()));
    }

    public void onReTweetClick(View view) {
        if (!mTweet.isRetweeted()) {
            client.postRetweet(mTweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	mTweet = Tweet.fromJSON(jsonObject);
                    updateRetweet();
                }
            });
        }
    }

    public void onFavoriteClick(View view) {
        if (mTweet.isFavorited()){
            client.postFavoriteRemoved(mTweet.getUid(),new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	mTweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        } else {
            client.postFavoriteCreate(mTweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                	mTweet = Tweet.fromJSON(jsonObject);
                    updateFavorites();
                }
            });
        }
    }

    public void onProfileImageClick(View view) {
        Intent i = new Intent(this, UserProfileActivity.class);
        i.putExtra("user", mTweet.getUser());
        startActivity(i);
    }

	@Override
	public void onPostTweet(boolean bPosted, Tweet newTweet) {
		getSupportFragmentManager().executePendingTransactions();
		HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentByTag("home"); // home is tag here...
		if(bPosted && homeTimelineFragment!= null){
			homeTimelineFragment.insert(newTweet);
		}
	}
}
