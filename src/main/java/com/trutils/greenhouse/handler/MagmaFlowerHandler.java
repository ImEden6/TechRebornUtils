package com.trutils.greenhouse.handler;

import com.trutils.greenhouse.api.GreenhousePlantHandler;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class MagmaFlowerHandler implements GreenhousePlantHandler {

    @Override
    public boolean canHandle(BlockState state) {
        var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return key.getNamespace().equals("betternether") && key.getPath().equals("magma_flower");
    }

    @Override
    @Nullable
    public HarvestOffer getHarvestOffer(ServerLevel world, BlockPos pos, BlockState state) {
        IntegerProperty ageProp = null;
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty ip) {
                ageProp = ip;
                break;
            }
        }
        if (ageProp == null) {
            return null;
        }
        int age = state.getValue(ageProp);
        if (age < 3) {
            return null;
        }
        var drops = Block.getDrops(state, world, pos, null);
        var newState = state.setValue(ageProp, 0);
        return new HarvestOffer(drops, newState);
    }
}
