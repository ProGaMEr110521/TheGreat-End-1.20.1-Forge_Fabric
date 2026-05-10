package com.progamer110521.thegreatend;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ModShaders {

    public static final ResourceLocation RENDERTYPE_END_POEM = new ResourceLocation(Constants.MOD_ID, "rendertype_end_poem");

    private static ShaderInstance rendertypeEndPoem = null;

    private static boolean registered = false;

    public static ShaderInstance getRendertypeEndPoem() {
        return rendertypeEndPoem;
    }

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            if (registered) return; // multiple registration fix, likely Fabric problem

            registered = true;

            try {
                context.register(
                        RENDERTYPE_END_POEM,
                        DefaultVertexFormat.POSITION_COLOR_TEX,
                        ShaderInstance -> {
                            rendertypeEndPoem = ShaderInstance;

                            Constants.LOG.info("Shader successfully loaded {}", RENDERTYPE_END_POEM);
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
