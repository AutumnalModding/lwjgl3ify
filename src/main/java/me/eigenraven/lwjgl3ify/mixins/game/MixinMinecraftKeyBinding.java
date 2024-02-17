package me.eigenraven.lwjgl3ify.mixins.game;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Use raw key events for keybind handling to handle dead keys, etc.
 */
@Mixin(Minecraft.class)
public class MixinMinecraftKeyBinding {

    @Redirect(
        method = "runTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V"),
        slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;next()Z")))
    private void lwjgl3ify$noKeybindUpdateHere(int eventKey, boolean eventKeyState) {
        // Disable, handled in the client proxy
    }
}
