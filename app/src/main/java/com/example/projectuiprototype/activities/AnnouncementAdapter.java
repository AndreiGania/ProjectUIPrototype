package com.example.projectuiprototype.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.AnnouncementApi;
import com.example.projectuiprototype.api.AnnouncementDto;
import com.example.projectuiprototype.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private final Context context;
    private final List<AnnouncementDto> announcements;
    private final AnnouncementApi announcementApi;
    private final boolean canDelete;

    public AnnouncementAdapter(Context context, List<AnnouncementDto> announcements) {
        this.context = context;
        this.announcements = announcements;
        this.announcementApi = ApiClient.getClient(context).create(AnnouncementApi.class);

        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String role = prefs.getString("role", "");
        this.canDelete = "manager".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        AnnouncementDto announcement = announcements.get(position);

        holder.tvTitle.setText(announcement.title != null ? announcement.title : "");
        holder.tvMessage.setText(announcement.message != null ? announcement.message : "");

        if (canDelete) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> showDeleteDialog(announcement, holder.getAdapterPosition()));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    private void showDeleteDialog(AnnouncementDto announcement, int position) {
        if (position == RecyclerView.NO_POSITION) return;

        new AlertDialog.Builder(context)
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete this announcement?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAnnouncement(announcement, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAnnouncement(AnnouncementDto announcement, int position) {
        if (announcement == null || announcement.id == null || announcement.id.trim().isEmpty()) {
            Toast.makeText(context, "Announcement ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        announcementApi.deleteAnnouncement(announcement.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    announcements.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage;
        Button btnDelete;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAnnouncementTitle);
            tvMessage = itemView.findViewById(R.id.tvAnnouncementMessage);
            btnDelete = itemView.findViewById(R.id.btnDeleteAnnouncement);
        }
    }
}