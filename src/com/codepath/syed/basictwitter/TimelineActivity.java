package com.codepath.syed.basictwitter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.codepath.syed.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.syed.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.syed.basictwitter.listeners.FragmentTabListener;
// should use FragmentActivity or ActionBarActivity

public class TimelineActivity extends FragmentActivity implements OnDataChangeEventListener{//, OnItemClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab home = actionBar
			.newTab()
			.setText("Home")
			.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home",
						HomeTimelineFragment.class));

		actionBar.addTab(home);
		actionBar.selectTab(home); // default selection

		Tab mentions = actionBar
			.newTab()
			.setText("Mentions")
			.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions",
			    		MentionsTimelineFragment.class));

		actionBar.addTab(mentions);
	}

	@Override
	public void dataChangeEvent(String s) {
		// TODO Auto-generated method stub
		
	}

	

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//		long id) {
//     	long viewId = view.getId();
//     	 
//         if (viewId == R.id.tvReply) {
//         	final Tweet tweet = (Tweet)aTweets.getItem(position+1); // I don't know why position is coming as -1. but it is working for now.
//             Log.d("debug:", "tvReply Clicked");
//             if(tweet!=null)
//             	showComposeTweetFragment("@"+tweet.getUser().getScreenName()+" ");
//         } else if (viewId == R.id.tvRetweet) {
//         	Log.d("debug:", "tvRetweet Clicked");
//         	
//         } else {
////             	TweetDetailActivity detailActivity
//         	Intent i = new Intent(this, TweetDetailActivity.class);
//         	startActivityForResult(i, RESULT_OK);
//         }
//     }

}
