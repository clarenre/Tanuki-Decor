/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.clock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.ClockBlockEntity;

import java.util.Set;

public class GorgeousClockBER extends ClockBER {

    public static final ResourceLocation LONG_HAND = new ResourceLocation(TanukiDecor.MODID, "block/gorgeous_clock/long_hand");
    public static final ResourceLocation SHORT_HAND = new ResourceLocation(TanukiDecor.MODID, "block/gorgeous_clock/short_hand");
    public static final ResourceLocation PENDULUM = new ResourceLocation(TanukiDecor.MODID, "block/gorgeous_clock/pendulum");

    private static final Vec3 PENDULUM_POSITION = new Vec3(-8.0D / 16.0D, -24.0D / 16.0D, 0);
    private static final Vec3 PENDULUM_PIVOT_POINT = new Vec3(8.0D / 16.0D, 16.0D / 16.0D, 0);

    public GorgeousClockBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext, SHORT_HAND, LONG_HAND,
                ROOT_POSITION,
                ROOT_PIVOT_POINT,
                HANDS_POSITION,
                new Vec3(8.0D / 16.0D, 6.0D / 16.0D, 0));
    }

    @Override
    public void render(ClockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    @Override
    public void renderAdditional(ClockRenderHelper renderHelper, ClockBlockEntity blockEntity, MultiBufferSource bufferSource) {
        final float pendulumRotation = blockEntity.getBias() * getPendulumRotation(1.0F, 8 * Mth.DEG_TO_RAD, blockEntity.getLevel().getGameTime(), renderHelper.getPartialTick());

        final BakedModel pendulum = Minecraft.getInstance().getModelManager().getModel(PENDULUM);

        // render pendulum
        renderHelper
                .withModel(pendulum)
                .withPosition(PENDULUM_POSITION)
                .withPivotPoint(PENDULUM_PIVOT_POINT)
                .withRotationZ(pendulumRotation)
                .render(blockRenderer);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(LONG_HAND);
        list.add(SHORT_HAND);
        list.add(PENDULUM);
    }

}
