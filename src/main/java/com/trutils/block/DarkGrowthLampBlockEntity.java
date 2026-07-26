package com.trutils.block;

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
		super(DarkGrowthLampBlock.ENTITY_TYPE, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state, MachineBaseBlockEntity blockEntity) {
		super.tick(level, pos, state, blockEntity);
		if (level == null || level.isClientSide) {
			return;
		}
		DarkGrowthLampBlock lamp = getLampBlock();
		if (lamp == null) {
			return;
		}

		long cost = getEuPerTick(lamp.getCost());
		if (getStored() >= cost) {
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

	private @Nullable DarkGrowthLampBlock getLampBlock() {
		if (getLevel() == null)
			return null;
		Block b = getLevel().getBlockState(getBlockPos()).getBlock();
		return b instanceof DarkGrowthLampBlock lamp ? lamp : null;
	}

	@Override
	public long getBaseMaxPower() {
		DarkGrowthLampBlock lamp = getLampBlock();
		if (lamp != null) {
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
		DarkGrowthLampBlock lamp = getLampBlock();
		if (lamp != null) {
			return lamp.getMaxInput();
		}
		return 32;
	}

	@Override
	public ItemStack getToolDrop(Player player) {
		BlockState state = getLevel() != null ? getLevel().getBlockState(getBlockPos()) : null;
		return new ItemStack(state != null ? state.getBlock() : DarkGrowthLampBlock.INSTANCE);
	}
}
