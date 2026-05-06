package com.progamer110521.thegreatend.mixin;

import com.progamer110521.thegreatend.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(WinScreen.class)
public abstract class MixinWinScreen {

    @Shadow
    private boolean poem;

    private boolean hasStartedPlaying = false;

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/WinScreen;wrapCreditsIO(Ljava/lang/String;Lnet/minecraft/client/gui/screens/WinScreen$CreditsReader;)V"
            ),
            index = 0
    )
    private String modifyEndTextPath(String originalPath) {
        if (originalPath.equals("texts/end.txt")) {
            return "thegreatend:texts/end.txt";
        }
        return originalPath;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!hasStartedPlaying) {
            playEndPoemSound();
            hasStartedPlaying = true;
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        stopEndPoemSound();
    }

    private void playEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(
                new SimpleSoundInstance(
                        ModSounds.ENDPOEM_RU.getId(),
                        SoundSource.MUSIC,
                        1.0F,  // громкость
                        1.0F,  // pitch
                        minecraft.level.getRandom(),
                        false, // loop
                        0,     // delay
                        SimpleSoundInstance.Attenuation.NONE,
                        0.0,   // x
                        0.0,   // y
                        0.0,   // z
                        true   // relative
                )
        );
    }

    private void stopEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().stop(ModSounds.ENDPOEM_RU.getId(), SoundSource.MUSIC);
    }

}
