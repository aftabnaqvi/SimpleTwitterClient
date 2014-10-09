package com.codepath.syed.basictwitter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.codepath.syed.basictwitter.fragments.SearchResultFragment;


public class SearchResultActivity extends FragmentActivity {
	private String query;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		query = getIntent().getStringExtra("query");
        SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(query);
        getActionBar().setTitle("Search: " + query);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flSearchResults, searchResultFragment);
        ft.commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
