package com.example.lenscraft.PackageRecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lenscraft.R;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ItemRecyclerViewAdapter";

    private List<ItemModel> items;
    private OnItemClickListener itemClickListener;
    private String selectedCategory;

    public interface OnItemClickListener {
        void onItemClick(int itemId, String category);
    }

    public ItemRecyclerViewAdapter(List<ItemModel> items, OnItemClickListener itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setSelectedCategory(String category) {
        selectedCategory = category;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your item layout here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lenscraft_packages_recycler_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel item = items.get(position);

        // Bind the data to your item views
        holder.brandTextView.setText("Brand: " + item.getBrand());
        holder.modelTextView.setText("Model: " + item.getModel());
        holder.descriptionTextView.setText("Description: " + item.getDescription());
        holder.amountTextView.setText("Amount: Kshs" + item.getAmount());

        // Set a click listener for the item view
        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                int itemIdToPass = getItemIdToPass(item);
                Log.d(TAG, "Item clicked with ID: " + itemIdToPass);
                itemClickListener.onItemClick(itemIdToPass, selectedCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getItemIdToPass(ItemModel item) {
        if (selectedCategory != null) {
            switch (selectedCategory) {
                case "Light":
                    return item.getLightId();
                case "Printable":
                    return item.getPrintableId();
                case "Lens":
                    return item.getLensId();
                case "Audio":
                    return item.getAudioId();
                case "Camera":
                    return item.getCameraId();
            }
        }
        return -1; // Default value if category is not recognized
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView brandTextView;
        TextView modelTextView;
        TextView descriptionTextView;
        TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            brandTextView = itemView.findViewById(R.id.packageItemBrandTV);
            modelTextView = itemView.findViewById(R.id.packageItemModelTV);
            descriptionTextView = itemView.findViewById(R.id.packageItemDescriptionTV);
            amountTextView = itemView.findViewById(R.id.packageItemAmountTV);
        }
    }
}
