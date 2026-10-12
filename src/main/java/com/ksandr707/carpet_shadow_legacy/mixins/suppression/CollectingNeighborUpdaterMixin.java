package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.InstantNeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import com.ksandr707.carpet_shadow_legacy.utility.UpdateDepth;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * With classicUpdateSuppression on, neighbor updates skip the 1.19+ queue and run
 * recursively through vanilla's InstantNeighborUpdater, like 1.18 and earlier.
 * Deep update chains overflow the stack again, which is what update suppressors rely on.
 */
@Mixin(CollectingNeighborUpdater.class)
public abstract class CollectingNeighborUpdaterMixin {



    @Shadow @Final private Level level;

    @Unique private InstantNeighborUpdater csl$instant;

    @Unique
    private InstantNeighborUpdater csl$instant() {
        if (csl$instant == null) csl$instant = new InstantNeighborUpdater(this.level);
        return csl$instant;
    }

    @Inject(method = "shapeUpdate", at = @At("HEAD"), cancellable = true)
    private void csl$shapeUpdate(Direction direction, BlockState state, BlockPos pos, BlockPos neighborPos, int flags, int maxDepth, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.classicUpdateSuppression) return;
        UpdateDepth.enter();
        try {
            csl$instant().shapeUpdate(direction, state, pos, neighborPos, flags, maxDepth);
        } finally { UpdateDepth.exit(); }
        ci.cancel();
    }

    @Inject(method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/redstone/Orientation;)V", at = @At("HEAD"), cancellable = true)
    private void csl$neighborChanged(BlockPos pos, Block block, @Nullable Orientation orientation, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.classicUpdateSuppression) return;
        UpdateDepth.enter();
        try {
            csl$instant().neighborChanged(pos, block, orientation);
        } finally { UpdateDepth.exit(); }
        ci.cancel();
    }

    @Inject(method = "neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/redstone/Orientation;Z)V", at = @At("HEAD"), cancellable = true)
    private void csl$neighborChangedState(BlockState state, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.classicUpdateSuppression) return;
        UpdateDepth.enter();
        try {
            csl$instant().neighborChanged(state, pos, block, orientation, movedByPiston);
        } finally { UpdateDepth.exit(); }
        ci.cancel();
    }

    @Inject(method = "updateNeighborsAtExceptFromFacing", at = @At("HEAD"), cancellable = true, require = 0)
    private void csl$updateNeighbors(BlockPos pos, Block block, @Nullable Direction skip, @Nullable Orientation orientation, CallbackInfo ci) {
        if (!CarpetShadowLegacySettings.classicUpdateSuppression) return;
        UpdateDepth.enter();
        try {
            csl$instant().updateNeighborsAtExceptFromFacing(pos, block, skip, orientation);
        } finally { UpdateDepth.exit(); }
        ci.cancel();
    }
}
