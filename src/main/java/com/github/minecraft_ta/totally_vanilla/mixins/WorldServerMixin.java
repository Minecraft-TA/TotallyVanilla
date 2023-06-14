package com.github.minecraft_ta.totally_vanilla.mixins;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin {

    @Inject(method = "wakeAllPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;resetRainAndThunder()V"), cancellable = true)
    private void onWakeAllPlayers(CallbackInfo ci) {
        boolean doWeatherCycle = ((WorldServer) (Object) this).getGameRules().getGameRuleBooleanValue("doWeatherCycle");
        if (!doWeatherCycle) {
            ci.cancel();
        }
    }



}
