package bg.sofia.uni.fmi.mjt.dungeons.lib.inventory;

import bg.sofia.uni.fmi.mjt.dungeons.lib.enums.ItemType;
import bg.sofia.uni.fmi.mjt.dungeons.lib.exceptions.ItemNumberOutOfBoundsException;
import bg.sofia.uni.fmi.mjt.dungeons.lib.inventory.items.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeons.lib.inventory.items.Item;
import bg.sofia.uni.fmi.mjt.dungeons.lib.inventory.items.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeons.lib.network.Transmissible;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Transmissible {

    private static final int INVENTORY_SIZE = 9;
    private List<Item> items;

    public Inventory() {
        items = new ArrayList<>(INVENTORY_SIZE);
    }

    public void addItemToInventory(Item item) {
        if (items.size() < INVENTORY_SIZE) {
            items.add(item);
        }
    }

    public int currentSize() {
        return items.size();
    }

    // Items are 1-9, indexes of the list are 0-8
    public Item getItem(int itemNumber) throws ItemNumberOutOfBoundsException {
        assertItemNumberInBounds(itemNumber);
        return items.get(itemNumber - 1);
    }

    public Item removeItem(int itemNumber) throws ItemNumberOutOfBoundsException {
        assertItemNumberInBounds(itemNumber);
        return items.remove(itemNumber - 1);
    }

    private void assertItemNumberInBounds(int itemNumber) throws ItemNumberOutOfBoundsException {
        if (itemNumber > items.size()) {
            throw new ItemNumberOutOfBoundsException();
        }
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(items.size());
        for (Item item : items) {
            out.writeInt(item.type().ordinal());
            item.serialize(out);
        }
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        int itemsCount = in.readInt();
        for (int i = 1; i <= itemsCount; i++) {
            ItemType itemType = ItemType.values()[in.readInt()];
            items.add(switch (itemType) {
                case HEALTH_POTION -> new HealthPotion();
                case MANA_POTION -> new ManaPotion();
            });
        }
    }
}