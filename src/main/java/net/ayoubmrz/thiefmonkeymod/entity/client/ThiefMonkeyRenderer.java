package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class ThiefMonkeyRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ThiefMonkeyEntity, R> {

    public ThiefMonkeyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThiefMonkeyModel());
        addRenderLayer(new CustomItemInHandGeoLayer<>(this));
    }


    @Override
    public Identifier getTextureLocation(R renderState) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "textures/entity/thief_monkey.png");
    }

    @Override
    public void scaleModelForRender(R renderState, float widthScale, float heightScale, MatrixStack poseStack, BakedGeoModel model, boolean isReRender) {
        if (renderState instanceof LivingEntityRenderState livingState) {
            if (livingState.baby) {
                poseStack.scale(0.8f, 0.8f, 0.8f);
            } else {
                poseStack.scale(1.5f, 1.5f, 1.5f);
            }
        } else {
            poseStack.scale(1.5f, 1.5f, 1.5f);
        }
    }

}
