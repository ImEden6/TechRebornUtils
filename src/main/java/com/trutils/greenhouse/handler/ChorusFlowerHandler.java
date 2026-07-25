package com.trutils.greenhouse.handler;

import com.trutils.greenhouse.api.GreenhousePlantHandler;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

        // Trace down to find the base block of the chorus tree
        BlockPos basePos = pos;
        while (world.getBlockState(basePos.below()).getBlock() instanceof ChorusPlantBlock ||
               world.getBlockState(basePos.below()).getBlock() instanceof ChorusFlowerBlock) {
            basePos = basePos.below();
        }

        // Find all flowers on this tree
        List<BlockPos> flowers = new ArrayList<>();
        List<BlockPos> visited = new ArrayList<>();
        findFlowers(world, basePos, flowers, visited);

        // Only harvest if ALL flowers on the tree are mature
        boolean allMature = true;
        for (BlockPos flowerPos : flowers) {
            BlockState flowerState = world.getBlockState(flowerPos);
            if (flowerState.getValue(ChorusFlowerBlock.AGE) < CHORUS_MAX_AGE) {
                allMature = false;
                break;
            }
        }

        if (!allMature) {
            return null;
        }

        // Recursively harvest the entire tree and collect all drops
        List<ItemStack> drops = new ArrayList<>();
        harvestChorusNode(world, basePos, drops);

        // Return all collected drops and set the harvested flower block to AIR
        return new HarvestOffer(drops, Blocks.AIR.defaultBlockState());
    }

    private void findFlowers(ServerLevel world, BlockPos pos, List<BlockPos> flowers, List<BlockPos> visited) {
        if (visited.contains(pos)) return;
        visited.add(pos);

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ChorusFlowerBlock) {
            flowers.add(pos);
            // Also check neighbors in case of branching directly from a flower (rare, but safe)
            for (Direction dir : Direction.values()) {
                findFlowers(world, pos.relative(dir), flowers, visited);
            }
        } else if (state.getBlock() instanceof ChorusPlantBlock) {
            for (Direction dir : Direction.values()) {
                findFlowers(world, pos.relative(dir), flowers, visited);
            }
        }
    }

    private void harvestChorusNode(ServerLevel world, BlockPos pos, List<ItemStack> drops) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ChorusPlantBlock || state.getBlock() instanceof ChorusFlowerBlock) {
            drops.addAll(Block.getDrops(state, world, pos, null));
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            for (Direction dir : Direction.values()) {
                harvestChorusNode(world, pos.relative(dir), drops);
            }
        }
    }
}
