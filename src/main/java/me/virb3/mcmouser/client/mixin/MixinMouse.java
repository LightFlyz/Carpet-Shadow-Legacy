package me.virb3.mcmouser.client.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouse {
    private double deltaX;

    // 26.3: Ctrl+Click right-click emulation is now a vanilla option
    // (Options > Controls > "Ctrl+Click emulates right click"), so no patch is needed for it.

    @Inject(method = "onScroll", at = @At(value = "HEAD"))
    private void onScroll(long windowHandle, double deltaX, double deltaY, CallbackInfo ci) {
        this.deltaX = deltaX;
    }

    @ModifyVariable(method = "onScroll", ordinal = 1, at = @At(value = "LOAD"), argsOnly = true)
    private double onScroll_deltaX(double deltaY) {
        if (Util.getPlatform() == Util.OS.OSX && deltaY == 0) {
            return deltaX;
        }
        return deltaY;
    }
}
