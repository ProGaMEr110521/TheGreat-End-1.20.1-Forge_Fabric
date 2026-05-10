package com.progamer110521.thegreatend.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.progamer110521.thegreatend.Constants;
import com.progamer110521.thegreatend.ModShaders;
import com.progamer110521.thegreatend.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
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
    private boolean hasStartedPlaying = false;

    @Unique
    private static String minecraftLanguage = "temp";

    @Unique
    private SoundInstance poemSoundInstance = null;

    @Unique
    private static final ResourceLocation END_PORTAL =
            new ResourceLocation("textures/entity/end_portal.png");

    @Unique
    private void getUserLanguage() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraftLanguage = minecraft.options.languageCode;
    }

    @ModifyArg(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/WinScreen;wrapCreditsIO(Ljava/lang/String;Lnet/minecraft/client/gui/screens/WinScreen$CreditsReader;)V"),
            index = 0)
    private String modifyEndTextPath(String originalPath) {
        getUserLanguage();

        if (originalPath.equals("texts/end.txt")) {
            if (minecraftLanguage.equals("ru_ru")) { return "thegreatend:texts/poem_ru_ru.txt"; }
        }
        return originalPath;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        getUserLanguage();

        if (!hasStartedPlaying) {
            playEndPoemSound();
            hasStartedPlaying = true;
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        stopEndPoemSound();
    }

    @Unique
    private void playEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation voiceoverLocalization = ModSounds.getVoiceoverLocalization(minecraftLanguage);

        if (voiceoverLocalization != null) {

        SoundInstance poemSoundInstanceLoc = new SimpleSoundInstance(
                voiceoverLocalization,
                SoundSource.VOICE,
                1.0F,
                1.0F,
                minecraft.level.getRandom(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0,
                true
        );

            poemSoundInstance = poemSoundInstanceLoc;
            minecraft.getSoundManager().play(poemSoundInstanceLoc);
        }
    }

    @Unique
    private void stopEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();

        if (poemSoundInstance != null) {
            minecraft.getSoundManager().stop(poemSoundInstance);
        }
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void onRenderBg(GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        renderEndSky(guiGraphics);
    }

    @Unique
    private void renderEndSky(GuiGraphics guiGraphics) {
        ShaderInstance customShader = ModShaders.getRendertypeEndPoem();

        // Проверяем, что шейдер готов
        if (customShader == null || customShader.getId() <= 0) {
            return;
        }

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        Matrix4f matrix = guiGraphics.pose().last().pose();

        float gameTime = (float) ((System.nanoTime() / 1000000L) % 10000000L) / 20.0f;
        float customTime = (float) (System.currentTimeMillis() % 10000000L) / 3000.0f;

        // Устанавливаем юниформы
        customShader.safeGetUniform("GameTime").set(gameTime);
        customShader.safeGetUniform("CustomTime").set(customTime);
        customShader.safeGetUniform("EndPortalLayers").set(15);

        // Применяем шейдер через RenderSystem
        RenderSystem.setShader(() -> customShader);
        RenderSystem.setShaderTexture(0, END_PORTAL);
        RenderSystem.setShaderTexture(1, END_PORTAL);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        // Используем тот же формат, что и в шейдере — POSITION_COLOR_TEX
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        builder.vertex(matrix, 0, height, 0).color(1f, 1f, 1f, 1f).uv(0, 0).endVertex();
        builder.vertex(matrix, width, height, 0).color(1f, 1f, 1f, 1f).uv(1, 0).endVertex();
        builder.vertex(matrix, width, 0, 0).color(1f, 1f, 1f, 1f).uv(1, 1).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(1f, 1f, 1f, 1f).uv(0, 1).endVertex();
        tesselator.end();

        RenderSystem.setShaderTexture(1, 0);
        RenderSystem.disableBlend();
    }
}
