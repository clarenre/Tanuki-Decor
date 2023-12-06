/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.ClockBlockEntity;

import java.util.Set;

public class StationClockBER extends ClockBER {

    public static final ResourceLocation SHORT_HAND = new ResourceLocation(TanukiDecor.MODID, "block/station_clock/short_hand");
    public static final ResourceLocation LONG_HAND = new ResourceLocation(TanukiDecor.MODID, "block/station_clock/long_hand");

    public StationClockBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext, SHORT_HAND, LONG_HAND,
                new Vec3(8.0D / 16.0D, 8.0D / 16.0D, 8.0D / 16.0D),
                ROOT_PIVOT_POINT,
                new Vec3(-8.0D / 16.0D, 8.0D / 16.0D, -1.48D / 16.0D),
                new Vec3(8.0D / 16.0D, 8.0D / 16.0D, 0));
    }

    @Override
    public void render(ClockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final BlockState blockState = pBlockEntity.getBlockState();
        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final Level level = pBlockEntity.getLevel();
        // query the day time with an offset to ensure 18000 is noon (etc)
        final long dayTime = (level.getDayTime() + 6000L) % 24000L;
        final float hourAngleInterval = Mth.TWO_PI / 12.0F;
        final float hourRotation = ClockBlockEntity.getHour(dayTime, pPartialTick) * hourAngleInterval;
        final float minuteRotation = ClockBlockEntity.getMinute(dayTime, 0.98F) * Mth.TWO_PI;

        // start rendering
        pPoseStack.pushPose();

        // prepare render helper
        this.clockRenderHelper
                .withPoseStack(pPoseStack)
                .withBlockState(blockState)
                .withRenderType(pBufferSource, RenderType.cutout())
                .withPackedLight(pPackedLight)
                .withPackedOverlay(pPackedOverlay)
                .withPartialTick(pPartialTick)
                .withRotationZ(0);

        // rotate pose stack
        this.clockRenderHelper
                .withPosition(this.rootPosition)
                .withPivotPoint(this.rootPivotPoint)
                .rotateForDirection(direction);

        // don't ask why it works, it just does
        pPoseStack.translate(-8.0D / 16.0D, -8.0D / 16.0D, -6.5D / 16.0D);

        // render on front and back
        for(int i = 0; i < 2; i++) {

            // render short hand
            this.clockRenderHelper
                    .withModel(mc.getModelManager().getModel(this.shortHand))
                    .withPosition(this.handsPosition)
                    .withPivotPoint(this.handsPivotPoint)
                    .withRotationZ(hourRotation)
                    .render(blockRenderer);

            // render long hand
            this.clockRenderHelper
                    .withModel(mc.getModelManager().getModel(this.longHand))
                    .withRotationZ(minuteRotation)
                    .render(blockRenderer);

            renderAdditional(this.clockRenderHelper, pBlockEntity, pBufferSource);

            pPoseStack.mulPose(Vector3f.YP.rotation(Mth.PI));
            pPoseStack.translate(0, 0, -13.0D / 16.0D);
        }

        // finish rendering
        pPoseStack.popPose();
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(SHORT_HAND);
        list.add(LONG_HAND);
    }
}
