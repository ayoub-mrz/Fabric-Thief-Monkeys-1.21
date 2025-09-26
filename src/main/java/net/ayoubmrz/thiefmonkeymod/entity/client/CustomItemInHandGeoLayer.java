package net.ayoubmrz.thiefmonkeymod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemInHandGeoLayer;

public class CustomItemInHandGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends GeoRenderState> extends ItemInHandGeoLayer<T, O, R> {

    public CustomItemInHandGeoLayer(GeoRenderer<T, O, R> renderer) { super(renderer); }

    public CustomItemInHandGeoLayer(GeoRenderer<T, O, R> renderer, String rightHandBoneName, String leftHandBoneName) {
        super(renderer, rightHandBoneName, leftHandBoneName);
    }

    @Override
    protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack,
                                      ItemDisplayContext displayContext, R renderState,
                                      VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {

        poseStack.push();

        // Custom scale and translation
        poseStack.scale(0.5f, 0.5f, 0.5f); // Scale down to 50%

        // Call the parent method to apply default transformations and render
        super.renderStackForBone(poseStack, bone, stack, displayContext, renderState, bufferSource, packedLight, packedOverlay);

        poseStack.pop();
    }
}
