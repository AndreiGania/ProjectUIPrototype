package com.example.projectuiprototype.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.UserApi;
import com.example.projectuiprototype.api.UserDto;
import com.example.projectuiprototype.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnRefresh {
        void refresh();
    }

    private final List<User> users;
    private final Context context;
    private final UserApi userApi;
    private final OnRefresh onRefresh;

    public UserAdapter(List<User> users, Context context, OnRefresh onRefresh) {
        this.users = users;
        this.context = context;
        this.onRefresh = onRefresh;
        this.userApi = ApiClient.getClient(context.getApplicationContext()).create(UserApi.class);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        String role = (user.role == null) ? "" : user.role;
        holder.txtUserRow.setText(user.username + " (" + role + ")");

        // This screen should already be filtered, but just in case:
        String r = role.toLowerCase();
        if (r.equals("manager") || r.equals("admin")) {
            holder.btnPromoteManager.setVisibility(View.GONE);
        } else {
            holder.btnPromoteManager.setVisibility(View.VISIBLE);
        }

        holder.btnPromoteManager.setOnClickListener(v -> {
            if (user.serverId == null || user.serverId.isEmpty()) {
                Toast.makeText(context, "Missing serverId for user", Toast.LENGTH_SHORT).show();
                return;
            }

            holder.btnPromoteManager.setEnabled(false);
            holder.btnPromoteManager.setText("Promoting...");

            userApi.promoteToManager(user.serverId).enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    holder.btnPromoteManager.setEnabled(true);
                    holder.btnPromoteManager.setText("Promote");

                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Promote failed: " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(context, user.username + " promoted ✅", Toast.LENGTH_SHORT).show();

                    // Refresh the list from server so promoted user disappears
                    if (onRefresh != null) onRefresh.refresh();
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    holder.btnPromoteManager.setEnabled(true);
                    holder.btnPromoteManager.setText("Promote");
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserRow;
        Button btnPromoteManager;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserRow = itemView.findViewById(R.id.txtUserRow);
            btnPromoteManager = itemView.findViewById(R.id.btnPromoteManager);
        }
    }
}