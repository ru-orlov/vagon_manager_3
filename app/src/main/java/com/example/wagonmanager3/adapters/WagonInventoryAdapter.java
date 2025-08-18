package com.example.wagonmanager3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.InventoryItem;

import java.util.List;

public class WagonInventoryAdapter extends RecyclerView.Adapter<WagonInventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> items;

    public WagonInventoryAdapter(List<InventoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView inventoryName;
        private TextView inventoryDescription;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            inventoryName = itemView.findViewById(R.id.textViewInventoryName);
            inventoryDescription = itemView.findViewById(R.id.textViewInventoryDescription);
        }

        public void bind(InventoryItem item) {
            inventoryName.setText(item.getName());
            inventoryDescription.setText(item.getDescription());
        }
    }
}
