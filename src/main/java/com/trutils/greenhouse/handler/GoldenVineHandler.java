package com.trutils.greenhouse.handler;

import com.trutils.greenhouse.api.GreenhousePlantHandler;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class GoldenVineHandler implements GreenhousePlantHandler {

    @Override
    public boolean canHandle(BlockState state) {
        var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return key.getNamespace().equals("betternether") && key.getPath().equals("golden_vine");
    }

    @Override
    @Nullable
    public HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state) {
        BooleanProperty bottomProp = null;
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals("bottom") && prop instanceof BooleanProperty bp) {
                bottomProp = bp;
                break;
            }
        }
        if (bottomProp == null) {
            return null;
        }
        // Only harvest if it's the bottom block
        boolean isBottom = state.getValue(bottomProp);
        if (!isBottom) {
            return null;
        }
        // To preserve the root, make sure the block above is also a golden vine
        BlockState aboveState = world.getBlockState(pos.above());
        if (aboveState.getBlock() != state.getBlock()) {
            return null; // Don't harvest the root block
        }
        var drops = Block.getDrops(state, world, pos, null);
        // Replace with AIR to harvest the bottom block
        var newState = Blocks.AIR.defaultBlockState();
        return new HarvestOffer(drops, newState);
    }
}
