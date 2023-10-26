package com.example.lenscraft.Recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenscraft.R;

import java.util.List;

public class ImageButtonAdapter extends RecyclerView.Adapter<ImageButtonAdapter.ViewHolder> {
    private List<ImageButtonModel> imageButtonList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ImageButtonAdapter(List<ImageButtonModel> imageButtonList) {
        this.imageButtonList = imageButtonList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_view_item_design, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageButtonModel item = imageButtonList.get(position);
        holder.imageButton.setImageResource(item.getImageResourceId());
        holder.textView.setText(item.getLabelText());
    }

    @Override
    public int getItemCount() {
        return imageButtonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageButton;
        TextView textView;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            imageButton = view.findViewById(R.id.itemImgBtn);
            textView = view.findViewById(R.id.itemNameTV);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);

                        }
                    }
                }
            });
        }
    }
}
