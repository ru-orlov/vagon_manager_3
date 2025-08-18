package com.example.wagonmanager3.models;

public class InventoryTableRow {
    private String groupName;
    private String itemName;
    private int quantity;

    public InventoryTableRow(String groupName, String itemName, int quantity) {
        this.groupName = groupName;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getGroupName() { return groupName; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
}
