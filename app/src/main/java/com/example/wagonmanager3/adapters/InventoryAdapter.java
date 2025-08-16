package com.example.wagonmanager3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.InventoryItem;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>
        implements Filterable {

    private List<InventoryItem> items;
    private List<InventoryItem> filteredItems;
    private final OnItemClickListener listener;

    public InventoryAdapter(List<InventoryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        this.listener = listener;
    }

    public void updateData(List<InventoryItem> newItems) {
        this.items = newItems;
        this.filteredItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(filteredItems.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return new InventoryFilter();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView groupView;
        private final TextView quantityView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.itemName);
            groupView = itemView.findViewById(R.id.itemGroup);
            quantityView = itemView.findViewById(R.id.itemQuantity);
        }

        public void bind(InventoryItem item, OnItemClickListener listener) {
            nameView.setText(item.getName());
            groupView.setText(item.getName());
            quantityView.setText(String.format(Locale.getDefault(), "x%d", item.getQuantity()));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    private class InventoryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<InventoryItem> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(items);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (InventoryItem item : items) {
                    if (item.getName().toLowerCase().contains(pattern) ||
                            item.getName().toLowerCase().contains(pattern)) {
                        filtered.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems.clear();
            filteredItems.addAll((List<InventoryItem>) results.values);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);
    }
}