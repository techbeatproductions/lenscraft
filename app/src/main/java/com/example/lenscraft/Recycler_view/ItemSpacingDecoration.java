package com.example.lenscraft.Recycler_view;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    public ItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.right = spacing; // Set spacing for the right side of the item (change to left for left spacing)
        }
    }
}
