package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.util.Identifier;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ThiefMonkeyModel extends DefaultedEntityGeoModel<ThiefMonkeyEntity> {

    public ThiefMonkeyModel() {
        super(Identifier.of(ThiefMonkeyMod.MOD_ID, "thief_monkey"), true);
    }

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "thief_monkey.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "textures/entity/thief_monkey.png");
    }

    @Override
    public Identifier getAnimationResource(ThiefMonkeyEntity thiefMonkeyEntity) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "thief_monkey.animation.json");
    }

}
