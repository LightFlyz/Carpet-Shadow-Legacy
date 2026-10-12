package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.InstantNeighborUpdater;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Routes Level's neighbor updates through vanilla's recursive InstantNeighborUpdater
 * (the pre-1.19 behaviour) instead of the queued CollectingNeighborUpdater, so deep
 * update chains overflow the stack again and classic update suppression works.
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    @Unique
    private NeighborUpdater csl$instantUpdater;

    @ModifyExpressionValue(
            method = {"updateNeighborsAt*", "updateNeighborsAtExceptFromFacing", "neighborChanged*", "neighborShapeChanged"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;neighborUpdater:Lnet/minecraft/world/level/redstone/NeighborUpdater;"),
            require = 1, allow = 16
    )
    private NeighborUpdater csl$useInstantUpdater(NeighborUpdater original) {
        if (!CarpetShadowLegacySettings.classicUpdateSuppression) return original;
        if (csl$instantUpdater == null) csl$instantUpdater = new InstantNeighborUpdater((Level) (Object) this);
        return csl$instantUpdater;
    }
}
