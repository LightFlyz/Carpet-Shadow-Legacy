package com.ksandr707.carpet_shadow_legacy.mixins.general;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.ksandr707.carpet_shadow_legacy.Globals;
import com.ksandr707.carpet_shadow_legacy.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin {

    @WrapOperation(
            method = "interactAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Z"
            )
    )
    private boolean handleSoundSuppression(
            ArmorStandEntity instance,
            PlayerEntity player,
            EquipmentSlot slot,
            ItemStack stack,
            Hand hand,
            Operation<Boolean> original) {

        if (CarpetShadowLegacySettings.shadowSuppressionGeneration) {
            try {
                return original.call(instance, player, slot, stack, hand);
            } catch (Throwable ignored) {

            }

            ItemStack heldStack = player.getStackInHand(hand);
            ItemStack standStack = instance.getEquippedStack(slot);

            if (heldStack.isEmpty() || standStack.isEmpty()) {
                return false;
            }

            if (heldStack.getCount() != standStack.getCount()) {
                return false;
            }

            ItemStack cmpHeld = heldStack.copy();
            ItemStack cmpStand = standStack.copy();
            ((ShadowItem) (Object) cmpHeld).removeShadow();
            ((ShadowItem) (Object) cmpStand).removeShadow();

            if (!ItemStack.areItemsAndComponentsEqual(cmpHeld, cmpStand)) {
                return false;
            }

            String shadowId = ((ShadowItem) (Object) heldStack).getShadowId();
            if (shadowId == null || shadowId.isEmpty()) {
                shadowId = Globals.createShadowId();
                ((ShadowItem) (Object) heldStack).setShadowId(shadowId);
            }

            Globals.getByIdOrAdd(shadowId, heldStack);

            if (standStack != heldStack) {
                try {
                    instance.equipStack(slot, heldStack);
                } catch (Throwable ignored) {
                }
            }

            return false;
        }
        return original.call(instance, player, slot, stack, hand);
    }
}