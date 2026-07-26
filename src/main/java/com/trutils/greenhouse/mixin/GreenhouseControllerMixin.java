package com.trutils.greenhouse.mixin;

import com.trutils.block.DarkGrowthLampBlock;
import com.trutils.greenhouse.api.GreenhousePlantRegistry;
import com.trutils.greenhouse.api.HarvestOffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.common.blockentity.MultiblockWriter;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import techreborn.blockentity.machine.tier1.GreenhouseControllerBlockEntity;
import techreborn.blocks.lighting.LampBlock;
import techreborn.config.TechRebornConfig;
import techreborn.init.TRContent;

import java.util.List;

@Mixin(GreenhouseControllerBlockEntity.class)
public abstract class GreenhouseControllerMixin {

	@Shadow(remap = false)
	private BlockPos multiblockCenter;
	@Shadow(remap = false)
	private int workingIndex;
	@Shadow(remap = false)
	private int range;
	@Shadow(remap = false)
	private boolean growthBoost;

	@Shadow(remap = false)
	private boolean insertIntoInv(List<ItemStack> stacks) {
		throw new AbstractMethodError("Shadow");
	}

	@Unique
	private int capturedWorkingIndex;

	@Unique
	private int getCeilingUpgradeCount() {
		var self = (reborncore.common.blockentity.MachineBaseBlockEntity) (Object) this;
		net.minecraft.world.Container upgradeInv = self.getUpgradeInventory();
		if (upgradeInv == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < upgradeInv.getContainerSize(); i++) {
			ItemStack stack = upgradeInv.getItem(i);
			if (!stack.isEmpty() && stack.is(com.trutils.init.ModItems.GREENHOUSE_CEILING_UPGRADE)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	@Unique
	private static boolean isValidLamp(net.minecraft.world.level.BlockGetter world, BlockPos pos) {
		BlockState s = world.getBlockState(pos);
		return LampBlock.isActive(s) || DarkGrowthLampBlock.isActive(s);
	}

	/**
	 * @author Mervyn
	 * @reason Fully replaces the multiblock pattern to support adjustable ceiling
	 * height via the Greenhouse Ceiling Upgrade, which the vanilla TechReborn
	 * pattern does not account for.
	 */
	@Overwrite(remap = false)
	public void writeMultiblock(MultiblockWriter writer) {
		BlockState lamp = TRContent.Machine.LAMP_INCANDESCENT.block.defaultBlockState()
				.setValue(BlockStateProperties.FACING, Direction.DOWN);
		BlockState crop = Blocks.CACTUS.defaultBlockState();

		int count = getCeilingUpgradeCount();
		int lampY = Math.min(3 + count, 6);

		for (int i = 0; i < 3; i++) {
			for (int j = -1; j < 2; j++) {
				writer.add(i * 3 + 2, lampY, j * 3, GreenhouseControllerMixin::isValidLamp, lamp);
			}
		}

		for (int i = 1; i <= 9; i++) {
			for (int j = -4; j <= 4; j++) {
				writer.add(i, 0, j, (world, pos) -> true, crop);
			}
		}
	}

	@Inject(method = "workCycle", at = @At("HEAD"), remap = false)
	private void onWorkCycleHead(CallbackInfo ci) {
		capturedWorkingIndex = workingIndex;
	}

	@Inject(method = "workCycle", at = @At("TAIL"), remap = false)
	private void onWorkCycleTail(CallbackInfo ci) {
		Level level = ((net.minecraft.world.level.block.entity.BlockEntity) (Object) this).getLevel();
		if (level == null || level.isClientSide) {
			return;
		}

		int size = range * 2 + 1;
		int offsetX = capturedWorkingIndex % size;
		int offsetZ = capturedWorkingIndex / size;
		BlockPos corner = multiblockCenter.offset(-range, 0, -range);
		int count = getCeilingUpgradeCount();
		int maxScanY = Math.min(3 + count, 6);

		var self = (PowerAcceptorBlockEntity) (Object) this;

		for (int y = maxScanY - 1; y >= 0; y--) {
			BlockPos pos = corner.offset(offsetX, y, offsetZ);
			tryHarvestBlock(level, pos, growthBoost, self);
		}
	}

	@Unique
	private void tryHarvestBlock(Level level, BlockPos pos, boolean growthBoost, PowerAcceptorBlockEntity self) {
		BlockState state = level.getBlockState(pos);
		for (var handler : GreenhousePlantRegistry.getHandlers()) {
			if (!handler.canHandle(state)) {
				continue;
			}
			if (growthBoost) {
				if (self.getStored() >= TechRebornConfig.greenhouseControllerEnergyPerBonemeal) {
					self.useEnergy(TechRebornConfig.greenhouseControllerEnergyPerBonemeal);
					state.randomTick((ServerLevel) level, pos, level.random);
					state = level.getBlockState(pos);
					if (!handler.canHandle(state)) {
						break;
					}
				}
			}
			HarvestOffer offer = handler.getHarvestOffer((ServerLevel) level, pos, state);
			if (offer != null && !offer.drops().isEmpty()) {
				if (self.getStored() >= TechRebornConfig.greenhouseControllerEnergyPerHarvest
						&& insertIntoInv(offer.drops())) {
					self.useEnergy(TechRebornConfig.greenhouseControllerEnergyPerHarvest);
					if (offer.newState() != null) {
						level.setBlock(pos, offer.newState(), 2);
					} else {
						level.destroyBlock(pos, false, null, 3);
					}
				}
			}
			break;
		}
	}
}
