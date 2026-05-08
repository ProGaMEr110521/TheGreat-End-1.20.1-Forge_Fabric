package com.progamer110521.thegreatend.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.progamer110521.thegreatend.ModShaders;
import com.progamer110521.thegreatend.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WinScreen.class)
public abstract class MixinWinScreen {

    @Unique
    private boolean theGreat_End_1_20_1$hasStartedPlaying = false;

    @Unique
    private static final ResourceLocation END_PORTAL =
            new ResourceLocation("textures/entity/end_portal.png");

    @Unique
    private static String theGreat_End_1_20_1$minecraftLanguage = "temp";

    @Unique
    private void theGreat_End_1_20_1$getUserLanguage() {
        Minecraft minecraft = Minecraft.getInstance();
        theGreat_End_1_20_1$minecraftLanguage = minecraft.options.languageCode;
    }

    @ModifyArg(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/WinScreen;wrapCreditsIO(Ljava/lang/String;Lnet/minecraft/client/gui/screens/WinScreen$CreditsReader;)V"),
            index = 0)
    private String modifyEndTextPath(String originalPath) {
        theGreat_End_1_20_1$getUserLanguage();

        if (originalPath.equals("texts/end.txt")) {
            if (theGreat_End_1_20_1$minecraftLanguage.equals("ru_ru")) { return "thegreatend:texts/poem_ru_ru.txt"; }
        }
        return originalPath;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        theGreat_End_1_20_1$getUserLanguage();

        if (!theGreat_End_1_20_1$hasStartedPlaying) {
            theGreat_End_1_20_1$playEndPoemSound();
            theGreat_End_1_20_1$hasStartedPlaying = true;
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        theGreat_End_1_20_1$stopEndPoemSound();
    }

    @Unique
    private void theGreat_End_1_20_1$playEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation voiceoverLocalization = ModSounds.getVoiceoverLocalization(theGreat_End_1_20_1$minecraftLanguage);

        if (voiceoverLocalization != null) {

            minecraft.getSoundManager().play(new SimpleSoundInstance(
                        voiceoverLocalization,
                        SoundSource.VOICE,
                        1.0F,
                        1.0F,
                        minecraft.level.getRandom(),
                        false,
                        0,
                        SimpleSoundInstance.Attenuation.NONE,
                        0.0, 0.0, 0.0,
                        true
                )
            );
        }
    }

    @Unique
    private void theGreat_End_1_20_1$stopEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().stop(ModSounds.ENDPOEM_RU.getId(), SoundSource.MUSIC);
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void onRenderBg(GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        theGreat_End_1_20_1$renderEndSky(guiGraphics);
    }

    @Unique
    private void theGreat_End_1_20_1$renderEndSky(GuiGraphics guiGraphics) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        Matrix4f matrix = guiGraphics.pose().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();


        float gameTime = (float) ((System.nanoTime() / 1000000L) % 10000000L) / 20.0f;
        float customTime = (float) (System.currentTimeMillis() % 10000000L) / 3000.0f;

        ShaderInstance customShader = ModShaders.endPoemShader;

        if (customShader != null) {
            customShader.safeGetUniform("GameTime").set(gameTime);
            customShader.safeGetUniform("CustomTime").set(customTime);
            customShader.safeGetUniform("EndPortalLayers").set(15);
            customShader.apply();

            RenderSystem.setShader(() -> customShader);
            RenderSystem.setShaderTexture(0, END_PORTAL);
            RenderSystem.setShaderTexture(1, END_PORTAL);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            builder.vertex(matrix, 0, height, 0).endVertex();
            builder.vertex(matrix, width, height, 0).endVertex();
            builder.vertex(matrix, width, 0, 0).endVertex();
            builder.vertex(matrix, 0, 0, 0).endVertex();
            tesselator.end();

            RenderSystem.setShaderTexture(1, 0);
            RenderSystem.disableBlend();
        }
    }
}