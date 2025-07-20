package amaury.emergence.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.DolphinEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class VariantDolphinRenderer extends DolphinEntityRenderer {
    private static final Identifier[] TEXTURES = new Identifier[]{
        Identifier.of("emergence", "textures/entity/dolphin/blue.png"),
        Identifier.of("emergence", "textures/entity/dolphin/cold.png"),
        Identifier.of("emergence", "textures/entity/dolphin/gray_gold.png"),
        Identifier.of("emergence", "textures/entity/dolphin/gray.png"),
        Identifier.of("emergence", "textures/entity/dolphin/indo.png"),
        Identifier.of("emergence", "textures/entity/dolphin/orca.png"),
        Identifier.of("emergence", "textures/entity/dolphin/striped.png"),
        Identifier.of("emergence", "textures/entity/dolphin/default.png"),
        Identifier.of("emergence", "textures/entity/dolphin/gray_striped.png")
    };

    public VariantDolphinRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(DolphinEntity entity) {
        ClientWorld world = MinecraftClient.getInstance().world;
        BlockPos pos = entity.getBlockPos();
        RegistryEntry<Biome> entry = world.getBiome(pos);

        int[] variants;
        if (entry.matchesKey(BiomeKeys.WARM_OCEAN) || entry.matchesKey(BiomeKeys.DEEP_LUKEWARM_OCEAN) || entry.matchesKey(BiomeKeys.LUKEWARM_OCEAN)) {
            // warm: default (7) or blue (0)
            variants = new int[]{7, 1,  0};
        } else if (entry.matchesKey(BiomeKeys.COLD_OCEAN) ||
                entry.matchesKey(BiomeKeys.DEEP_COLD_OCEAN)) {
            // cold: cold (1) or gray_gold (2)
            variants = new int[]{1, 2};
        } else if (entry.matchesKey(BiomeKeys.FROZEN_OCEAN)) {
            // frozen: orca (5)
            variants = new int[]{5};
        } else if (entry.matchesKey(BiomeKeys.OCEAN) ||
                entry.matchesKey(BiomeKeys.DEEP_OCEAN)) {
            // open ocean: gray (3), striped (6), gray_striped (8)
            variants = new int[]{3, 6, 8};
        } else {
            // fallback: your “default” slot
            variants = new int[]{7};
        }

        // pick one deterministically per‑entity
        int seed = entity.getUuid().hashCode();
        int idx   = Math.floorMod(seed, variants.length);
        int variantIndex = variants[idx];

        return TEXTURES[MathHelper.clamp(variantIndex, 0, TEXTURES.length - 1)];
    }
}
