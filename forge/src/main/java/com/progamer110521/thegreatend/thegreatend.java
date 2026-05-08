package com.progamer110521.thegreatend;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class thegreatend {
    
    public thegreatend() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.register(modEventBus);
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
//        Constants.LOG.info("User language code: {}", Minecraft.getInstance().options.languageCode);
        CommonClass.init();
        
    }
}