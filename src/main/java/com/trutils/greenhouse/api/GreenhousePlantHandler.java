package com.trutils.greenhouse.api;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface GreenhousePlantHandler {
    boolean canHandle(BlockState state);

    @Nullable
    HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state);
}
