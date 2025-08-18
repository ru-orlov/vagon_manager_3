package com.example.wagonmanager3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.InventoryTableRow;

import java.util.List;

public class InventoryTableAdapter extends RecyclerView.Adapter<InventoryTableAdapter.ViewHolder> {
    private final List<InventoryTableRow> data;

    public InventoryTableAdapter(List<InventoryTableRow> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public InventoryTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_table_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryTableAdapter.ViewHolder holder, int position) {
        InventoryTableRow row = data.get(position);
        holder.groupName.setText(row.getGroupName());
        holder.itemName.setText(row.getItemName());
        holder.itemQuantity.setText(String.valueOf(row.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, itemName, itemQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }
}