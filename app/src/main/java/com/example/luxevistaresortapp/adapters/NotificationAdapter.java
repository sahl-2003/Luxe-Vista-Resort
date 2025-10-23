package com.example.luxevistaresortapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.models.Notification;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notificationList;
    private Context context;
    private OnNotificationActionListener actionListener;
    private boolean isAdminView;

    public interface OnNotificationActionListener {
        void onEdit(Notification notification);
        void onDelete(Notification notification);
        void onToggleActive(Notification notification);
        void onView(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notificationList, 
                             OnNotificationActionListener actionListener, boolean isAdminView) {
        this.context = context;
        this.notificationList = notificationList;
        this.actionListener = actionListener;
        this.isAdminView = isAdminView;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notificationList = newNotifications;
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView typeIcon;
        private TextView titleText;
        private TextView messageText;
        private TextView typeText;
        private TextView dateText;
        private TextView statusText;
        private Button editButton;
        private Button deleteButton;
        private View expiredIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            typeIcon = itemView.findViewById(R.id.notificationTypeIcon);
            titleText = itemView.findViewById(R.id.notificationTitle);
            messageText = itemView.findViewById(R.id.notificationMessage);
            typeText = itemView.findViewById(R.id.notificationType);
            dateText = itemView.findViewById(R.id.notificationDate);
            statusText = itemView.findViewById(R.id.notificationStatus);
            editButton = itemView.findViewById(R.id.editNotificationButton);
            deleteButton = itemView.findViewById(R.id.deleteNotificationButton);
            expiredIndicator = itemView.findViewById(R.id.expiredIndicator);
        }

        public void bind(Notification notification) {
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            typeText.setText(notification.getTypeDisplayName());
            typeIcon.setImageResource(notification.getTypeIcon());

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            if (notification.getCreatedAt() != null) {
                dateText.setText("Created: " + sdf.format(notification.getCreatedAt().toDate()));
            }

            // Show/hide admin controls
            if (isAdminView) {
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.VISIBLE);

                // Set status text
                if (notification.isExpired()) {
                    statusText.setText("EXPIRED");
                    statusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    expiredIndicator.setVisibility(View.VISIBLE);
                } else if (notification.isActive()) {
                    statusText.setText("ACTIVE");
                    statusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    expiredIndicator.setVisibility(View.GONE);
                } else {
                    statusText.setText("INACTIVE");
                    statusText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    expiredIndicator.setVisibility(View.GONE);
                }

                // Set click listeners
                editButton.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onEdit(notification);
                    }
                });

                deleteButton.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onDelete(notification);
                    }
                });
            } else {
                // User view - hide admin controls
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                expiredIndicator.setVisibility(View.GONE);

                // Make entire item clickable for user view
                itemView.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onView(notification);
                    }
                });
            }

            // Set background color based on type
            int backgroundColor;
            switch (notification.getType()) {
                case "event":
                    backgroundColor = context.getResources().getColor(R.color.primary_100);
                    break;
                case "discount":
                    backgroundColor = context.getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case "service_update":
                    backgroundColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                    break;
                default:
                    backgroundColor = context.getResources().getColor(R.color.primary_40);
                    break;
            }
            typeText.setBackgroundColor(backgroundColor);
        }
    }
} 