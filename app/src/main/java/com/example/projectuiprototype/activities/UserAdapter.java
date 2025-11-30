package com.example.projectuiprototype.activities;

import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    List<User> users;
    Context context;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
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

        holder.txtUserRow.setText(user.username + " (" + user.role + ")");

        if(user.role.equals("manager")) {
            holder.btnPromoteManager.setVisibility(View.GONE);
        }

        holder.btnPromoteManager.setOnClickListener(v -> {
            DatabaseClient.getInstance(context)
                    .getDatabase()
                    .userDao()
                    .updateRole(user.id, "manager");

            user.role = "manager";
            notifyItemChanged(position);

            Toast.makeText(context, user.username + " is now a MANAGER!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserRow;
        Button btnPromoteManager;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserRow = itemView.findViewById(R.id.txtUserRow);
            btnPromoteManager = itemView.findViewById(R.id.btnPromoteManager);
        }
    }
}
