package com.github.minecraft_ta.totally_vanilla.mixins;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameSettings.class)
public class GameSettingsMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void makeGuiScaleSlider(CallbackInfo ci) {
        GameSettings.Options guiScale = GameSettings.Options.GUI_SCALE;

        ReflectionHelper.setPrivateValue(GameSettings.Options.class, guiScale, true, "enumFloat");
        ReflectionHelper.setPrivateValue(GameSettings.Options.class, guiScale, 1.0f, "valueStep");
//                ReflectionHelper.setPrivateValue(GameSettings.Options.class, value, 0.0f, "valueMin"); Default is zero
        guiScale.setValueMax(5.0f);
    }

    @Inject(method = "setOptionFloatValue", at = @At("HEAD"), cancellable = true)
    private void onSetOptionFloatValue(GameSettings.Options settingsOption, float value, CallbackInfo ci) {
        if (settingsOption == GameSettings.Options.GUI_SCALE) {
            Minecraft mc = Minecraft.getMinecraft();
            if ((int) value == mc.gameSettings.guiScale)
                return;
            mc.gameSettings.guiScale = (int) value;

            // Immediately apply the new GUI scale to the current screen. Otherwise, this will only happen once the
            // mouse button is released on the slider.
            if (mc.currentScreen != null) { // Sanity check?
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                int i1 = scaledresolution.getScaledWidth();
                int j1 = scaledresolution.getScaledHeight();
                mc.currentScreen.setWorldAndResolution(mc, i1, j1);
            }
            ci.cancel();
        }
    }

    @Inject(method = "getOptionFloatValue", at = @At("HEAD"), cancellable = true)
    private void onGetOptionFloatValue(GameSettings.Options settingsOption, CallbackInfoReturnable<Float> cir) {
        if (settingsOption == GameSettings.Options.GUI_SCALE) {
            cir.setReturnValue((float) Minecraft.getMinecraft().gameSettings.guiScale);
        }
    }

    @Inject(method = "getKeyBinding", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings$Options;getEnumFloat()Z", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGetKeyBinding(GameSettings.Options settingsOption, CallbackInfoReturnable<String> cir, String s) {
        if (settingsOption == GameSettings.Options.GUI_SCALE) {
            if (Minecraft.getMinecraft().gameSettings.guiScale == 0)
                cir.setReturnValue(s + "Auto");
            else
                cir.setReturnValue(s + Minecraft.getMinecraft().gameSettings.guiScale + "x");
        }
    }
}
