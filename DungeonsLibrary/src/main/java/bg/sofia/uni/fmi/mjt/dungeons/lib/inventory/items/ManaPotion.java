package bg.sofia.uni.fmi.mjt.dungeons.lib.inventory.items;

import bg.sofia.uni.fmi.mjt.dungeons.lib.enums.ItemType;

import java.util.Objects;
import java.util.Random;

public class ManaPotion implements Item {


    private static final Random random = new Random();

    private static final int MIN_REPLENISHMENT = 20;
    private static final int MAX_REPLENISHMENT = 70;

    public int replenishmentAmount() {
        return random.nextInt(MAX_REPLENISHMENT - MIN_REPLENISHMENT) + MIN_REPLENISHMENT;
    }

    @Override
    public ItemType type() {
        return ItemType.MANA_POTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ItemType.MANA_POTION.ordinal());
    }
}
