package com.example.luxevistaresortapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.ServiceReservation;
import java.util.List;

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.ServiceHistoryViewHolder> {
    private List<ServiceReservation> serviceHistoryList;

    public ServiceHistoryAdapter(List<ServiceReservation> serviceHistoryList) {
        this.serviceHistoryList = serviceHistoryList;
    }

    public void updateList(List<ServiceReservation> newList) {
        serviceHistoryList = newList;
        notifyDataSetChanged();
    }

    public interface OnServiceCancelListener {
        void onCancel(ServiceReservation reservation);
    }
    private OnServiceCancelListener cancelListener;
    public void setOnServiceCancelListener(OnServiceCancelListener listener) {
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ServiceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_history, parent, false);
        return new ServiceHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHistoryViewHolder holder, int position) {
        ServiceReservation reservation = serviceHistoryList.get(position);
        holder.serviceNameTextView.setText(reservation.getServiceName());
        holder.serviceDateTextView.setText("Date: " + reservation.getDate());
        holder.cancelButton.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceHistoryList.size();
    }

    static class ServiceHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, serviceDateTextView;
        Button cancelButton;
        ServiceHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceDateTextView = itemView.findViewById(R.id.serviceDateTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
} 