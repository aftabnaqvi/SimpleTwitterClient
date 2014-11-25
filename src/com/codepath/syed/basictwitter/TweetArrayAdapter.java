package com.codepath.syed.basictwitter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import eu.erikw.PullToRefreshListView;

class BitmapScaler
{
	// Scale and maintain aspect ratio given a desired width
	// BitmapScaler.scaleToFitWidth(bitmap, 100);
	public static Bitmap scaleToFitWidth(Bitmap b, int width)
	{
		float factor = width / (float) b.getWidth();
		return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
	}


	// Scale and maintain aspect ratio given a desired height
	// BitmapScaler.scaleToFitHeight(bitmap, 100);
	public static Bitmap scaleToFitHeight(Bitmap b, int height)
	{
		float factor = height / (float) b.getHeight();
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
	}
}

public class TweetArrayAdapter extends ArrayAdapter<Tweet>{

	public TweetArrayAdapter(Context context, List<Tweet> objects) {
		super(context, R.layout.tweet_item, objects);
	}
	
	public static class ViewHolder{
		private ImageView	ivProfileImage;
		private TextView	tvName;
		private TextView	tvScreenName;
		private TextView	tvTime;
		private TextView	tvTweetBody;
		private TextView 	tvReply;
		private TextView	tvRetweet;
		private TextView	tvFavorite;
		private ImageView	ivTweetImage;
		private ProgressBar	ivTweetImageProgressBar;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// Get the data from position.
		final Tweet tweet = getItem(position);
		
		ViewHolder viewHolder =  null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_item, parent, false);
			viewHolder.ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
			viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
			viewHolder.tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
			viewHolder.tvTweetBody = (TextView)convertView.findViewById(R.id.tvTweetBody);
			viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tvTime);
			viewHolder.tvRetweet = (TextView)convertView.findViewById(R.id.tvRetweet);
			viewHolder.tvFavorite = (TextView)convertView.findViewById(R.id.tvFavorite); 
			viewHolder.tvReply = (TextView)convertView.findViewById(R.id.tvReply);
			viewHolder.ivTweetImage = (ImageView)convertView.findViewById(R.id.ivTweetImage);
			viewHolder.ivTweetImageProgressBar = (ProgressBar)convertView.findViewById(R.id.imageProgressBar);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
			
		}	
		
		updateTweetVew(viewHolder, tweet);
		viewHolder.tvReply.setOnClickListener(new OnClickListener() {
			 
		    @Override
		    public void onClick(View v) {
		        ((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    }
		});
		
		viewHolder.tvRetweet.setOnClickListener(new OnClickListener() {
			 
		    @Override
		    public void onClick(View v) {
		    	((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    }
		});
		
		viewHolder.tvFavorite.setOnClickListener(new OnClickListener() {
			 
		    @Override
		    public void onClick(View v) {
		    	((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    }
		});
		
		viewHolder.ivProfileImage.setOnClickListener(new OnClickListener() {
			 
		    @Override
		    public void onClick(View v) {
		    	v.setTag(tweet.getUser().getScreenName());
		    	((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    }
		});
		
		return convertView;
	}
	
	private void updateTweetVew(ViewHolder viewHolder, Tweet tweet){
		if(viewHolder == null || tweet == null){
			Log.e("error", "Invalid Arguments");
			return;
		}
	
		if(viewHolder.ivProfileImage != null){
			// loading user profile image via its URL into imageView directly.
			viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
			display(viewHolder.ivProfileImage, tweet.getUser().getProfileImageUrl(), null);
		}
		
		if(viewHolder.tvName != null){
			SpannableString formattedUsername =  new SpannableString(tweet.getUser().getName());
			formattedUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, formattedUsername.length(), 0); 
			viewHolder.tvName.setText("");
			viewHolder.tvName.append(formattedUsername);
			viewHolder.tvName.append(" ");
		}
		
		if(viewHolder.tvTweetBody != null){
			viewHolder.tvTweetBody.setText(Html.fromHtml(tweet.getBodyWithoutMediaUrl()));
		}	
		
		if (tweet.getTwitterMediaUrls() != null && tweet.getTwitterMediaUrls().size() > 0){
			viewHolder.ivTweetImage.setVisibility(View.VISIBLE);
			display(viewHolder.ivTweetImage, tweet.getTwitterMediaUrls().get(0).getMediaUrlHttps(), viewHolder.ivTweetImageProgressBar);
            
        } else {
            viewHolder.ivTweetImage.setVisibility(View.GONE);
        }
		
		if(viewHolder.tvTime != null){
			String tweetTime = Utility.getRelativeTimeAgo(tweet.getCreatedAt());
			viewHolder.tvTime.setText(tweetTime);
		}
		
		if(viewHolder.tvRetweet != null){
			if (tweet.isRetweeted()){
				viewHolder.tvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
			} else {
				viewHolder.tvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
			}
			
			viewHolder.tvRetweet.setText(" "+tweet.getReTweetCount());
		}
		
		if(viewHolder.tvFavorite != null){
			if (tweet.isFavorited()){
				viewHolder.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
			} else {
				viewHolder.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
			}
			viewHolder.tvFavorite.setText(" "+tweet.getFavoriteCount());
		}
		
		if(viewHolder.tvScreenName != null){
			viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		}
	}
	
	public void display(ImageView img, String url, final ProgressBar spinner)
	{
		ImageLoader.getInstance().displayImage(url, img, new ImageLoadingListener(){
	
	        @Override
	        public void onLoadingStarted(String imageUri, View view) {
	        	if(spinner != null)
	        		spinner.setVisibility(View.VISIBLE); // set the spinner visible
	        }
	        
	        @Override
	        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
	        	if(spinner != null)
	        		spinner.setVisibility(View.GONE); // set the spinenr visibility to gone
	        }
	        
	        @Override
	        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	        	if(spinner != null)
	        		spinner.setVisibility(View.GONE); //  loading completed set the spinne m[p 8978 p 5p b84etttttttttyetw4r visibility to gone
	        	
	        	int width=0;
	        	if( view.getWidth() == 0){
	        		width = view.getMeasuredWidth();
	        	}else{
	        		width = view.getWidth();
	        	}
	        	if( width == 0){
	        		width = loadedImage.getWidth();
	        	}
	         
	        	int height=0;
	        	if( view.getHeight() == 0){
	        		height = view.getMeasuredHeight();
	        	}else{
	        		height = view.getHeight();
	        	}
	        	if( height == 0 ){
	        		height = loadedImage.getHeight();
	        	}
	        	BitmapScaler.scaleToFitHeight(loadedImage, height);
	        	BitmapScaler.scaleToFitWidth(loadedImage, width);
	        }
	        
	        @Override
	        public void onLoadingCancelled(String imageUri, View view) {
	        }
		});
	}
}