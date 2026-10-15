package com.ksandr707.carpet_shadow_legacy.mixins.suppression;

import com.ksandr707.carpet_shadow_legacy.utility.UpdateDepth;
import net.minecraft.SystemReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Every update suppression makes vanilla build a CrashReport, whose SystemReport probes the
 * hardware through OSHI. On Linux that runs `lshw`, which can block for over a minute and gets the
 * server killed by the watchdog. Hardware details are useless for a suppression, so skip them
 * while a classic update chain is running.
 */
@Mixin(SystemReport.class)
public abstract class SystemReportMixin {
    @Inject(method = "putHardware", at = @At("HEAD"), cancellable = true)
    private void csl$skipHardwareDuringSuppression(CallbackInfo ci) {
        if (UpdateDepth.DEPTH.get()[0] > 0) ci.cancel();
    }
}
