package net.ayoubmrz.thiefmonkeymod.entity;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<ThiefMonkeyEntity> THIEF_MONKEY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(ThiefMonkeyMod.MOD_ID, "thief_monkey"),
            EntityType.Builder.create(ThiefMonkeyEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.6f).build());


    public static void registerModEntities() {
        ThiefMonkeyMod.LOGGER.info("Registering Mod Goblin for " + ThiefMonkeyMod.MOD_ID);
    }
}
