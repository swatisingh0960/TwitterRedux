package com.swatisingh0960.github.twitterredux.models;

import android.support.v7.widget.RecyclerView;

import com.swatisingh0960.github.twitterredux.databinding.ItemFollowBinding;

public class ViewHolderFollow extends RecyclerView.ViewHolder {

    ItemFollowBinding binding;

    public ViewHolderFollow(ItemFollowBinding binding) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemFollowBinding getBinding() {
        return binding;
    }
}
