package net.ayoubmrz.thiefmonkeymod.item;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.ModEntities;
import net.ayoubmrz.thiefmonkeymod.item.custom.ModFoodComponents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item THIEF_MONKEY_SPAWN_EGG = registerItem("thief_monkey_spawn_egg",
            new SpawnEggItem(ModEntities.THIEF_MONKEY, 0xffd3b0, 0xb35e26, new Item.Settings()));

    public static final Item BANANA = registerItem("banana",
            new Item( new Item.Settings().maxCount(16).food(ModFoodComponents.BANANA)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ThiefMonkeyMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ThiefMonkeyMod.LOGGER.info("Registering Mod Items for " + ThiefMonkeyMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(THIEF_MONKEY_SPAWN_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(BANANA);
        });
    }
}