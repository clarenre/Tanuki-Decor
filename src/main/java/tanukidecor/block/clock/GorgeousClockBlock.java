/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.HorizontalDoubleBlock;
import tanukidecor.block.entity.ClockBlockEntity;

import java.util.function.Supplier;

public class GorgeousClockBlock extends HorizontalDoubleBlock implements EntityBlock, IChimeProvider {

    protected final Supplier<SoundEvent> tickSound;
    protected final Supplier<SoundEvent> chimeSound;

    public static final VoxelShape UPPER_SHAPE = box(2, 0, 9, 14, 12, 16);
    public static final VoxelShape LOWER_SHAPE = box(4, 4, 11, 12, 16, 16);

    public GorgeousClockBlock(Supplier<SoundEvent> tickSound, Supplier<SoundEvent> chimeSound, Properties pProperties) {
        super(pProperties, HorizontalDoubleBlock.createShapeBuilder(UPPER_SHAPE, LOWER_SHAPE));
        this.tickSound = tickSound;
        this.chimeSound = chimeSound;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(HALF, DoubleBlockHalf.UPPER));
    }

    //// CHIME PROVIDER ////

    @Nullable
    @Override
    public SoundEvent getTickSound() {
        return this.tickSound.get();
    }

    @Nullable
    @Override
    public SoundEvent getChimeSound() {
        return this.chimeSound.get();
    }

    //// PLACEMENT ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;
        if (pContext.getClickedFace().getAxis() != Direction.Axis.Y
                && blockpos.getY() > level.getMinBuildHeight()
                && level.getBlockState(blockpos.below()).canBeReplaced(pContext)) {
            return this.defaultBlockState()
                    .setValue(FACING, pContext.getClickedFace())
                    .setValue(WATERLOGGED, waterlogged)
                    .setValue(HALF, DoubleBlockHalf.UPPER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        boolean waterlogged = pLevel.getFluidState(pPos.below()).getType() == Fluids.WATER;
        pLevel.setBlock(pPos.below(), pState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        final Direction facing = pState.getValue(FACING);
        final BlockPos supportingPos = pPos.relative(facing.getOpposite());
        return (pState.getValue(HALF) == DoubleBlockHalf.UPPER && pLevel.getBlockState(supportingPos).isFaceSturdy(pLevel, supportingPos, facing))
                || pLevel.getBlockState(pPos.above()).is(this);
    }

    //// BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if(pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return TDRegistry.BlockEntityReg.GORGEOUS_CLOCK.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<ClockBlockEntity>) (ClockBlockEntity::tick) : null;
    }
}