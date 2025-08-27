package com.example.wagonmanager3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private OnItemActionListener actionListener;

    // Интерфейс для обработки действий
    public interface OnItemActionListener {
        void onAddItem(InventoryItem item);
        void onEditItem(InventoryItem item);
        void onDeleteItem(InventoryItem item);
    }

    public InventorySectionAdapter(List<InventoryGroup> groups, List<InventoryItem> items, OnItemActionListener listener) {
        this.actionListener = listener;

        // Группируем элементы по группам и формируем плоский список для отображения
        Map<String, List<InventoryItem>> displayData = new LinkedHashMap<>();
        for (InventoryGroup group : groups) {
            List<InventoryItem> groupItems = new ArrayList<>();
            for (InventoryItem item : items) {
                if (String.valueOf(item.getGroupId()).equals(group.getUuid())) {
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
            itemHolder.bind(item, actionListener);
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;

        GroupViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemQuantity;
        ImageButton btnAdd;
        ImageButton btnEdit;
        ImageButton btnDelete;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(InventoryItem item, OnItemActionListener listener) {
            itemName.setText(item.getName());
            itemQuantity.setText(String.valueOf(item.getQuantity()));

            if (listener != null) {
                btnAdd.setOnClickListener(v -> listener.onAddItem(item));
                btnEdit.setOnClickListener(v -> listener.onEditItem(item));
                btnDelete.setOnClickListener(v -> listener.onDeleteItem(item));
            }
        }
    }
}