package com.swatisingh0960.github.twitterredux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.swatisingh0960.github.twitterredux.R;
import com.swatisingh0960.github.twitterredux.adapters.FollowContactsAdapter;
import com.swatisingh0960.github.twitterredux.fragments.ComposeDialogFragment;
import com.swatisingh0960.github.twitterredux.fragments.FollowListFragment;
import com.swatisingh0960.github.twitterredux.fragments.FriendsListFragment;
import com.swatisingh0960.github.twitterredux.fragments.HomeTimelineFragment;
import com.swatisingh0960.github.twitterredux.fragments.TweetsListFragment;
import com.swatisingh0960.github.twitterredux.models.Message;
import com.swatisingh0960.github.twitterredux.models.Tweet;
import com.swatisingh0960.github.twitterredux.models.User;
import com.swatisingh0960.github.twitterredux.others.HelperMethods;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FollowActivity extends AppCompatActivity
    implements ComposeDialogFragment.ComposeDialogListener {

    int request;
    User account;
    final String TAG = "follow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get intent data
        request = getIntent().getIntExtra("request", -1);
        account = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace fragment base on request code
        if (request == ProfileActivity.FOLLOWERS) {
            ft.replace(R.id.flFollow, FollowListFragment.newInstance(account), TAG);
            setTitle("Followers");
        } else {
            ft.replace(R.id.flFollow, FriendsListFragment.newInstance(account), TAG);
            setTitle("Following");
        }
        // Complete the changes added above
        ft.commit();

        // Bind view with ButterKnife
        ButterKnife.bind(this);
    }

    @Override
    public void onFinishComposeDialog(int requestCode, Tweet tweet, Message message) {
        if (requestCode == TimelineActivity.REQUEST_COMPOSE) {
            HelperMethods.postTweet(HomeTimelineFragment.adapter, tweet);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == TimelineActivity.REQUEST_DETAILS ||
            requestCode == TimelineActivity.REQUEST_PROFILE) && resultCode == TimelineActivity.RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            User updatedUser = Parcels.unwrap(data.getParcelableExtra("user"));
            // Update position
            getCurrentAdapter().update(position, updatedUser);
        }
    }

    private FollowContactsAdapter getCurrentAdapter() {
        FollowContactsAdapter adapter = null;
        TweetsListFragment fg = (TweetsListFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if (fg instanceof FollowListFragment) {
            adapter = ((FollowListFragment) fg).getAdapter();
        } else {
            adapter = ((FriendsListFragment) fg).getAdapter();
        }
        return adapter;
    }

    // double tap to scroll to top
    @OnClick({R.id.toolbar, R.id.fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                // Get fragment
                TweetsListFragment fg = (TweetsListFragment) getSupportFragmentManager().findFragmentByTag(TAG);
                // go to top
                fg.goToTop();
                break;
            case R.id.fab:
                composeTweet();
                break;
        }
    }

    private void composeTweet() {
        ComposeDialogFragment composeDialogFragment
            = ComposeDialogFragment.newInstance(TimelineActivity.REQUEST_COMPOSE, null, null);
        composeDialogFragment.show(getSupportFragmentManager(), "fragment_compose");
    }

}
