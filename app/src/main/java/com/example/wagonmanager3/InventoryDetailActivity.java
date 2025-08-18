package com.example.wagonmanager3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.InventoryItem;
import com.example.wagonmanager3.models.WagonInventory;

import java.util.List;

public class InventoryDetailActivity extends AppCompatActivity {
    private TextView itemName, itemGroup, itemQuantity, itemDescription;
    private RecyclerView wagonsRecyclerView;
    private Button editButton;
    private long itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);

        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId == -1) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadItemData();
        setupEditButton();
    }

    private void initViews() {
        itemName = findViewById(R.id.itemName);
        itemGroup = findViewById(R.id.itemGroup);
        itemQuantity = findViewById(R.id.itemQuantity);
        itemDescription = findViewById(R.id.itemDescription);
        wagonsRecyclerView = findViewById(R.id.wagonsRecyclerView);
        editButton = findViewById(R.id.editButton);

        wagonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadItemData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Загрузка основной информации
        InventoryItem item = dbHelper.getInventoryItemById(itemId);
        if (item != null) {
            itemName.setText(item.getName());
            itemGroup.setText((int) item.getGroupId());
            itemQuantity.setText(String.format("Количество: %d", item.getQuantity()));
            itemDescription.setText(item.getDescription());
            getSupportActionBar().setTitle(item.getName());
        }

        // Загрузка списка вагонов
        List<WagonInventory> wagons = dbHelper.getWagonsForItem(itemId);
        WagonsAdapter adapter = new WagonsAdapter(wagons);
        wagonsRecyclerView.setAdapter(adapter);
    }

    private void setupEditButton() {
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryEditActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class WagonsAdapter extends RecyclerView.Adapter<WagonsAdapter.ViewHolder> {
        private final List<WagonInventory> wagons;

        public WagonsAdapter(List<WagonInventory> wagons) {
            this.wagons = wagons;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wagon_simple, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(wagons.get(position));
        }

        @Override
        public int getItemCount() {
            return wagons.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView wagonNumber;
            private final TextView condition;

            public ViewHolder(View itemView) {
                super(itemView);
                wagonNumber = itemView.findViewById(R.id.wagonNumber);
                condition = itemView.findViewById(R.id.condition);
            }

            public void bind(WagonInventory wagon) {
                wagonNumber.setText((int) wagon.getVagonUuid());
                condition.setText(wagon.getCondition());
                // Можно добавить цветовую индикацию состояния
            }
        }
    }
}
