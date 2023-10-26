package com.example.lenscraft.Recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lenscraft.R;
import com.example.lenscraft.Recycler_view.PackageItemModel;

import java.util.List;

public class PackageItemAdapter extends RecyclerView.Adapter<PackageItemAdapter.ViewHolder> {
    private List<PackageItemModel> itemList;
    private OnItemClickListener listener;

    public PackageItemAdapter(List<PackageItemModel> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_single_package_item_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageItemModel item = itemList.get(position);

        // Set data to views
        holder.brandTV.setText(item.getBrand());
        holder.amountTV.setText(String.valueOf(item.getAmount()));
        holder.backgroundImageView.setImageResource(getBackgroundResource(item.getItemType()));

        holder.itemView.setOnClickListener(view -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView backgroundImageView;
        TextView brandTV;
        TextView amountTV;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.singlepackageitemCardViewFragment);
            backgroundImageView = itemView.findViewById(R.id.singlePackageItemSelectorImgView);
            brandTV = itemView.findViewById(R.id.brandTV);
            amountTV = itemView.findViewById(R.id.singleItemAmountTV);
        }
    }

    private int getBackgroundResource(int itemType) {
        switch (itemType) {
            case PackageItemModel.CAMERA:
                return R.drawable.packagecamera;
            case PackageItemModel.LENS:
                return R.drawable.packagelens;
            // Add cases for other item types
            default:
                return R.drawable.packagecamera;
//                return R.drawable.default_background; // Return a default resource
        }
    }



    public interface OnItemClickListener {
        void onItemClick(PackageItemModel item);
    }
}
