package amaury.emergence.client;

import amaury.emergence.client.renderer.VariantDolphinRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

public class EmergenceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityType.DOLPHIN, VariantDolphinRenderer::new);
    }
}