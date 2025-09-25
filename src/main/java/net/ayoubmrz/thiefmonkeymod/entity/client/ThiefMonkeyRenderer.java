package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.ayoubmrz.thiefmonkeymod.ThiefMonkeyMod;
import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThiefMonkeyRenderer extends GeoEntityRenderer<ThiefMonkeyEntity> {

    public ThiefMonkeyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThiefMonkeyModel<>());
        this.addRenderLayer(new ThiefMonkeyItemLayer(this));
    }

    @Override
    public Identifier getTextureLocation(ThiefMonkeyEntity animatable) {
        return Identifier.of(ThiefMonkeyMod.MOD_ID, "textures/entity/thief_monkey.png");
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, ThiefMonkeyEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);

        if (animatable.isBaby()) {
            poseStack.scale(0.8f, 0.8f, 0.8f);
        } else {
            poseStack.scale(1.5f, 1.5f, 1.5f);
        }
    }

}
