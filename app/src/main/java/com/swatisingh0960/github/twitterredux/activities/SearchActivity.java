package com.swatisingh0960.github.twitterredux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.swatisingh0960.github.twitterredux.R;
import com.swatisingh0960.github.twitterredux.adapters.ContactsAdapter;
import com.swatisingh0960.github.twitterredux.adapters.SmartFragmentStatePagerAdapter;
import com.swatisingh0960.github.twitterredux.fragments.ComposeDialogFragment;
import com.swatisingh0960.github.twitterredux.fragments.HomeTimelineFragment;
import com.swatisingh0960.github.twitterredux.fragments.SearchTweetsFragment;
import com.swatisingh0960.github.twitterredux.fragments.TweetsListFragment;
import com.swatisingh0960.github.twitterredux.models.Message;
import com.swatisingh0960.github.twitterredux.models.Tweet;
import com.swatisingh0960.github.twitterredux.others.HelperMethods;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity
    implements ComposeDialogFragment.ComposeDialogListener {

    private TwitterClient client;
    public TweetsPagerAdapter tweetsPagerAdapter;

    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;

    public static final int TOP_TWEETS = 0;
    public static final int ALL_TWEETS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Bind views
        ButterKnife.bind(this);
        // Set Toolbar as Actionbar
        setSupportActionBar(toolbar);

        // Get client
        client = TwitterApplication.getRestClient();

        // Create new Adapter
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        // Set viewpager adapter for the pager
        viewPager.setAdapter(tweetsPagerAdapter);
        // Attach the pager tab to viewpager
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public void onFinishComposeDialog(int requestCode, Tweet tweet, Message message) {
        if (requestCode == TimelineActivity.REQUEST_COMPOSE || requestCode == TimelineActivity.REQUEST_REPLY) {
            HelperMethods.postTweet(HomeTimelineFragment.adapter, tweet);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == TimelineActivity.REQUEST_DETAILS ||
            requestCode == TimelineActivity.REQUEST_PROFILE)
            && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            Tweet updatedTweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update position
            getCurrentAdapter().update(position, updatedTweet);
        }
    }

    private ContactsAdapter getCurrentAdapter() {
        TweetsListFragment fg = getCurrentFragment();
        ContactsAdapter adapter = ((SearchTweetsFragment) fg).getAdapter();
        return adapter;
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Add hint
        searchView.setQueryHint("Search...");
        // Expande search view
        searchItem.expandActionView();
        // add actionbar search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Hide input soft keyboard
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                onArticleSearch(query.trim());
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void onArticleSearch(final String query) {
        if (!HelperMethods.isOnline()) {
            return;
        }
        // Update query of all fragmen
        if (query != null && query.length() != 0) {
            for (int i = 0; i < tweetsPagerAdapter.getCount(); i++) {
                SearchTweetsFragment fg = (SearchTweetsFragment) tweetsPagerAdapter.getCurrentFragment(i);
                fg.updateQuery(query);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Send progress bar to fragment
        for (int i = 0; i < tweetsPagerAdapter.getCount(); i++) {
            tweetsPagerAdapter.getCurrentFragment(i).setProgressbar(miActionProgressItem);
        }
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void composeTweet() {
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment
            .newInstance(TimelineActivity.REQUEST_COMPOSE, null, null);
        composeDialogFragment.show(getSupportFragmentManager(), "fragment_compose");
    }

    // double tap to scroll to top
    @OnClick({R.id.toolbar, R.id.fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                getCurrentFragment().goToTop();
                break;
            case R.id.fab:
                composeTweet();
                break;
        }
    }

    // Get current fragment
    public TweetsListFragment getCurrentFragment() {
        return tweetsPagerAdapter.getCurrentFragment(viewPager.getCurrentItem());
    }

    // Return the order of fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String[] tabTitles = {"Top Tweets", "All Tweets"};

        private Map<Integer, TweetsListFragment> map = new HashMap<>();

        // Adapter gets the manager insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Decide fragment by tab position
        @Override
        public Fragment getItem(int position) {
            TweetsListFragment fg = null;
            if (position == 0) {
                fg = SearchTweetsFragment.newInstance(null, TOP_TWEETS);
            } else if (position == 1) {
                fg = SearchTweetsFragment.newInstance(null, ALL_TWEETS);
            }
            map.put(position, fg);
            return fg;
        }

        // Decide tab name by tab position
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // get total count of fragments
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        // Get created fragment by position
        public TweetsListFragment getCurrentFragment(int position) {
            return map.get(position);
        }
    }
}