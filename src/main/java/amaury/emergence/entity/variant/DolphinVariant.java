package amaury.emergence.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DolphinVariant {
    DEFAULT(0),
    BLUE(1),
    COLD(2),
    GRAY_GOLD(3),
    GRAY(4),
    INDO(5),
    ORCA(6),
    STRIPED(7),
    GRAY_STRIPED(8);

    private static final DolphinVariant[] BY_ID =
        Arrays.stream(values())
              .sorted(Comparator.comparingInt(DolphinVariant::getId))
              .toArray(DolphinVariant[]::new);

    private final int id;
    
    DolphinVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DolphinVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
