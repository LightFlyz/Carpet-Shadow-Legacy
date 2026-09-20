package com.ksandr707.carpet_shadow_legacy;

import com.ksandr707.carpet_shadow_legacy.interfaces.ShadowItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.*;

public class Globals {

    public static final Set<Inventory> toUpdate = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<Thread, Integer> mergingThreads = new HashMap<>();

    public static String createShadowId() {
        return UUID.randomUUID().toString();
    }

    public static ItemStack getByIdOrNull(String shadow_id) {
        if(shadow_id == null)
            return null;
        return CarpetShadowLegacy.shadowMap.get().getIfPresent(shadow_id);
    }

    public static ItemStack getByIdOrAdd(String shadow_id, ItemStack stack) {
        ItemStack reference = CarpetShadowLegacy.shadowMap.get().getIfPresent(shadow_id);
        if (reference != null)
            return reference;
        ((ShadowItem)(Object)stack).setShadowId(shadow_id);
        CarpetShadowLegacy.shadowMap.get().put(shadow_id, stack);
        return stack;
    }

    public static void mergingStart() {
        mergingThreads.merge(Thread.currentThread(), 1, Integer::sum);
    }

    public static void mergingEnd() {
        mergingThreads.compute(Thread.currentThread(), (thread, depth) -> depth != null && depth > 1 ? depth - 1 : null);
    }

    public static boolean isMergingThread() {
        return mergingThreads.getOrDefault(Thread.currentThread(), 0) > 0;
    }

    public static boolean shadowMergeCheck(ItemStack stack1, ItemStack stack2, boolean ret) {
        if (CarpetShadowLegacySettings.shadowItemInventoryFragilityFix && isMergingThread() && stack1.isOf(stack2.getItem())) {
            boolean shadow1 = ((ShadowItem) (Object) stack1).isShadowItem();
            boolean shadow2 = ((ShadowItem) (Object) stack2).isShadowItem();
            if (shadow1 ^ shadow2)
                return true;
            if (shadow1 && shadow2)
                return !CarpetShadowLegacySettings.shadowItemPreventCombine;
        }
        return ret;
    }
}