package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.ksandr707.carpet_shadow_legacy.utility.UpdateDepth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiodeBlock.class)
public abstract class DebugDiodeMixin {
    @Inject(method = "neighborChanged", at = @At("HEAD"))
    private void csl$debugDiode(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.shadowDebugLog) return;
        CarpetShadowLegacy.LOGGER.info("[shadowDebug] {} at {} got updated: canSurvive={} (breaks={})", state.getBlock().getDescriptionId(), pos.toShortString(),
                state.canSurvive(level, pos), !state.canSurvive(level, pos));
    }
}
