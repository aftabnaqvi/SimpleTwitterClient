package com.codepath.syed.basictwitter;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetActivity extends Activity {
	
	private TextView 		mTvUsername;
	private TextView 		mTvScreenName;
	private EditText 		mEtTweet;
	private ImageView 		mIvProfileImage;
	private int 			mTweetCharCountLeft;
	private int 			mTotalCount;
	private TwitterClient 	mTwitterClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_compose_tweet);
		mTwitterClient = TwitterApplication.getRestClient();
		setupViews();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		addListeners();
		mTweetCharCountLeft = 140;
		mTotalCount = 140;
		invalidateOptionsMenu();
	}

	private void setupViews(){
		mTvUsername = (TextView)findViewById(R.id.tvUsername);
		mTvScreenName = (TextView)findViewById(R.id.tvCurrentUserScreenName);
		mEtTweet = (EditText)findViewById(R.id.etTweet);
		mIvProfileImage = (ImageView)findViewById(R.id.ivCurrentUserProfileImage);
		
	 	String userName = null;
	 	String screenName = null;
	 	String profileImageUrl = null;
	 	
	 	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//getPreferences(MODE_PRIVATE);
		if(preferences != null){
			userName = preferences.getString("name", "");
			screenName = preferences.getString("screen_name", "");
			profileImageUrl = preferences.getString("profile_image_url", "");
		} else {
			Log.e("ERORR:", "preferences object is NULL");
		}
		
		if( mTvUsername != null)
			mTvUsername.setText(userName);
		
		if(mTvScreenName != null)
			mTvScreenName.setText("@" + screenName);
		
		if(mIvProfileImage != null){
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(profileImageUrl, mIvProfileImage);
		}
	}
	
	private void addListeners(){
		mEtTweet.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { 
			}
		
			public void beforeTextChanged(CharSequence s, int start,
				int count, int after) {
			}
		
			public void onTextChanged(CharSequence s, int start,
				int before, int count) {
				//Log.d("debug:", "str: " + s + " start: " + start + " before: " + before + " count: " + count);
				String tweet = mEtTweet.getText().toString();
				mTweetCharCountLeft = mTotalCount-tweet.length();;
				invalidateOptionsMenu();
			}
		});
	}

	// menus...
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem tweetCharCount = menu.findItem(R.id.tweet_char_left);
		tweetCharCount.setTitle(String.valueOf(mTweetCharCountLeft));
		MenuItem tweet = menu.findItem(R.id.tweet);
		if(mTweetCharCountLeft<0){
			tweet.setEnabled(false);
		} else {
			tweet.setEnabled(true);
		}
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_compose_tweet_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.tweet:
	    	postTweet();
	    	break;
	    case android.R.id.home:
	    	String tweet = mEtTweet.getText().toString();
	    	if(tweet.length()>1){
	    		// show an alert to discard the tweet.
	    		showConfirmationAlert();
	    	} else {
			this.finish();
	    	}
			break;
	    default:
	    	break;
	    }
		
		return true;
	}

	// ----------------- post a Tweet
	private void postTweet(){
		String tweetText = mEtTweet.getText().toString();
		if(tweetText.isEmpty()){
			Toast.makeText(getBaseContext(), "Compose and then Tweet", Toast.LENGTH_SHORT).show();
			return;
		}
		mTwitterClient.postUpdateTweet(tweetText, 0, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("debug:","user-->> posted!!!");
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug:",e.toString());
				Log.d("debug:",s.toString());
			}
		});
		
		Intent i = new Intent();
		i.putExtra("tweet", tweetText);
		setResult(RESULT_OK,i);
		finish();
	}

	@Override
	public void finish() {
		super.finish();
	}

	private void showConfirmationAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	    alertDialog.setTitle(getResources().getString(R.string.new_tweet));
	    alertDialog.setMessage(getResources().getString(R.string.tweet_discard_message));
	    alertDialog.setIcon(R.drawable.round_button_twitter);
	
	    // Setting Positive "Yes" Button
	    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog,int which) {
	
	        finish(); // closing this activity.
	        }
	    });
	
	    // Setting Negative "NO" Button
	    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {	        
	        dialog.cancel();
	        }
	    });

	    // Showing Alert Message
	    alertDialog.show();
	}
}
