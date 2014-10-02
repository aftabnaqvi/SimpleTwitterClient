package com.codepath.syed.basictwitter;

import java.util.List;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.syed.basictwitter.models.Tweet;
import com.codepath.syed.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

import eu.erikw.PullToRefreshListView;

public class TweetArrayAdapter extends ArrayAdapter<Tweet>{

	public TweetArrayAdapter(Context context, List<Tweet> objects) {
		super(context, R.layout.tweet_item, objects);
	}
	
	public static class ViewHolder{
		private ImageView	ivProfileImage;
		private TextView	tvName;
		public TextView		tvScreenName;
		private TextView	tvTime;
		private ImageView	ivTime;
		private TextView	tvTweetBody;
		private ImageView	ivReply;
		private TextView 	tvReply;
		private TextView	tvRetweet;
		private TextView	tvFavorite;
		private ImageView	ivTweetImage;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
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
		    	if(!tweet.isRetweeted()){
		    		tweet.setRetweeted(true);
		    		((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    	}
		    }
		});
		
		viewHolder.tvFavorite.setOnClickListener(new OnClickListener() {
			 
		    @Override
		    public void onClick(View v) {
		    	if(!tweet.isFavorited()){
		    		tweet.setFavorited(true);
		    		((PullToRefreshListView) parent).performItemClick(v, position, 0);
		    	}
		    }
		});
		
//		viewHolder.tvReply.setOnHoverListener(new OnHoverListener() {
//			 
//		    @Override
//		    public boolean onHover(View v, MotionEvent event) {
//	            switch (event.getAction()) {
//	                case MotionEvent.ACTION_HOVER_ENTER:
//	                	((PullToRefreshListView) parent).performItemClick(v, position, 0);
//	                    break;
////	                case MotionEvent.ACTION_HOVER_MOVE:
////	                    mMessageTextView.setText(Hover.this.getResources().getString(
////	                            R.string.hover_message_moved_at,
////	                            event.getX(), event.getY()));
////	                    break;
////	                case MotionEvent.ACTION_HOVER_EXIT:
////	                    mMessageTextView.setText(Hover.this.getResources().getString(
////	                            R.string.hover_message_exited_at,
////	                            event.getX(), event.getY()));
//	                   //break;
//	            }
//	            return false;
//	            //break;
//	        }
//		});
		
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
			ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(tweet.getTwitterMediaUrls().get(0).getMedia_url_https(),viewHolder.ivTweetImage);
            viewHolder.ivTweetImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivTweetImage.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)viewHolder.tvReply.getLayoutParams();
//            p.topMargin = viewHolder.tvTweetBody.getHeight() +  viewHolder.tvScreenName.getHeight() + 10; // in PX
//
//            //p.addRule(RelativeLayout.BELOW);
////            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
////                    ViewGroup.LayoutParams.WRAP_CONTENT);
////
////            p.addRule(RelativeLayout.BELOW, R.id.etTweet);
////
////            viewHolder.tvReply.setLayoutParams(p);
           
            
        }
		
		if(viewHolder.tvTime != null){
			String tweetTime = Utility.getRelativeTimeAgo(tweet.getCreatedAt());
			viewHolder.tvTime.setText(tweetTime);
		}
		
		if(viewHolder.tvRetweet != null){
			if (tweet.isRetweeted()){
				viewHolder.tvRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
				viewHolder.tvRetweet.setText(" "+tweet.getReTweetCount());
			} else {
				viewHolder.tvRetweet.setText(" "+tweet.getReTweetCount());
			}
		}
		
		if(viewHolder.tvFavorite != null){
			if (tweet.isFavorited()){
				viewHolder.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
				viewHolder.tvFavorite.setText(" "+tweet.getReTweetCount());
			} else {
				viewHolder.tvFavorite.setText(" "+tweet.getFavoriteCount());
			}
		}
		
		if(viewHolder.tvScreenName != null){
			viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		}
	}
}