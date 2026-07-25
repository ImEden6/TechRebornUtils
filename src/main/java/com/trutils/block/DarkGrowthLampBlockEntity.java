package com.trutils.block;

import com.trutils.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import reborncore.api.IToolDrop;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;

public class DarkGrowthLampBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop {

	public DarkGrowthLampBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.DARK_GROWTH_LAMP_ENTITY, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state, MachineBaseBlockEntity blockEntity) {
		super.tick(level, pos, state, blockEntity);
		if (level == null || level.isClientSide) {
			return;
		}
		Block b = state.getBlock();
		if (!(b instanceof DarkGrowthLampBlock lamp)) {
			return;
		}

		long cost = getEuPerTick(lamp.getCost());
		if (getStored() > cost) {
			useEnergy(cost);
			if (!DarkGrowthLampBlock.isActive(state)) {
				DarkGrowthLampBlock.setActive(true, level, pos);
			}
		} else if (DarkGrowthLampBlock.isActive(state)) {
			DarkGrowthLampBlock.setActive(false, level, pos);
		}
	}

	@Override
	protected boolean canAcceptEnergy(@Nullable Direction side) {
		return side != null;
	}

	@Override
	public boolean canProvideEnergy(@Nullable Direction side) {
		return false;
	}

	@Override
	public long getBaseMaxPower() {
		Block b = getLevel() != null ? getLevel().getBlockState(getBlockPos()).getBlock() : null;
		if (b instanceof DarkGrowthLampBlock lamp) {
			return lamp.getCapacity();
		}
		return 33;
	}

	@Override
	public long getBaseMaxOutput() {
		return 0;
	}

	@Override
	public long getBaseMaxInput() {
		Block b = getLevel() != null ? getLevel().getBlockState(getBlockPos()).getBlock() : null;
		if (b instanceof DarkGrowthLampBlock lamp) {
			return lamp.getMaxInput();
		}
		return 32;
	}

	@Override
	public ItemStack getToolDrop(Player player) {
		if (getLevel() == null) {
			return new ItemStack(ModBlocks.DARK_GROWTH_LAMP);
		}
		return new ItemStack(getLevel().getBlockState(getBlockPos()).getBlock());
	}
}
