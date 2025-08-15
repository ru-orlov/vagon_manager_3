package com.example.wagonmanager3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.ScanHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class HistoryAdapter extends ArrayAdapter<ScanHistory> {
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ScanHistory item);
    }

    public HistoryAdapter(Context context, List<ScanHistory> items, OnItemClickListener listener) {
        super(context, R.layout.item_history, items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScanHistory item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_history, parent, false);
        }

        TextView tvWagon = convertView.findViewById(R.id.tv_wagon_number);
        TextView tvDate = convertView.findViewById(R.id.tv_scan_time);

        tvWagon.setText("Вагон №" + item.getWagonNumber());
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(item.getScanTime()));

        convertView.setOnClickListener(v -> listener.onItemClick(item));
        return convertView;
    }
}