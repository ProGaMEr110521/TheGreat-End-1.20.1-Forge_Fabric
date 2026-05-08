package com.progamer110521.thegreatend;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "thegreatend");

    public static final RegistryObject<SoundEvent> ENDPOEM_RU = SOUND_EVENTS.register("vpoem_ru_ru",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("thegreatend", "vpoem_ru_ru")));

    public static ResourceLocation getVoiceoverLocalization(String languageCode) {

        if (languageCode.equals("ru_ru")) {
            return ENDPOEM_RU.getId();
        }
        return null;
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
