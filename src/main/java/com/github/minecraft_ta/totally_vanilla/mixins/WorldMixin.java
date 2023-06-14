package com.github.minecraft_ta.totally_vanilla.mixins;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow
    public abstract GameRules getGameRules();

    @Inject(method = "updateWeatherBody", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;getThunderTime()I"), cancellable = true)
    private void onUpdateWeatherBody(CallbackInfo ci) {
        boolean doWeatherCycle = this.getGameRules().getGameRuleBooleanValue("doWeatherCycle");
        if (!doWeatherCycle) {
            ci.cancel();
        }
    }

}
