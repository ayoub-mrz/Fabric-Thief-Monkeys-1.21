package net.ayoubmrz.thiefmonkeymod;

import net.ayoubmrz.thiefmonkeymod.entity.ModEntities;
import net.ayoubmrz.thiefmonkeymod.entity.client.ThiefMonkeyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ThiefMonkeyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.THIEF_MONKEY, ThiefMonkeyRenderer::new);

    }
}
