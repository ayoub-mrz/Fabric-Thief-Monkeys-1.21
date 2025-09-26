package net.ayoubmrz.thiefmonkeymod.datagen;

import net.ayoubmrz.thiefmonkeymod.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {}

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BANANA, Models.GENERATED);
    }

}