package net.ayoubmrz.thiefmonkeymod;

import net.ayoubmrz.thiefmonkeymod.entity.ModEntities;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.ayoubmrz.thiefmonkeymod.item.ModItems;
import net.ayoubmrz.thiefmonkeymod.sound.ModSounds;
import net.ayoubmrz.thiefmonkeymod.util.ModLootTableModifiers;
import net.ayoubmrz.thiefmonkeymod.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThiefMonkeyMod implements ModInitializer {
	public static final String MOD_ID = "thiefmonkeymod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerModItems();

		ModEntities.registerModEntities();

		FabricDefaultAttributeRegistry.register(ModEntities.THIEF_MONKEY, ThiefMonkeyEntity.setAttributes());

		ModLootTableModifiers.modifyLootTables();

		ModWorldGeneration.generateModWorldGen();

		ModSounds.registerSounds();
		
	}
}