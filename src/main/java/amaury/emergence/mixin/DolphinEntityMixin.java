package amaury.emergence.mixin;

import amaury.emergence.api.VariantDolphin;
import amaury.emergence.entity.variant.DolphinVariant;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.registry.entry.RegistryEntry; // Added import for biome registry entry
import net.minecraft.world.biome.Biome; // Added import for Biome class
import net.minecraft.util.math.BlockPos; // Added import for BlockPos
import net.minecraft.world.biome.BiomeKeys; // Ensure BiomeKeys is imported

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

@Mixin(DolphinEntity.class)
public abstract class DolphinEntityMixin extends WaterCreatureEntity implements VariantDolphin {

    // Register the custom TrackedData. The ID is assigned automatically by Minecraft.
    // This must be a static final field.
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
        DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DolphinEntityMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Injects into the end of the initDataTracker method to add our custom variant data.
     * By injecting at 'TAIL', we ensure that all vanilla data trackers have been added
     * by the superclass constructors and the original entity's setup before we add our own.
     * This is crucial to prevent conflicts with existing data tracker IDs and the
     * "has not defined synched data value" error.
     *
     * @param builder The DataTracker.Builder instance used to add tracked data.
     * @param ci CallbackInfo for the mixin injection.
     */
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void emergence_initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        // Initialize with default variant ID 0.
        builder.add(DATA_ID_TYPE_VARIANT, 0);
    }

    /**
     * Retrieves the DolphinVariant based on the stored tracked data.
     * The '& 255' mask ensures we only consider the lower 8 bits, which is typical for variant IDs.
     * @return The current DolphinVariant of the entity.
     */
    @Override
    public DolphinVariant getVariant() {
        return DolphinVariant.byId(this.dataTracker.get(DATA_ID_TYPE_VARIANT) & 255);
    }

    /**
     * Private helper to get the raw integer variant ID from the data tracker.
     * @return The raw integer ID of the variant.
     */
    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    /**
     * Sets the DolphinVariant for the entity.
     * @param variant The DolphinVariant to set.
     */
    @Override
    public void setVariant(DolphinVariant variant) {
        // Store only the ID of the variant, masked to ensure it fits within the integer type.
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    /**
     * Injects into the end of the writeCustomDataToNbt method to save the variant to NBT.
     * Using @Inject at TAIL ensures this runs after vanilla NBT writing.
     * @param nbt The NbtCompound to write data to.
     * @param ci CallbackInfo for the mixin injection.
     */
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void emergence_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Variant", this.getTypeVariant());
    }

    /**
     * Injects into the end of the readCustomDataFromNbt method to load the variant from NBT.
     * Using @Inject at TAIL ensures this runs after vanilla NBT reading.
     * @param nbt The NbtCompound to read data from.
     * @param ci CallbackInfo for the mixin injection.
     */
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void emergence_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        // Read the variant from NBT. getInt will return 0 if "Variant" tag is not found,
        // which matches our default initialization.
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
    }

    /**
     * Overrides the initialize method to set a random variant when the entity spawns,
     * now based on the biome temperature.
     * This method is called after the entity's data tracker has been initialized,
     * so it's safe to use setVariant here.
     *
     * @param world The server world access.
     * @param difficulty The local difficulty.
     * @param spawnReason The reason for spawning.
     * @param entityData Existing entity data (can be null).
     * @return The initialized EntityData.
     */
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData) {
        // Always call the super method first to ensure base initialization is complete.
        EntityData result = super.initialize(world, difficulty, spawnReason, entityData);

        // Get the biome at the entity's current position and its temperature
        BlockPos pos = this.getBlockPos();
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);

        DolphinVariant chosenVariant;

        // Determine variant based on specific biome keys
        if (biomeEntry.matchesKey(BiomeKeys.FROZEN_OCEAN)) {
            // Frozen Ocean: ORCA (5)
            chosenVariant = DolphinVariant.ORCA;
        } else if (biomeEntry.matchesKey(BiomeKeys.WARM_OCEAN)) {
            // Warm Ocean: 1/50 chance for INDO, otherwise COLD
            if (this.random.nextInt(50) == 0) { // 1 in 50 chance for Indo Dolphin
                chosenVariant = DolphinVariant.INDO; // Assuming DolphinVariant.INDO exists
            } else {
                chosenVariant = DolphinVariant.BLUE; // The remaining variant for warm ocean
            }
        } else if (biomeEntry.matchesKey(BiomeKeys.COLD_OCEAN) ||
                   biomeEntry.matchesKey(BiomeKeys.DEEP_COLD_OCEAN)) {
            // Cold Ocean: COLD (1) or GRAY_GOLD (2)
            DolphinVariant[] coldVariants = {DolphinVariant.COLD, DolphinVariant.GRAY_GOLD};
            chosenVariant = Util.getRandom(coldVariants, this.random);
        } else if (biomeEntry.matchesKey(BiomeKeys.OCEAN) ||
                   biomeEntry.matchesKey(BiomeKeys.DEEP_OCEAN) ||
                   biomeEntry.matchesKey(BiomeKeys.LUKEWARM_OCEAN) ||
                   biomeEntry.matchesKey(BiomeKeys.DEEP_LUKEWARM_OCEAN)) {
            // Normal/Lukewarm Ocean: GRAY (3), STRIPED (6), GRAY_STRIPED (8), BLUE (0), or DEFAULT (7)
            DolphinVariant[] normalLukewarmVariants = {
                DolphinVariant.GRAY,
                DolphinVariant.STRIPED,
                DolphinVariant.GRAY_STRIPED,
                DolphinVariant.DEFAULT
            };
            chosenVariant = Util.getRandom(normalLukewarmVariants, this.random);
        } else {
            // Fallback for any other biome not explicitly listed (e.g., rivers, lakes, non-ocean biomes)
            chosenVariant = DolphinVariant.DEFAULT;
        }

        setVariant(chosenVariant);
        return result;
    }
}