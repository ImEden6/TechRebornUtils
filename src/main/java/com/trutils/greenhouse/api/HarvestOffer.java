package com.trutils.greenhouse.api;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public record HarvestOffer(@NotNull List<ItemStack> drops, @Nullable BlockState newState) {
}
