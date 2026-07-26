package com.trutils.init;

import com.trutils.TechReUtilityMod;
import com.trutils.block.DarkGrowthLampBlock;
import com.trutils.block.DarkGrowthLampBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
	public static final Block DARK_GROWTH_LAMP = new DarkGrowthLampBlock();

	public static void initialize() {
		Registry.register(BuiltInRegistries.BLOCK, TechReUtilityMod.id("dark_growth_lamp"), DARK_GROWTH_LAMP);
		Registry.register(BuiltInRegistries.ITEM, TechReUtilityMod.id("dark_growth_lamp"),
				new BlockItem(DARK_GROWTH_LAMP, new Item.Properties()));

		DarkGrowthLampBlock.ENTITY_TYPE = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				TechReUtilityMod.id("dark_growth_lamp"),
				FabricBlockEntityTypeBuilder.create(DarkGrowthLampBlockEntity::new, DARK_GROWTH_LAMP).build());
	}
}
