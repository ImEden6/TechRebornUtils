package com.trutils.block;

import com.trutils.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import reborncore.api.ToolManager;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blocks.BlockWrenchEventHandler;
import reborncore.common.util.WrenchUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class DarkGrowthLampBlock extends Block implements EntityBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	private static final int COST = 1;
	private static final int CAPACITY = 33;
	private static final int MAX_INPUT = 32;

	private static final VoxelShape SHAPE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);

	public DarkGrowthLampBlock() {
		super(Properties.of()
				.requiresCorrectToolForDrops()
				.strength(2.0F, 2.0F)
				.lightLevel(state -> DarkGrowthLampBlock.isActive(state) ? 3 : 0)
				.sound(SoundType.GLASS));
		registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
		BlockWrenchEventHandler.wrenchableBlocks.add(this);
	}

	public static boolean isActive(BlockState state) {
		return state.hasProperty(ACTIVE) && state.getValue(ACTIVE);
	}

	public static void setActive(boolean active, Level level, BlockPos pos) {
		level.setBlock(pos, level.getBlockState(pos).setValue(ACTIVE, active), 3);
	}

	public int getCost() {
		return COST;
	}

	public int getCapacity() {
		return CAPACITY;
	}

	public int getMaxInput() {
		return MAX_INPUT;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!stack.isEmpty() && ToolManager.INSTANCE.canHandleTool(stack)) {
			if (WrenchUtils.handleWrench(stack, level, pos, player, hit.getDirection())) {
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DarkGrowthLampBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type != ModBlocks.DARK_GROWTH_LAMP_ENTITY) return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<?>) (l, p, s, be) ->
				((DarkGrowthLampBlockEntity) be).tick(l, p, s, (MachineBaseBlockEntity) be);
	}
}
