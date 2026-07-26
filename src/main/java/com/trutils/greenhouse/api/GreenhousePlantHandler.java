package com.trutils.greenhouse.api;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface GreenhousePlantHandler {
    /**
     * @param state the block state at the scanned position
     * @return true if this handler can process the given block state
     */
    boolean canHandle(BlockState state);

    /**
     * Returns a harvest offer for the given block, or null if the crop isn't ready.
     * <p>
     * This method may read and modify the world (e.g. break connected blocks).
     * The returned {@link HarvestOffer} specifies the items to collect and the
     * replacement block state. If {@code newState} is null, the mixin will
     * destroy the block via {@code destroyBlock} instead.
     *
     * @param world the server world
     * @param pos   the position of the block
     * @param state the current block state at {@code pos}
     * @return a harvest offer, or null if the crop should not be harvested yet
     */
    @Nullable
    HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state);
}
