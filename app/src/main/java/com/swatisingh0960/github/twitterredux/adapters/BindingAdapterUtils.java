package com.swatisingh0960.github.twitterredux.adapters;

import android.databinding.BindingAdapter;
import android.net.ParseException;
import android.widget.ImageView;
import android.widget.TextView;

import com.swatisingh0960.github.twitterredux.others.HelperMethods;
import com.swatisingh0960.github.twitterredux.others.LinkifiedTextView;
import com.swatisingh0960.github.twitterredux.others.TimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BindingAdapterUtils {
    @BindingAdapter({"bind:profileImageUrl"})
    public static void loadImage(ImageView view, String url) {
        HelperMethods.loadProfileImg(view, url);
    }

    @BindingAdapter({"bind:msgProfileImageUrl"})
    public static void loadMsgImage(ImageView view, String url) {
        HelperMethods.loadMsgProfileImg(view, url);
    }

    @BindingAdapter({"bind:profileBannerUrl"})
    public static void loadBannerImage(ImageView view, String url) {
        HelperMethods.loadBannerImg(view, url);
    }

    @BindingAdapter({"bind:relativeTime"})
    public static void loadRelativeTime(TextView view, String data) {
        if (view == null || data == null || data.length() == 0) {
            return;
        }
        String time = TimeFormatter.getTimeDifference(data);
        view.setText(time);
    }

    @BindingAdapter({"bind:linkifiedText"})
    public static void formatLinkifiedText(LinkifiedTextView view, String data) {
        if (view == null || data == null || data.length() == 0) {
            return;
        }
        view.setText(data);
        HelperMethods.formatBody(view.getContext(), view);
    }

    @BindingAdapter({"bind:timeFormat"})
    public static void loadTime(TextView view, String data) {
        if (view == null || data == null || data.length() == 0) {
            return;
        }
        SimpleDateFormat input = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm aa - dd MMM yy");
        String result = null;
        try {
            Date oneWayTripDate = input.parse(data);                 // parse input
            result = output.format(oneWayTripDate);    // format output
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        view.setText(result);
    }
}
