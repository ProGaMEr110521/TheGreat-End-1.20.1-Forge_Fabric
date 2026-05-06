package com.progamer110521.thegreatend.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.progamer110521.thegreatend.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
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
    private float scroll;

    private boolean hasStartedPlaying = false;

    private static final ResourceLocation END_SKY =
            new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation END_PORTAL =
            new ResourceLocation("textures/entity/end_portal.png");

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

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void onRenderBg(GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        renderEndSky(guiGraphics);
    }

    private void renderEndSky(GuiGraphics guiGraphics) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        float time = (System.currentTimeMillis() % 100000L) / 100000.0f;
        // Шевелимся вверх
        float parallax = -this.scroll * 0.001f;

        // 1 слой, темное небо
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_SKY);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float bgScale = 0.4f;
        // вверх v++
        float bgV1 = time * 0.015f + parallax * 0.1f;
        float bgU1 = time * 0.005f;
        float bgV2 = bgV1 + 3.0f * bgScale;
        float bgU2 = bgU1 + 3.0f * bgScale;

        float r1 = 0.02f;
        float g1 = 0.0f;
        float b1 = 0.08f;
        float a1 = 1.0f;

        bufferbuilder.vertex(0, height, 0).uv(bgU1, bgV2).color(r1, g1, b1, a1).endVertex();
        bufferbuilder.vertex(width, height, 0).uv(bgU2, bgV2).color(r1, g1, b1, a1).endVertex();
        bufferbuilder.vertex(width, 0, 0).uv(bgU2, bgV1).color(r1, g1, b1, a1).endVertex();
        bufferbuilder.vertex(0, 0, 0).uv(bgU1, bgV1).color(r1, g1, b1, a1).endVertex();

        tesselator.end();

        // слой 2 портал
        RenderSystem.setShaderTexture(0, END_PORTAL);
        RenderSystem.enableBlend();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float pV1 = time * 0.25f + parallax * 0.7f;
        float pU1 = time * 0.15f;
        float pV2 = pV1 + 2.5f;
        float pU2 = pU1 + 2.5f;

        float r2 = 0.1f;
        float g2 = 0.0f;
        float b2 = 0.3f;
        float a2 = 0.85f;

        bufferbuilder.vertex(0, height, 0).uv(pU1, pV2).color(r2, g2, b2, a2).endVertex();
        bufferbuilder.vertex(width, height, 0).uv(pU2, pV2).color(r2, g2, b2, a2).endVertex();
        bufferbuilder.vertex(width, 0, 0).uv(pU2, pV1).color(r2, g2, b2, a2).endVertex();
        bufferbuilder.vertex(0, 0, 0).uv(pU1, pV1).color(r2, g2, b2, a2).endVertex();

        tesselator.end();

        // слой 3 доп зв
        RenderSystem.setShaderTexture(0, END_PORTAL);
        RenderSystem.enableBlend();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float sV1 = time * 0.5f + parallax * 0.3f;
        float sU1 = time * 0.3f;
        float sV2 = sV1 + 3.0f;
        float sU2 = sU1 + 3.0f;

        float r3 = 0.7f;
        float g3 = 0.8f;
        float b3 = 1.0f;
        float a3 = 0.4f;

        bufferbuilder.vertex(0, height, 0).uv(sU1, sV2).color(r3, g3, b3, a3).endVertex();
        bufferbuilder.vertex(width, height, 0).uv(sU2, sV2).color(r3, g3, b3, a3).endVertex();
        bufferbuilder.vertex(width, 0, 0).uv(sU2, sV1).color(r3, g3, b3, a3).endVertex();
        bufferbuilder.vertex(0, 0, 0).uv(sU1, sV1).color(r3, g3, b3, a3).endVertex();

        tesselator.end();
        RenderSystem.disableBlend();
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

    private void stopEndPoemSound() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().stop(ModSounds.ENDPOEM_RU.getId(), SoundSource.MUSIC);
    }
}