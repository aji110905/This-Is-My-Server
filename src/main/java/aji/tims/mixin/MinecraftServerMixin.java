package aji.tims.mixin;

import aji.tims.ThisIsMyServer;
import aji.tims.config.favicon.FaviconConfig;
import aji.tims.config.motd.MotdConfig;
import aji.tims.config.online.OnlineConfig;
import com.mojang.datafixers.DataFixer;
import net.minecraft.network.QueryableServer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.Optional;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements QueryableServer, ChunkErrorHandler, CommandOutput, AutoCloseable{
    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Shadow
    public abstract boolean shouldEnforceSecureProfile();

    @Shadow
    protected abstract ServerMetadata.Players createMetadataPlayers();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        ThisIsMyServer.server = (MinecraftServer) (Object) this;
    }

    @Inject(method = "getServerMetadata", at = @At("HEAD"), cancellable = true)
    public void getServerMetadata(CallbackInfoReturnable<ServerMetadata> cir) {
        ServerMetadata.Players players = createMetadataPlayers();
        cir.setReturnValue
                (
                        new ServerMetadata(
                                Text.of(((MotdConfig) ThisIsMyServer.configManager.getConfig(MotdConfig.NAME)).randomMotd()),
                                Optional.of(new ServerMetadata.Players(players.max(), ((OnlineConfig) ThisIsMyServer.configManager.getConfig(OnlineConfig.NAME)).getOnlineValue(), players.sample())),
                                Optional.of(ServerMetadata.Version.create()),
                                Optional.ofNullable(((FaviconConfig) ThisIsMyServer.configManager.getConfig(FaviconConfig.NAME)).randomFavicon()),
                                shouldEnforceSecureProfile()
                        )
                );
    }
}
