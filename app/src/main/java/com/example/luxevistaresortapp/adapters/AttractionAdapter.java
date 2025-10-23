package com.example.luxevistaresortapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Attraction;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {
    private Context context;
    private List<Attraction> attractionList;
    private final OnAttractionActionListener actionListener;

    public AttractionAdapter(Context context, List<Attraction> attractionList, OnAttractionActionListener actionListener) {
        this.context = context;
        this.attractionList = attractionList;
        this.actionListener = actionListener;
    }

    public void updateList(List<Attraction> newList) {
        attractionList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.attractionNameTextView.setText(attraction.getName());
        holder.attractionDescriptionTextView.setText(attraction.getDescription());
        if (attraction.getImageUrl() != null && attraction.getImageUrl().startsWith("file:///android_res/drawable/")) {
            String drawableName = attraction.getImageUrl().substring("file:///android_res/drawable/".length());
            int resId = holder.attractionImageView.getContext().getResources().getIdentifier(drawableName, "drawable", holder.attractionImageView.getContext().getPackageName());
            if (resId != 0) {
                holder.attractionImageView.setImageResource(resId);
            } else {
                holder.attractionImageView.setImageResource(R.drawable.placeholder);
            }
        } else if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
            Picasso.get().load(attraction.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.attractionImageView);
        } else {
            holder.attractionImageView.setImageResource(R.drawable.placeholder);
        }
        if (actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> actionListener.onEdit(attraction));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDelete(attraction));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(context, "Selected: " + attraction.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public interface OnAttractionActionListener {
        void onEdit(com.example.luxevistaresortapp.models.Attraction attraction);
        void onDelete(com.example.luxevistaresortapp.models.Attraction attraction);
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        public ImageView attractionImageView;
        public TextView attractionNameTextView, attractionDescriptionTextView;
        public Button editButton, deleteButton;

        AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionImageView = itemView.findViewById(R.id.attractionImageView);
            attractionNameTextView = itemView.findViewById(R.id.attractionNameTextView);
            attractionDescriptionTextView = itemView.findViewById(R.id.attractionDescriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}