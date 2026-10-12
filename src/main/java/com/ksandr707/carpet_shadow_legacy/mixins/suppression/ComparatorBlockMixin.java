package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Newer versions made comparators pop off through shape updates when their support changes
 * (e.g. opening the trapdoor they stand on). Before that, they only checked support on a
 * neighbour update, which is what the classic trapdoor floating-comparator trick relies on.
 */
@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin {
    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void csl$legacySupport(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                   BlockPos neighbourPos, BlockState neighbourState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        if (CarpetShadowLegacySettings.legacyComparatorSupport && direction == Direction.DOWN) cir.setReturnValue(state);
    }
}
