/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity;

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

public class FoliotClockBER extends ClockBER {

    public static final ResourceLocation SHORT_HAND = new ResourceLocation(TanukiDecor.MODID, "block/foliot_clock/short_hand");
    public static final ResourceLocation GEAR = new ResourceLocation(TanukiDecor.MODID, "block/foliot_clock/gear");

    public FoliotClockBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext, SHORT_HAND, null,
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
        final float time = blockEntity.getLevel().getGameTime() + renderHelper.getPartialTick();
        final float angle = 0.00125F * time * Mth.TWO_PI;

        final BakedModel gear = Minecraft.getInstance().getModelManager().getModel(GEAR);
        final Vec3 gearPosition = new Vec3(-8.0D / 16.0D, -8.0D / 16.0D, 0);

        this.clockRenderHelper
                .withModel(gear)
                .withPosition(gearPosition)
                .withPivotPoint(new Vec3(8.0D / 16.0D, 4.0D / 16.0D, 0))
                .withRotationZ(angle)
                .render(blockRenderer);

        this.clockRenderHelper
                .withPosition(gearPosition.add(1.0D / 16.0D, 2.0D / 16.0D, 2.01D / 16.0D))
                .withRotationZ(-2.0F * angle)
                .render(blockRenderer);

        this.clockRenderHelper
                .withPosition(gearPosition.add(0, 4.0D / 16.0D, 0))
                .withRotationZ(4.0F * angle)
                .render(blockRenderer);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(SHORT_HAND);
        list.add(GEAR);
    }
}
