package com.example.luxevistaresortapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Offer;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {
    private Context context;
    private List<Offer> offerList;
    private final OnOfferActionListener actionListener;

    public OfferAdapter(Context context, List<Offer> offerList, OnOfferActionListener actionListener) {
        this.context = context;
        this.offerList = offerList;
        this.actionListener = actionListener;
    }

    public void updateList(List<Offer> newList) {
        offerList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.offerTitleTextView.setText(offer.getTitle());
        holder.offerDescriptionTextView.setText(offer.getDescription());
        holder.offerStatusTextView.setText(offer.isActive() ? "Status: Active" : "Status: Inactive");
        holder.offerStatusTextView.setTextColor(offer.isActive() ? 0xFF4CAF50 : 0xFFF44336); // Green for active, red for inactive
        if (actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> actionListener.onEdit(offer));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDelete(offer));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public interface OnOfferActionListener {
        void onEdit(com.example.luxevistaresortapp.models.Offer offer);
        void onDelete(com.example.luxevistaresortapp.models.Offer offer);
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView offerTitleTextView, offerDescriptionTextView, offerStatusTextView;
        public Button editButton, deleteButton;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerTitleTextView = itemView.findViewById(R.id.offerTitleTextView);
            offerDescriptionTextView = itemView.findViewById(R.id.offerDescriptionTextView);
            offerStatusTextView = itemView.findViewById(R.id.offerStatusTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}