package com.example.luxevistaresortapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Service;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private final OnServiceClickListener listener;
    private final OnServiceActionListener actionListener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public interface OnServiceActionListener {
        void onEdit(Service service);
        void onDelete(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener, OnServiceActionListener actionListener) {
        this.serviceList = serviceList;
        this.listener = listener;
        this.actionListener = actionListener;
    }

    public void updateList(List<Service> newList) {
        serviceList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceNameTextView.setText(service.getName());
        holder.serviceTypeTextView.setText(service.getType());
        holder.servicePriceTextView.setText(String.format("$%.2f", service.getPrice()));
        if (service.getImageUrl() != null && service.getImageUrl().startsWith("file:///android_res/drawable/")) {
            String drawableName = service.getImageUrl().substring("file:///android_res/drawable/".length());
            int resId = holder.serviceImageView.getContext().getResources().getIdentifier(drawableName, "drawable", holder.serviceImageView.getContext().getPackageName());
            if (resId != 0) {
                holder.serviceImageView.setImageResource(resId);
            } else {
                holder.serviceImageView.setImageResource(R.drawable.placeholder);
            }
        } else if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Picasso.get().load(service.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.serviceImageView);
        } else {
            holder.serviceImageView.setImageResource(R.drawable.placeholder);
        }
        TextView availableSlotsTextView = holder.itemView.findViewById(R.id.serviceAvailableSlotsTextView);
        availableSlotsTextView.setText("Available Slots: " + service.getAvailableSlots());
        if (actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.reserveButton.setVisibility(View.GONE);
            holder.editButton.setOnClickListener(v -> actionListener.onEdit(service));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDelete(service));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.reserveButton.setVisibility(View.VISIBLE);
            holder.reserveButton.setOnClickListener(v -> listener.onServiceClick(service));
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImageView;
        TextView serviceNameTextView, serviceTypeTextView, servicePriceTextView;
        public Button reserveButton, editButton, deleteButton;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceTypeTextView = itemView.findViewById(R.id.serviceTypeTextView);
            servicePriceTextView = itemView.findViewById(R.id.servicePriceTextView);
            reserveButton = itemView.findViewById(R.id.reserveButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}