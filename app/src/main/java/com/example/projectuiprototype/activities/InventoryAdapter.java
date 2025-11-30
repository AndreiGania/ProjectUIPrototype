package com.example.projectuiprototype.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.InventoryItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InvHolder> {

    List<InventoryItem> items;
    Context context;

    public InventoryAdapter(List<InventoryItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull @Override
    public InvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inventory, parent, false);
        return new InvHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InvHolder h, int pos) {
        InventoryItem item = items.get(pos);
        h.name.setText(item.name);
        h.qty.setText("Qty: " + item.quantity);

        h.editBtn.setOnClickListener(v -> {
            Intent i = new Intent(context, InventoryEditActivity.class);
            i.putExtra("id", item.id);
            i.putExtra("name", item.name);
            i.putExtra("qty", item.quantity);
            context.startActivity(i);
        });

        h.deleteBtn.setOnClickListener(v -> {
            DatabaseClient.getInstance(context).getDatabase().inventoryDao().deleteItem(item);
            items.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class InvHolder extends RecyclerView.ViewHolder {
        TextView name, qty;
        Button editBtn, deleteBtn;
        InvHolder(View v){
            super(v);
            name = v.findViewById(R.id.txtInvName);
            qty = v.findViewById(R.id.txtInvQty);
            editBtn = v.findViewById(R.id.btnEditInv);
            deleteBtn = v.findViewById(R.id.btnDeleteInv);
        }
    }
}
