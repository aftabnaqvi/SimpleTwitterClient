package com.codepath.syed.basictwitter;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends FragmentActivity implements OnItemClickListener{

    private Tweet tweet;
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

    private final int REQUEST_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        setupViews();
    }

    private void setupViews() {
        //tweet = (Tweet) getIntent().getParcelableExtra("tweet"); - still working on it.

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
            ImageLoader.getInstance().displayImage(tweet.getTwitterMediaUrls().get(0).getMedia_url_https(),ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
        } else {
            ivMedia.setVisibility(View.GONE);
        }

    }


    public void replyTweet(View view) {
        //Adding extra space at end for user convenience
        long uid = tweet.getUid();
        String screenName = tweet.getUser().getScreenName() + " ";
        ComposeTweetFragment composeFragment =  ComposeTweetFragment.newInstance(screenName, tweet.getUser());
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

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

//    @Override
//    public void onPostTweet(boolean success, Tweet newTweet) {
//
//    }
}
