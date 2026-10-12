package com.ksandr707.carpet_shadow_legacy.utility;

/** Tracks classic (recursive) neighbor-update depth on the current thread. [current, max] */
public final class UpdateDepth {
    public static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[2]);
    public static void enter() { int[] d = DEPTH.get(); d[0]++; if (d[0] > d[1]) d[1] = d[0]; }
    public static void exit() { DEPTH.get()[0]--; }
}
