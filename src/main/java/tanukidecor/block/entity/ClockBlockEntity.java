/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.IChimeProvider;

import java.util.Random;

public class ClockBlockEntity extends BlockEntity {

    protected final @Nullable IChimeProvider chimeProvider;

    public ClockBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.chimeProvider = (pBlockState.getBlock() instanceof IChimeProvider provider) ? provider : null;
    }

    /**
     * Updates the block entity each tick
     * @param level the level
     * @param blockPos the block entity position
     * @param blockState the block state
     * @param blockEntity the block entity
     */
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, ClockBlockEntity blockEntity) {
        // verify server side
        if(level.isClientSide()) {
            return;
        }
        // attempt to tick
        blockEntity.playTick(level, blockPos, blockState);
        // attempt to chime
        blockEntity.playChime(level, blockPos, blockState);
    }

    protected void playTick(Level level, BlockPos blockPos, BlockState blockState) {
        // validate chime provider
        if(null == chimeProvider) {
            return;
        }
        final long gameTime = level.getGameTime();
        final long dayTime = level.getDayTime();
        final Random random = level.getRandom();
        // attempt to play tick sound
        final SoundEvent tickSound = chimeProvider.getTickSound();
        if(tickSound != null && gameTime % chimeProvider.getTickSoundInterval() == 0) {
            final float pitch = chimeProvider.getTickPitch(random, dayTime);
            final float volume = chimeProvider.getTickVolume(random, dayTime);
            level.playSound(null, blockPos, tickSound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    protected void playChime(Level level, BlockPos blockPos, BlockState blockState) {
        // validate chime provider
        if(null == chimeProvider) {
            return;
        }
        // verify day cycle enabled
        if(!level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            return;
        }
        final long dayTime = level.getDayTime();
        final Random random = level.getRandom();
        // attempt to play chime sound
        final SoundEvent chimeSound = chimeProvider.getChimeSound();
        if(chimeSound != null && this.chimeProvider.isTimeToChime(dayTime)) {
            final float pitch = chimeProvider.getChimePitch(random, dayTime);
            final float volume = chimeProvider.getChimeVolume(random, dayTime);
            level.playSound(null, blockPos, chimeSound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    /**
     * @param dayTime the day time
     * @param partialTick the partial tick
     * @return the current hour from 0 to 24
     */
    public static float getHour(final long dayTime, final float partialTick) {
        return Mth.lerp(partialTick, dayTime - 1, dayTime) / 1000.0F;
    }

    /**
     * @param dayTime the day time
     * @param partialTick the partial tick
     * @return the current hour progress from 0 to 1
     */
    public static float getMinute(final long dayTime, final float partialTick) {
        final int minute = (int) (dayTime % 1000);
        return (Mth.lerp(partialTick, minute - 1, minute) / 1000.0F);
    }

    /**
     * @param dayTime the day time
     * @param partialTick the partial tick
     * @return the current second
     */
    public static float getSecond(final long dayTime, final float partialTick) {
        return Mth.lerp(partialTick, dayTime - 1, dayTime) / 20;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1);
    }
}
