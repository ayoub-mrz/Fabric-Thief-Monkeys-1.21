package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ThiefMonkeyModel<T extends ThiefMonkeyEntity> extends GeoModel<ThiefMonkeyEntity> {


    @Override
    public Identifier getModelResource(ThiefMonkeyEntity thiefMonkeyEntity, @Nullable GeoRenderer<ThiefMonkeyEntity> geoRenderer) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "geo/thief_monkey.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThiefMonkeyEntity thiefMonkeyEntity, @Nullable GeoRenderer<ThiefMonkeyEntity> geoRenderer) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "textures/entity/thief_monkey.png");
    }

    @Override
    public Identifier getAnimationResource(ThiefMonkeyEntity thiefMonkeyEntity) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "animations/thief_monkey.animation.json");
    }

    @Override
    public void setCustomAnimations(ThiefMonkeyEntity animatable, long instanceId, AnimationState<ThiefMonkeyEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }


}
