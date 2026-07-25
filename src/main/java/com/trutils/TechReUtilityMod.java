package com.trutils;

import com.trutils.greenhouse.api.GreenhousePlantRegistry;
import com.trutils.greenhouse.handler.ChorusFlowerHandler;
import com.trutils.greenhouse.handler.MagmaFlowerHandler;
import com.trutils.greenhouse.handler.GoldenVineHandler;
import com.trutils.init.ModBlocks;
import com.trutils.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechReUtilityMod implements ModInitializer {
	public static final String MOD_ID = "trutils";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModItems.initialize();
		GreenhousePlantRegistry.register(new ChorusFlowerHandler());
		GreenhousePlantRegistry.register(new MagmaFlowerHandler());
		GreenhousePlantRegistry.register(new GoldenVineHandler());
		LOGGER.info("TechReUtility initialized");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
