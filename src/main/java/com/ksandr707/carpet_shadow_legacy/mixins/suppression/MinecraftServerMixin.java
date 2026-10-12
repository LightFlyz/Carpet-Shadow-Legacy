package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BooleanSupplier;

/** Keeps the server alive when an update suppressor fires during a level tick. */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @WrapOperation(
            method = "tickChildren",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V")
    )
    private void csl$catchSuppression(ServerLevel level, BooleanSupplier haveTime, Operation<Void> original) {
        try {
            original.call(level, haveTime);
        } catch (Throwable t) {
            if (!CarpetShadowLegacySettings.updateSuppressionCrashFix || !isSuppression(t)) throw t;
            CarpetShadowLegacy.LOGGER.warn("Update suppression in {} caught by updateSuppressionCrashFix", level.dimension());
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private static boolean isSuppression(Throwable t) {
        for (int i = 0; t != null && i < 64; t = t.getCause(), i++) {
            if (t instanceof StackOverflowError || t instanceof ClassCastException) return true;
        }
        return false;
    }
}
