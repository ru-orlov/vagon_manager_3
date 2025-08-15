package com.example.wagonmanager3.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.wagonmanager3.R;
import com.example.wagonmanager3.database.DbContract;

public class InventoryAdapter extends CursorAdapter {
    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = view.findViewById(R.id.item_name);
        TextView tvQuantity = view.findViewById(R.id.item_quantity);
        TextView tvGroup = view.findViewById(R.id.item_group);
        TextView tvCondition = view.findViewById(R.id.item_condition);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.WagonInventory.COLUMN_QUANTITY));
        String group = cursor.getString(cursor.getColumnIndexOrThrow("group_name"));
        String condition = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.WagonInventory.COLUMN_CONDITION));

        tvName.setText(name);
        tvQuantity.setText(String.valueOf(quantity));
        tvGroup.setText(group);
        tvCondition.setText(condition);
    }
}