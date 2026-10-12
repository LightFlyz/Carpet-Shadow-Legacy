package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.ksandr707.carpet_shadow_legacy.utility.UpdateDepth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** shadowDebugLog: show which blocks a container change notifies, during a click. */
@Mixin(Level.class)
public abstract class DebugLevelMixin {
    @Inject(method = "updateNeighbourForOutputSignal", at = @At("HEAD"))
    private void csl$debugOutputSignal(BlockPos pos, Block changedBlock, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.shadowDebugLog || !UpdateDepth.IN_CLICK.get()) return;
        Level level = (Level) (Object) this;
        StringBuilder sb = new StringBuilder();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos p = pos.relative(d);
            BlockState s = level.getBlockState(p);
            sb.append(d.getName()).append('=').append(s.getBlock().getDescriptionId());
            if (s.getBlock() instanceof net.minecraft.world.level.block.ComparatorBlock)
                sb.append("[canSurvive=").append(s.canSurvive(level, p)).append(", below=").append(level.getBlockState(p.below()).getBlock().getDescriptionId()).append(']');
            sb.append("  ");
        }
        CarpetShadowLegacy.LOGGER.info("[shadowDebug] container at {} ({}) notifies comparators; neighbours: {}", pos.toShortString(), changedBlock.getDescriptionId(), sb);
    }
}
