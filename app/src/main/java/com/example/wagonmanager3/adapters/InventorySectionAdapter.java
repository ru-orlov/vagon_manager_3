package com.example.wagonmanager3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.InventoryEditActivity;
import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.InventoryGroup;
import com.example.wagonmanager3.models.InventoryItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventorySectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GROUP = 0;
    private static final int TYPE_ITEM = 1;

    private final List<Object> itemsForDisplay = new ArrayList<>();

    public InventorySectionAdapter(List<InventoryGroup> groups, List<InventoryItem> items) {
        // Группируем элементы по группам и формируем плоский список для отображения
        Map<String, List<InventoryItem>> displayData = new LinkedHashMap<>();
        for (InventoryGroup group : groups) {
            // ВНИМАНИЕ: сравнивать groupId и group.getUuid() (или getId()), зависит от вашей модели!
            List<InventoryItem> groupItems = new ArrayList<>();
            for (InventoryItem item : items) {
                if (String.valueOf(item.getGroupId()).equals(group.getUuid())
                ) {
                    groupItems.add(item);
                }
            }
            displayData.put(group.getName(), groupItems);
        }

        // Формируем плоский список для RecyclerView
        for (InventoryGroup group : groups) {
            itemsForDisplay.add(group);
            List<InventoryItem> itemList = displayData.get(group.getName());
            if (itemList != null) {
                itemsForDisplay.addAll(itemList);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = itemsForDisplay.get(position);
        if (obj instanceof InventoryGroup) return TYPE_GROUP;
        else return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return itemsForDisplay.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GROUP) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_group_section, parent, false);
            return new GroupViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_section, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object obj = itemsForDisplay.get(position);
        if (getItemViewType(position) == TYPE_GROUP) {
            InventoryGroup group = (InventoryGroup) obj;
            ((GroupViewHolder) holder).groupName.setText(group.getName());
        } else {
            InventoryItem item = (InventoryItem) obj;
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.itemName.setText(item.getName());
            itemHolder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            
            // Add click listener for editing
            itemHolder.itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, InventoryEditActivity.class);
                intent.putExtra("inventory_id", item.getId());
                intent.putExtra("wagon_uuid", item.getVagonUuid());
                context.startActivity(intent);
            });
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        GroupViewHolder(View view) {
            super(view);
            groupName = view.findViewById(R.id.groupName);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        ItemViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.itemName);
            itemQuantity = view.findViewById(R.id.itemQuantity);
        }
    }
}