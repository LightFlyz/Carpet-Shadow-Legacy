package com.ksandr707.carpet_shadow_legacy.utility;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;

/** Tracks classic (recursive) neighbor-update depth on the current thread. [current, max] */
public final class UpdateDepth {
    public static final ThreadLocal<Boolean> IN_CLICK = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[2]);

    public static void enter() {
        int[] d = DEPTH.get();
        if (d[0] == 0) d[1] = 0;
        d[0]++;
        if (d[0] > d[1]) d[1] = d[0];
        int limit = CarpetShadowLegacySettings.classicUpdateSuppressionDepth;
        if (limit > 0 && d[0] > limit) {
            d[0]--;
            if (d[0] == 0) report(d[1]);
            throw new StackOverflowError("Update chain exceeded classicUpdateSuppressionDepth=" + limit);
        }
    }

    public static void exit() {
        int[] d = DEPTH.get();
        d[0]--;
        if (d[0] == 0) report(d[1]);
    }

    private static void report(int max) {
        if (max >= 32 || (CarpetShadowLegacySettings.shadowDebugLog && IN_CLICK.get())) CarpetShadowLegacy.LOGGER.info("Classic update chain finished, max depth {}", max);
    }
}
