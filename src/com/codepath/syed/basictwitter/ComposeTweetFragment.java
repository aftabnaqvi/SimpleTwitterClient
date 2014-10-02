package com.codepath.syed.basictwitter;


import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.syed.basictwitter.models.User;
import com.codepath.syed.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ComposeTweetFragment extends DialogFragment implements OnClickListener{

	private TextView tvUsername;
	private TextView tvScreenName;
	private EditText etTweet;
	private ImageView ivProfileImage;
	private TextView tvCharLeft;
	private int tweetCharCountLeft;
	private int totalCount;
	private TwitterClient client;
	private Button btnTweet;
	private Button btnCancel;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL,0);
	}

	private boolean bOptionsChanged = false;
	
	OnDataChangeEventListener dataChangeListener;
	public ComposeTweetFragment() {
		// Empty constructor required for DialogFragment
	}
	
	public static ComposeTweetFragment newInstance(String tweetReplyTo, User user) {
		ComposeTweetFragment fragment = new ComposeTweetFragment();
		Bundle args = new Bundle();
		args.putString("name", user.getName());
		args.putString("screen_name", user.getScreenName());
		args.putString("profile_image_url", user.getProfileImageUrl());
		args.putString("reply_to", tweetReplyTo);
		
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
		client = TwitterApplication.getRestClient();
		tweetCharCountLeft = 140;
		totalCount = 140;
		
		getDialog().setTitle(getResources().getString(R.string.new_tweet));
		setupViews(view);
		addListeners();
	
		return view;
	}

	private void setupViews(View v){
		tvUsername = (TextView)v.findViewById(R.id.tvUsername);
		tvScreenName = (TextView)v.findViewById(R.id.tvCurrentUserScreenName);
		etTweet = (EditText)v.findViewById(R.id.etTweet);
		ivProfileImage = (ImageView)v.findViewById(R.id.ivCurrentUserProfileImage);
		tvCharLeft = (TextView)v.findViewById(R.id.tvCharLeft);
		btnTweet = (Button)v.findViewById(R.id.btnTweet);
		btnCancel = (Button)v.findViewById(R.id.btnCancel);
		
		Bundle args = getArguments();
		if(args == null){
			Log.d("debug:", "args ae null...." );
			return;
		}
		
		if( tvUsername != null)
			tvUsername.setText(args.getString("name"));
		
		if(tvScreenName != null)
			tvScreenName.setText("@" + args.getString("screen_name"));
		
		if(ivProfileImage != null){
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(args.getString("profile_image_url"), ivProfileImage);
		}
		
		if(tvCharLeft != null){
			tvCharLeft.setText(String.valueOf(tweetCharCountLeft));
		}
		
		String replyTo = args.getString("reply_to");
		if(etTweet != null && !replyTo.isEmpty()){
			etTweet.setText(replyTo);
			tweetCharCountLeft = 140-replyTo.length();
			tvCharLeft.setText(String.valueOf(tweetCharCountLeft));
			etTweet.setSelection(replyTo.length());
		}
	}
	
	private void addListeners(){
		etTweet.addTextChangedListener(new TextWatcher() {

	   public void afterTextChanged(Editable s) { 
	   }
	
	   public void beforeTextChanged(CharSequence s, int start,
	     int count, int after) {
	   }
	
	   public void onTextChanged(CharSequence s, int start,
	     int before, int count) {
		   String tweet = etTweet.getText().toString();
		   tweetCharCountLeft = totalCount-tweet.length();
		   if(tweetCharCountLeft<0)
			   tvCharLeft.setTextColor(Color.RED);
		   else
			   tvCharLeft.setTextColor(Color.GRAY);
		   
		   tvCharLeft.setText(String.valueOf(tweetCharCountLeft));
	   }
	  });
		
		btnTweet.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}
	
	// ----------------- post the tweet
		private void postTweet(){
			if(!isDeviceConnected()){
				Toast.makeText(getDialog().getContext(), "Notwork is not available", Toast.LENGTH_SHORT);
				return;
			}
			String tweetText = etTweet.getText().toString();
			if(tweetText.isEmpty()){
				Toast.makeText(getDialog().getContext(), "Compose and then Tweet", Toast.LENGTH_SHORT).show();
				return;
			}
			
			client.postUpdateTweet(new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject json) {
					Log.d("debug:","user-->> posted!!!");
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug:",e.toString());
					Log.d("debug:",s.toString());
				}
			}, tweetText);
		}

		private void showConfirmationAlert(){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getDialog().getContext());
		    alertDialog.setTitle(getResources().getString(R.string.new_tweet));
		    alertDialog.setMessage(getResources().getString(R.string.tweet_discard_message));
		    alertDialog.setIcon(R.drawable.round_button_twitter);
		
		    // Setting Positive "Yes" Button
		    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,int which) {
		
		        	dismiss();// closing this activity.
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
		
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			dataChangeListener = (OnDataChangeEventListener) activity;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(activity.toString() + " must implement OnDataChangeEventListener");
	    }
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnTweet:
        	postTweet();
        	dataChangeListener.dataChangeEvent("dummy");
        	dismiss();
            break;
        case R.id.btnCancel:
        	String tweetText = etTweet.getText().toString();
        	if(tweetText.length()>0){
        		showConfirmationAlert();
        	} else {
        		dismiss();
        	}
        	break;
        }
    }
	
	public boolean isChanged(){
		return bOptionsChanged;
	}
	
	// Network handling helpers
    private boolean isDeviceConnected(){
    	if(Utility.isNetworkAvailable(getDialog().getContext()) == Boolean.FALSE){
        	return false; 
        }
    	
    	return true;
    }
}
