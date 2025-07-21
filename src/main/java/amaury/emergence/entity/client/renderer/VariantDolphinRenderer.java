package amaury.emergence.entity.client.renderer;

import com.google.common.collect.Maps;
import amaury.emergence.api.VariantDolphin;
import amaury.emergence.entity.variant.DolphinVariant;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DolphinEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

public class VariantDolphinRenderer extends DolphinEntityRenderer {
    private static final Map<DolphinVariant, Identifier> LOCATION_BY_VARIANT =
        Util.make(Maps.newEnumMap(DolphinVariant.class), map -> {
            map.put(DolphinVariant.DEFAULT,      Identifier.of("emergence", "textures/entity/dolphin/default.png"));
            map.put(DolphinVariant.BLUE,         Identifier.of("emergence", "textures/entity/dolphin/blue.png"));
            map.put(DolphinVariant.COLD,         Identifier.of("emergence", "textures/entity/dolphin/cold.png"));
            map.put(DolphinVariant.GRAY_GOLD,    Identifier.of("emergence", "textures/entity/dolphin/gray_gold.png"));
            map.put(DolphinVariant.GRAY,         Identifier.of("emergence", "textures/entity/dolphin/gray.png"));
            map.put(DolphinVariant.INDO,         Identifier.of("emergence", "textures/entity/dolphin/indo.png"));
            map.put(DolphinVariant.ORCA,         Identifier.of("emergence", "textures/entity/dolphin/orca.png"));
            map.put(DolphinVariant.STRIPED,      Identifier.of("emergence", "textures/entity/dolphin/striped.png"));
            map.put(DolphinVariant.GRAY_STRIPED, Identifier.of("emergence", "textures/entity/dolphin/gray_striped.png"));
        });

    public VariantDolphinRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DolphinEntity entity) {
        return LOCATION_BY_VARIANT.get(((VariantDolphin) entity).getVariant());
    }

    @Override
    public void render(DolphinEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int light) {
        if (livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, light);
    }
}
