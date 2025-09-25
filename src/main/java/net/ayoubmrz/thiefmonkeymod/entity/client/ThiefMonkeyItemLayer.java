package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ThiefMonkeyItemLayer extends GeoRenderLayer<ThiefMonkeyEntity> {
    public ThiefMonkeyItemLayer(GeoRenderer<ThiefMonkeyEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, ThiefMonkeyEntity animatable, BakedGeoModel bakedModel,
                       RenderLayer renderType, VertexConsumerProvider bufferSource,
                       VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        ItemStack heldItem = animatable.getHeldItem();
        if (!heldItem.isEmpty()) {
            GeoBone handBone = this.getGeoModel().getBone("right_hand").orElse(null);
            if (handBone != null) {
                poseStack.push();

                poseStack.translate(handBone.getPosX() / 16.0f, handBone.getPosY() / 16.0f, handBone.getPosZ() / 16.0f);

                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-animatable.bodyYaw));

                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(handBone.getRotX()));
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handBone.getRotY()));
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(handBone.getRotZ()));

                poseStack.translate(0.2, 0.32, 0.17);

                poseStack.scale(0.4f, 0.4f, 0.4f);

                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

                ItemRenderer itemRenderer = net.minecraft.client.MinecraftClient.getInstance().getItemRenderer();
                itemRenderer.renderItem(heldItem, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                        packedLight, packedOverlay, poseStack, bufferSource,
                        animatable.getWorld(), 0);
                poseStack.pop();
            }
        }
    }
}
