package com.progamer110521.thegreatend;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModShaders {

    public static ShaderInstance endPoemShader;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        ResourceProvider provider = event.getResourceProvider();

        event.registerShader(
                new ShaderInstance(
                        provider,
                        new ResourceLocation("thegreatend", "rendertype_end_poem"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> endPoemShader = shader
        );
    }
}