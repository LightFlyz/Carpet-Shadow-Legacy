package com.ksandr707.carpet_shadow_legacy.mixins.general;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.ksandr707.carpet_shadow_legacy.Globals;
import com.ksandr707.carpet_shadow_legacy.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow public abstract Slot getSlot(int index);

    @Shadow public abstract ItemStack getCursorStack();

    @WrapOperation(method = "onSlotClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void handle_shadowing(ScreenHandler instance, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, Operation<Void> original) {
        try {
            original.call(instance, slotIndex, button, actionType, player);
        } catch (Throwable error) {
            if(actionType!=SlotActionType.SWAP && actionType!=SlotActionType.PICKUP && actionType!=SlotActionType.QUICK_CRAFT)
                throw error;
            ItemStack stack1 = this.getSlot(slotIndex).getStack();
            ItemStack stack2 = player.getInventory().getStack(button);
            ItemStack stack3 = this.getCursorStack();
            ItemStack shadow = null;
            if(stack1 == stack2 || stack1 == stack3)
                shadow = stack1;
            else if (stack2 == stack3)
                shadow = stack2;

            if(shadow != null){
                CarpetShadowLegacy.LOGGER.warn("New Shadow Item Created");
                String shadow_id = ((ShadowItem) (Object) shadow).getShadowId();
                if (shadow_id == null || shadow_id.isEmpty())
                    shadow_id = Globals.createShadowId();
                Globals.getByIdOrAdd(shadow_id,shadow);
            }
        }
    }

    @Inject(
            method = "internalOnSlotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 1
            )
    )
    private void internalOnSlotClickMixin(
            int slotIndex,
            int button,
            SlotActionType actionType,
            PlayerEntity player,
            CallbackInfo ci,
            @Local(ordinal = 0) Slot slot,
            @Local(ordinal = 0) ItemStack itemStack
    ) {
        if (CarpetShadowLegacySettings.shadowSuppressionGeneration)
            slot.setStack(itemStack);
    }
}