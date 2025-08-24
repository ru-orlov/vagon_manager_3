package com.example.wagonmanager3;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.wagonmanager3.models.InventoryItem;

/**
 * Unit tests for DatabaseHelper methods related to inventory item loading
 */
public class DatabaseHelperTest {

    @Test
    public void inventoryItem_hasRequiredFields() {
        // Test that InventoryItem model has all required fields for the edit form
        InventoryItem item = new InventoryItem();
        
        // Test that we can set all required fields
        item.setId(1L);
        item.setUuid("test-uuid");
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setQuantity(5);
        item.setGroupId("group-uuid");
        item.setVagonUuid("vagon-uuid");
        item.setSyncStatus("synced");
        
        // Verify that all fields are properly set
        assertEquals(1L, item.getId());
        assertEquals("test-uuid", item.getUuid());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertEquals(5, item.getQuantity());
        assertEquals("group-uuid", item.getGroupId());
        assertEquals("vagon-uuid", item.getVagonUuid());
        assertEquals("synced", item.getSyncStatus());
    }

    @Test
    public void inventoryItem_toStringWorks() {
        // Test that toString method works for displaying items
        InventoryItem item = new InventoryItem();
        item.setName("Test Item");
        item.setQuantity(3);
        
        String expected = "Test Item (Кол-во: 3)";
        assertEquals(expected, item.toString());
    }

    @Test
    public void inventoryItem_noArgConstructorWorks() {
        // Test that no-argument constructor works
        InventoryItem item = new InventoryItem();
        assertNotNull(item);
        
        // Test that default values don't cause issues
        assertEquals(0, item.getQuantity());
        assertNull(item.getName());
        assertNull(item.getDescription());
    }
}