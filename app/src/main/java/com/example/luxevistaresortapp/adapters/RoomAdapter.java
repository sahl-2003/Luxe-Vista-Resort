package com.example.luxevistaresortapp.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Booking;
import com.example.luxevistaresortapp.models.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> roomList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final OnRoomActionListener actionListener;

    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomActionListener actionListener) {
        this.context = context;
        this.roomList = roomList;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.actionListener = actionListener;
    }

    public void updateList(List<Room> newList) {
        roomList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomNameTextView.setText(room.getName());
        holder.roomTypeTextView.setText(room.getType());
        holder.roomDescriptionTextView.setText(room.getDescription() != null ? room.getDescription() : "No description available");
        holder.roomPriceTextView.setText(String.format("$%.2f/night", room.getPrice()));
        if (room.getImageUrl().startsWith("file:///android_res/drawable/")) {
            String drawableName = room.getImageUrl().substring("file:///android_res/drawable/".length());
            int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            holder.roomImageView.setImageResource(resId);
        } else {
            Picasso.get().load(room.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.roomImageView);
        }
        if (actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.bookButton.setVisibility(View.GONE);
            holder.editButton.setOnClickListener(v -> actionListener.onEdit(room));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDelete(room));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.bookButton.setVisibility(View.VISIBLE);
            holder.bookButton.setOnClickListener(v -> {
                String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                if (userId == null) {
                    Toast.makeText(context, "Please log in to book a room", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Show a custom dialog with one DatePicker for check-in only
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_room_booking_date_single);
                DatePicker checkInPicker = dialog.findViewById(R.id.checkInDatePicker);
                Button confirmButton = dialog.findViewById(R.id.confirmBookingButton);
                confirmButton.setOnClickListener(view -> {
                    Calendar checkInDate = Calendar.getInstance();
                    checkInDate.set(checkInPicker.getYear(), checkInPicker.getMonth(), checkInPicker.getDayOfMonth());
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    String checkInStr = sdf.format(checkInDate.getTime());
                    // Check for existing booking for this room and date
                    db.collection("bookings")
                        .whereEqualTo("roomId", room.getId())
                        .whereEqualTo("date", checkInStr)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                Toast.makeText(context, "Room is already booked for this date. Please choose another date.", Toast.LENGTH_SHORT).show();
                            } else {
                                String bookingId = "booking_" + System.currentTimeMillis();
                                Booking booking = new Booking(
                                        bookingId,
                                        userId,
                                        room.getId(),
                                        room.getName() + ", 1 night",
                                        checkInStr,
                                        null,
                                        "Confirmed"
                                );
                                db.collection("bookings").document(bookingId).set(booking)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Room booked successfully: " + room.getName(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to book room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to check room availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                });
                dialog.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImageView;
        TextView roomNameTextView, roomTypeTextView, roomDescriptionTextView, roomPriceTextView;
        public Button bookButton, editButton, deleteButton;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            roomDescriptionTextView = itemView.findViewById(R.id.roomDescriptionTextView);
            roomPriceTextView = itemView.findViewById(R.id.roomPriceTextView);
            bookButton = itemView.findViewById(R.id.bookButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}