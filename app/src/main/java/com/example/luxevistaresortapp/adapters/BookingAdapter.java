package com.example.luxevistaresortapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;

    public BookingAdapter() {
        this.bookingList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bookingIdTextView.setText("Booking ID: " + booking.getBookingId());
        holder.bookingDetailsTextView.setText("Details: " + booking.getDetails());
        holder.bookingDateTextView.setText("Date: " + booking.getDate());
        holder.bookingStatusTextView.setText("Status: " + booking.getStatus());
        holder.cancelButton.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void setBookingList(List<Booking> bookings) {
        this.bookingList.clear();
        this.bookingList.addAll(bookings);
        notifyDataSetChanged();
    }

    public interface OnBookingCancelListener {
        void onCancel(Booking booking);
    }
    private OnBookingCancelListener cancelListener;
    public void setOnBookingCancelListener(OnBookingCancelListener listener) {
        this.cancelListener = listener;
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingIdTextView, bookingDetailsTextView, bookingDateTextView, bookingStatusTextView;
        Button cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingIdTextView = itemView.findViewById(R.id.bookingIdTextView);
            bookingDetailsTextView = itemView.findViewById(R.id.bookingDetailsTextView);
            bookingDateTextView = itemView.findViewById(R.id.bookingDateTextView);
            bookingStatusTextView = itemView.findViewById(R.id.bookingStatusTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}