package com.github.minecraft_ta.totally_vanilla.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiVideoSettings.class)
public class GuiVideoSettingsMixin {

    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInitGui(CallbackInfo ci) {
        GameSettings.Options guiScale = GameSettings.Options.GUI_SCALE;
        Minecraft mc = Minecraft.getMinecraft();

        // Algorithm from ScaledResolution, pretends that guiScale is Auto to find the max scale factor
        int displayWidth = mc.displayWidth, displayHeight = mc.displayHeight;
        int scaleFactor = 1;
        boolean flag = mc.func_152349_b();
        int k = 1000;

        while (scaleFactor < k && displayWidth / (scaleFactor + 1) >= 320 && displayHeight / (scaleFactor + 1) >= 240)
            ++scaleFactor;

        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1)
            --scaleFactor;

        guiScale.setValueMax(scaleFactor);
    }
}
