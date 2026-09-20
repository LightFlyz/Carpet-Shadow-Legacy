package com.ksandr707.carpet_shadow_legacy.mixins.fragility;

import com.ksandr707.carpet_shadow_legacy.Globals;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HopperBlockEntity.class, priority = 2000)
public abstract class HopperBlockEntityMixin {
    @Inject(method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/HopperBlockEntity;)Z", at = @At("HEAD"))
    private static void fix_insert_start(World world, BlockPos pos, HopperBlockEntity hopper, CallbackInfoReturnable<Boolean> cir) {
        Globals.mergingStart();
    }

    @Inject(method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/HopperBlockEntity;)Z", at = @At("RETURN"))
    private static void fix_insert_end(World world, BlockPos pos, HopperBlockEntity hopper, CallbackInfoReturnable<Boolean> cir) {
        Globals.mergingEnd();
    }

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("HEAD"))
    private static void fix_extract_start(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        Globals.mergingStart();
    }

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("RETURN"))
    private static void fix_extract_end(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        Globals.mergingEnd();
    }

    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"))
    private static void fix_transfer_start(Inventory from, Inventory to, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<ItemStack> cir) {
        Globals.mergingStart();
    }
    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private static void fix_transfer_end(Inventory from, Inventory to, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<ItemStack> cir) {
        Globals.mergingEnd();
    }
}