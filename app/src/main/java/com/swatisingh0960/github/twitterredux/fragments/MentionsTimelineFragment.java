package com.swatisingh0960.github.twitterredux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.swatisingh0960.github.twitterredux.activities.DetailsActivity;
import com.swatisingh0960.github.twitterredux.activities.TimelineActivity;
import com.swatisingh0960.github.twitterredux.activities.TwitterApplication;
import com.swatisingh0960.github.twitterredux.adapters.ContactsAdapter;
import com.swatisingh0960.github.twitterredux.models.Message;
import com.swatisingh0960.github.twitterredux.models.Tweet;
import com.swatisingh0960.github.twitterredux.models.User;
import com.swatisingh0960.github.twitterredux.others.HelperMethods;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment
    implements ComposeDialogFragment.ComposeDialogListener {

    private ContactsAdapter adapter;
    private User account;

    // Creates a new fragment given an int and title
    public static MentionsTimelineFragment newInstance(User account) {
        MentionsTimelineFragment fg = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(account));
        fg.setArguments(args);
        return fg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create new list
        tweets = new ArrayList<>();

        // Contact adapter from data source
        account = Parcels.unwrap(getArguments().getParcelable("user"));
        adapter = new ContactsAdapter(getActivity(), tweets, account);
        rvAdapter = adapter;

        // Load for the first time
        swipeLoad();
    }

    @Override
    public void swipeLoad() {
        // Clear existing data
        adapter.clear();
        // Populate new data
        populateTimeline(null, null);
    }

    @Override
    public void itemClicked(int position) {
        // Go to detail activity
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));
        intent.putExtra("position", position);
        getActivity().startActivityForResult(intent, TimelineActivity.REQUEST_DETAILS);
    }

    @Override
    public void onFinishComposeDialog(int requestCode, Tweet tweet, Message message) {
        if (requestCode == TimelineActivity.REQUEST_REPLY) {
            HelperMethods.postTweet(HomeTimelineFragment.adapter, tweet);
        }
    }

    @Override
    public void endlessLoad(int page, int totalItemsCount) {
        populateTimeline(null, Long.parseLong(tweets.get(totalItemsCount - 1).getTid()) - 1);
    }

    public ContactsAdapter getAdapter() {
        return adapter;
    }

    // Sent API request to get Timeline JSON
    // Fill the listView by creating Tweet object from JSON
    // since: id > since (newer)
    // max: id <= max (older)
    public void populateTimeline(Long since, Long max) {
        TwitterApplication.getRestClient().getMentionsTimeline(since, max, new JsonHttpResponseHandler(){
            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);
                if (newTweets != null) {
                    adapter.addAll(newTweets);
                    if (swipeContainer != null) {
                        // Disable refreshing icon
                        swipeContainer.setRefreshing(false);
                    }
                }
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                JSONObject errorResponse) {
                Log.d("DEBUG", "Fetch mentions timeline error: " + errorResponse.toString());
            }
        });
    }
}
