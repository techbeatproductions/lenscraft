package com.example.lenscraft.PackageRecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lenscraft.R;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private List<ItemModel> items;
    private String selectedCategory;

    public ItemRecyclerViewAdapter(List<ItemModel> items) {
        this.items = items;
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

        // You can also add additional views or customize the layout as needed
    }

    @Override
    public int getItemCount() {
        return items.size();
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

