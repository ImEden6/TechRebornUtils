package com.trutils.init;

import com.trutils.TechReUtilityMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import techreborn.items.UpgradeItem;

public class ModItems {
	public static Item GREENHOUSE_CEILING_UPGRADE;
	public static CreativeModeTab TRUTILS_TAB;
	public static final ResourceKey<CreativeModeTab> TRUTILS_TAB_KEY = ResourceKey.create(
			BuiltInRegistries.CREATIVE_MODE_TAB.key(),
			TechReUtilityMod.id("tab")
	);

	public static void initialize() {
		GREENHOUSE_CEILING_UPGRADE = Registry.register(
				BuiltInRegistries.ITEM,
				TechReUtilityMod.id("greenhouse_ceiling_upgrade"),
				new UpgradeItem("greenhouse_ceiling_upgrade", (machine, handler, stack) -> {})
		);

		TRUTILS_TAB = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				TechReUtilityMod.id("tab"),
				FabricItemGroup.builder()
						.title(Component.translatable("itemGroup.trutils.tab"))
						.icon(() -> new ItemStack(GREENHOUSE_CEILING_UPGRADE))
						.displayItems((parameters, output) -> {
							output.accept(ModBlocks.DARK_GROWTH_LAMP);
							output.accept(GREENHOUSE_CEILING_UPGRADE);
						})
						.build()
		);
	}
}
