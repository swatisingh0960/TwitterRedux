package com.swatisingh0960.github.twitterredux.models;

import android.support.v7.widget.RecyclerView;

import com.swatisingh0960.github.twitterredux.databinding.ItemMessageBinding;

public class ViewHolderMessage extends RecyclerView.ViewHolder {

    ItemMessageBinding binding;

    public ViewHolderMessage(ItemMessageBinding binding) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemMessageBinding getBinding() {
        return binding;
    }
}
