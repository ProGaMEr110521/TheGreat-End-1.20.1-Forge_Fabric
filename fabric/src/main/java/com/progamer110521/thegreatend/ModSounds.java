package com.progamer110521.thegreatend;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds implements ModInitializer {

    public static final SoundEvent ENDPOEM_RU = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "vpoem_ru_ru"));


    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.SOUND_EVENT,
                new ResourceLocation(Constants.MOD_ID, "vpoem_ru_ru"),
                ENDPOEM_RU);
    }

    public static ResourceLocation getVoiceoverLocalization(String languageCode) {

        if (languageCode.equals("ru_ru")) {
            return ENDPOEM_RU.getLocation();
        }
        return null;
    }
}
