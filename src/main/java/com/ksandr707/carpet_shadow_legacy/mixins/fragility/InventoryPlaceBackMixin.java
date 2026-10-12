package com.ksandr707.carpet_shadow_legacy.mixins.fragility;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Closing a menu puts the cursor stack back into the inventory. If that cursor stack is a shadow
 * of a stack already in the inventory (same object), 26.3's loop splits from it and grows the very
 * same stack, so the count never drops and the server hangs until the watchdog kills it.
 * The stack is already in the inventory, so there is nothing to place back.
 */
@Mixin(Inventory.class)
public abstract class InventoryPlaceBackMixin {
    @Inject(method = "placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("HEAD"), cancellable = true)
    private void csl$skipShadowAlreadyInside(ItemStack stack, boolean sendPacket, CallbackInfo ci) {
        if (stack.isEmpty()) return;
        Inventory self = (Inventory) (Object) this;
        for (int i = 0; i < self.getContainerSize(); i++) {
            if (self.getItem(i) == stack) {
                CarpetShadowLegacy.LOGGER.debug("Shadow stack already in inventory slot {}, not placing it back", i);
                ci.cancel();
                return;
            }
        }
    }
}
