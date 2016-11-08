package com.swatisingh0960.github.twitterredux.models;

import android.support.v7.widget.RecyclerView;

import com.swatisingh0960.github.twitterredux.databinding.ItemMediumBinding;

public class ViewHolderMedium extends RecyclerView.ViewHolder {

    ItemMediumBinding binding;

    public ViewHolderMedium(ItemMediumBinding binding) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemMediumBinding getBinding() {
        return binding;
    }
}
