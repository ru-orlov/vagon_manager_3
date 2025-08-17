package com.example.wagonmanager3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.R;
import com.example.wagonmanager3.models.ScanHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {
    private List<ScanHistory> scanHistoryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ScanHistory scanHistory);
        void onItemLongClick(ScanHistory scanHistory);
    }

    public ScanHistoryAdapter(List<ScanHistory> scanHistoryList, OnItemClickListener listener) {
        this.scanHistoryList = scanHistoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanHistory history = scanHistoryList.get(position);
        holder.bind(history, listener);
    }

    @Override
    public int getItemCount() {
        return scanHistoryList.size();
    }

    public void updateData(List<ScanHistory> newList) {
        scanHistoryList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvWagonNumber;
        private final TextView tvScanTime;

        public ViewHolder(View view) {
            super(view);
            tvWagonNumber = view.findViewById(R.id.tv_wagon_number);
            tvScanTime = view.findViewById(R.id.tv_scan_time);
        }

        public void bind(ScanHistory history, OnItemClickListener listener) {
            tvWagonNumber.setText("Вагон №" + history.getWagonNumber());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            tvScanTime.setText(sdf.format(history.getScanTime()));

            itemView.setOnClickListener(v -> listener.onItemClick(history));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(history);
                return true;
            });
        }
    }
}
