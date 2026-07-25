package com.trutils.greenhouse.handler;

import com.trutils.greenhouse.api.GreenhousePlantHandler;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class HugeMushroomHandler implements GreenhousePlantHandler {
    @Override
    public boolean canHandle(BlockState state) {
        return state.getBlock() instanceof HugeMushroomBlock;
    }

    @Override
    @Nullable
    public HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state) {
        var drops = Block.getDrops(state, world, pos, null);
        return new HarvestOffer(drops, null);
    }
}
