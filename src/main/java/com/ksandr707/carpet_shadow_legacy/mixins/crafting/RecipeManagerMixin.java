package com.ksandr707.carpet_shadow_legacy.mixins.crafting;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import com.ksandr707.carpet_shadow_legacy.Globals;
import com.ksandr707.carpet_shadow_legacy.interfaces.ShadowItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * 26.3 removed the old recipe-loading hook, so instead of adding the shadow recipe to the
 * loaded recipe map we answer crafting lookups for it directly.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Unique
    private static final RecipeHolder<CraftingRecipe> CSL_SHADOW_RECIPE = new RecipeHolder<>(
            ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("carpet_shadow_legacy", "shadow_recipe")),
            new CustomRecipe() {
            @Override
            public RecipeSerializer<? extends CustomRecipe> getSerializer() {
                return BookCloningRecipe.SERIALIZER;
            }

            @Override
            public boolean matches(CraftingInput inventory, Level world) {
                if (!CarpetShadowLegacySettings.shadowCraftingGeneration)
                    return false;
                boolean enderchest = false;
                int count = 0;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        if (stack.is(Items.ENDER_CHEST) && !enderchest && stack.getCount() == 1) {
                            enderchest = true;
                        }
                        count++;
                    }
                }
                return enderchest && count == 2;
            }

            @Override
            public ItemStack assemble(CraftingInput inventory) {
                if (!CarpetShadowLegacySettings.shadowCraftingGeneration){
                    return ItemStack.EMPTY;
                }

                ItemStack item = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        if (stack.is(Items.ENDER_CHEST)) {
                            if (enderchest != null) item = enderchest;
                            enderchest = stack;
                        } else {
                            item = stack;
                        }
                    }
                }
                if (item == null || enderchest == null) return ItemStack.EMPTY;

                String id = ((ShadowItem) (Object) item).getShadowId();
                if (id == null) {
                    id = CarpetShadowLegacy.shadow_id_generator.nextString();
                }
                return Globals.getByIdOrAdd(id, item);
            }

            @Override
            public NonNullList<ItemStack> getRemainingItems(CraftingInput inventory) {
                NonNullList<ItemStack> remainders = super.getRemainingItems(inventory);
                if (!CarpetShadowLegacySettings.shadowCraftingGeneration) return remainders;

                ItemStack item = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        if (stack.is(Items.ENDER_CHEST)) {
                            if (enderchest != null) item = enderchest;
                            enderchest = stack;
                        } else {
                            item = stack;
                        }
                }

                if (item != null && enderchest != null)
                    item.setCount(item.getCount() + 1);

                }
                return remainders;
            }
        });

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void csl$tryShadow(RecipeType<?> type, RecipeInput input, Level level, CallbackInfoReturnable cir) {
        if (type != RecipeType.CRAFTING || !CarpetShadowLegacySettings.shadowCraftingGeneration) return;
        if (input instanceof CraftingInput craftingInput && CSL_SHADOW_RECIPE.value().matches(craftingInput, level))
            cir.setReturnValue(Optional.of(CSL_SHADOW_RECIPE));
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private void csl$lookup(RecipeType<?> type, RecipeInput input, Level level, CallbackInfoReturnable<Optional<?>> cir) {
        csl$tryShadow(type, input, level, cir);
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private void csl$lookupKey(RecipeType<?> type, RecipeInput input, Level level, ResourceKey<?> hint, CallbackInfoReturnable<Optional<?>> cir) {
        csl$tryShadow(type, input, level, cir);
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private void csl$lookupHolder(RecipeType<?> type, RecipeInput input, Level level, RecipeHolder<?> hint, CallbackInfoReturnable<Optional<?>> cir) {
        csl$tryShadow(type, input, level, cir);
    }
}
