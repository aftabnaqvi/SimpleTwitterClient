package com.codepath.syed.basictwitter;

import com.codepath.syed.basictwitter.models.Tweet;

public interface ComposeFragmentListener {
	public void onPostTweet(boolean bPosted, Tweet newTweet);
}
