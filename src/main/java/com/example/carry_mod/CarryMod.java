package com.example.carry_mod;

import com.example.carry_mod.events.CarryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CarryMod.MOD_ID)
public class CarryMod {
	public static final String MOD_ID = "carry_mod";

	public CarryMod() {
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::loadComplete);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(new CarryEvent());
	}
}
