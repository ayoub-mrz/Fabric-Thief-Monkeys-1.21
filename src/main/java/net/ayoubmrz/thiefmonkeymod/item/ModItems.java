package net.ayoubmrz.thiefmonkeymod.item;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.ModEntities;
import net.ayoubmrz.thiefmonkeymod.item.custom.ModFoodComponents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    public static final Item THIEF_MONKEY_SPAWN_EGG = registerItem("thief_monkey_spawn_egg",
        setting -> new SpawnEggItem(ModEntities.THIEF_MONKEY, setting));

    public static final Item BANANA = registerItem("banana",
            setting -> new Item(setting.maxCount(16).food(ModFoodComponents.BANANA)));


    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(ThiefMonkeyMod.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ThiefMonkeyMod.MOD_ID, name)))));
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