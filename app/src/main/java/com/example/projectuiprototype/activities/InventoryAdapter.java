package com.example.projectuiprototype.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.projectuiprototype.api.InventoryApi;
import com.example.projectuiprototype.api.InventoryItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InvHolder> {

    private final List<InventoryItemDto> items;
    private final Context context;

    public InventoryAdapter(List<InventoryItemDto> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public InvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_inventory, parent, false);
        return new InvHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InvHolder h, int pos) {
        InventoryItemDto item = items.get(pos);

        h.name.setText(item.name != null ? item.name : "");
        h.qty.setText(String.valueOf(item.quantity));

        h.editBtn.setOnClickListener(v -> {
            Intent i = new Intent(context, InventoryEditActivity.class);

            // Mongo uses _id string
            i.putExtra("id", item.id);
            i.putExtra("name", item.name);
            i.putExtra("qty", item.quantity);
            i.putExtra("unit", item.unit);

            context.startActivity(i);
        });

        h.deleteBtn.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Remove " + (item.name != null ? item.name : "this item") + " from inventory?")
                .setPositiveButton("Delete", (d, w) -> deleteItemFromApi(item, pos))
                .setNegativeButton("Cancel", null)
                .show());
    }

    private void deleteItemFromApi(InventoryItemDto item, int pos) {
        if (item.id == null || item.id.isEmpty()) {
            Toast.makeText(context, "Missing item id", Toast.LENGTH_SHORT).show();
            return;
        }

        InventoryApi api = ApiClient.getClient(context).create(InventoryApi.class);

        api.deleteItem(item.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                items.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class InvHolder extends RecyclerView.ViewHolder {
        TextView name, qty;
        Button editBtn, deleteBtn;

        InvHolder(View v) {
            super(v);
            name = v.findViewById(R.id.txtInvName);
            qty = v.findViewById(R.id.txtInvQty);
            editBtn = v.findViewById(R.id.btnEditInv);
            deleteBtn = v.findViewById(R.id.btnDeleteInv);
        }
    }
}
