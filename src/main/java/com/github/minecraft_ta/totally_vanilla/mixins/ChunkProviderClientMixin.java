package com.github.minecraft_ta.totally_vanilla.mixins;

import com.github.minecraft_ta.totally_vanilla.TotallyVanillaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(ChunkProviderClient.class)
public abstract class ChunkProviderClientMixin {

    @Shadow
    private LongHashMap chunkMapping;
    @Shadow
    private List chunkListing;

    @Unique
    private final Set<ChunkCoordIntPair> tv$clientSideUnloadQueued = new HashSet<>();

    @Shadow
    public abstract Chunk provideChunk(int p_73154_1_, int p_73154_2_);

    @Inject(method = "unloadChunk", at = @At("HEAD"), cancellable = true)
    public void onUnloadChunk(int chunkX, int chunkZ, CallbackInfo ci) {
        if (!TotallyVanillaConfig.clientSideKeepChunks)
            return;

        ci.cancel();
        tv$clientSideUnloadQueued.add(new ChunkCoordIntPair(chunkX, chunkZ));
    }

    @Inject(method = "unloadQueuedChunks", at = @At("HEAD"))
    public void unloadCapturedChunks(CallbackInfoReturnable<Boolean> cir) {
        if (!TotallyVanillaConfig.clientSideKeepChunks)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        int playerChunkX = mc.thePlayer.chunkCoordX;
        int playerChunkZ = mc.thePlayer.chunkCoordZ;

        int keepDistanceSq = mc.gameSettings.renderDistanceChunks * mc.gameSettings.renderDistanceChunks;

        tv$clientSideUnloadQueued.removeIf(pair -> {
            int xDiff = playerChunkX - pair.chunkXPos;
            int zDiff = playerChunkZ - pair.chunkZPos;
            if (xDiff * xDiff + zDiff * zDiff <= keepDistanceSq)
                return false;

            // Begin ChunkProviderClient#unloadChunk
            Chunk c = provideChunk(pair.chunkXPos, pair.chunkZPos);

            if (!c.isEmpty())
                c.onChunkUnload();

            this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(pair.chunkXPos, pair.chunkZPos));
            this.chunkListing.remove(c);
            // End ChunkProviderClient#unloadChunk

            // This is usually called right after a chunk was unloaded, but since we're doing it manually at a later
            // point in time, we need to also update the renderer here.
            mc.theWorld.markBlockRangeForRenderUpdate(pair.chunkXPos * 16, 0, pair.chunkZPos * 16, pair.chunkXPos * 16 + 15, 256, pair.chunkZPos * 16 + 15);
            return true;
        });
    }
}
