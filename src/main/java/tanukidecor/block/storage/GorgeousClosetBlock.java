/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.HorizontalMultiblock;
import tanukidecor.block.entity.StorageBlockEntity;
import tanukidecor.util.MultiblockHandler;

public class GorgeousClosetBlock extends HorizontalMultiblock implements EntityBlock {

    public GorgeousClosetBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X2X1, HorizontalMultiblock.createHorizontalShapeBuilder(MultiblockHandler.MULTIBLOCK_2X2X1, SHAPE), pProperties);
    }

    //// CONTAINER ////
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return StorageBlockEntity.use(pState, pLevel, pPos, pPlayer, pHand, pHit, SoundEvents.BARREL_OPEN);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            StorageBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    //// BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if(pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.GORGEOUS_CLOSET.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }

    //// REDSTONE ////

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][] {
            // height = 0
            {},
            // height = 1
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(13, 0, 1, 15, 3, 3),
                                    box(13, 0, 13, 15, 3, 15),
                                    box(0, 3, 1, 15, 5, 15),
                                    box(0, 5, 2, 14, 16, 14))
                    },
                    // width = 2
                    {
                            Shapes.or(box(1, 0, 1, 3, 3, 3),
                                    box(1, 0, 13, 3, 3, 15),
                                    box(1, 3, 1, 16, 5, 15),
                                    box(2, 5, 2, 16, 16, 14))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 2, 14, 13, 14),
                                    box(0, 13, 0, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.or(box(2, 0, 2, 16, 13, 14),
                                    box(0, 13, 0, 16, 16, 16))
                    }
            }
    };
}
