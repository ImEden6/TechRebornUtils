package com.trutils.greenhouse.handler;

import com.trutils.greenhouse.api.GreenhousePlantHandler;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ChorusFlowerHandler implements GreenhousePlantHandler {
    private static final int CHORUS_MAX_AGE = 5;

    @Override
    public boolean canHandle(BlockState state) {
        return state.getBlock() instanceof ChorusFlowerBlock;
    }

    @Override
    @Nullable
    public HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state) {
        if (state.getValue(ChorusFlowerBlock.AGE) < CHORUS_MAX_AGE) {
            return null;
        }
        var drops = Block.getDrops(state, world, pos, null);
        var newState = state.setValue(ChorusFlowerBlock.AGE, 0);
        return new HarvestOffer(drops, newState);
    }
}
