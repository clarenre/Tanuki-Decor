/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.IChimeProvider;

import java.util.Random;

public class ClockBlockEntity extends BlockEntity {

    protected static final long CHIME_INTERVAL = 40L;

    protected final @Nullable IChimeProvider chimeProvider;

    protected long chimeTimestamp;
    protected long nextChimeTimestamp;


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
        if(null == chimeProvider) {
            return;
        }
        final long dayTime = level.getDayTime();
        final Random random = level.getRandom();
        // attempt to play chime sound
        final long chimeTimeRemaining = nextChimeTimestamp - chimeTimestamp;
        final SoundEvent chimeSound = chimeProvider.getChimeSound();
        if(chimeTimeRemaining == 0 && chimeSound != null) {
            final float pitch = chimeProvider.getChimePitch(random, dayTime);
            final float volume = chimeProvider.getChimeVolume(random, dayTime);
            level.playSound(null, blockPos, chimeSound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    protected void playChime(Level level, BlockPos blockPos, BlockState blockState) {
        if(null == chimeProvider) {
            return;
        }
        final long dayTime = level.getDayTime();
        final Random random = level.getRandom();
        // attempt to play tick sound
        final SoundEvent tickSound = chimeProvider.getTickSound();
        if(dayTime % 20 == 0 && tickSound != null) {
            final float pitch = chimeProvider.getTickPitch(random, dayTime);
            final float volume = chimeProvider.getTickVolume(random, dayTime);
            level.playSound(null, blockPos, tickSound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    public float getHour() {
        return (level.getDayTime()) / 1000.0F;
    }

    public float getMinute() {
        return (level.getDayTime()) / 100.0F; // TODO fix math
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1);
    }
}
