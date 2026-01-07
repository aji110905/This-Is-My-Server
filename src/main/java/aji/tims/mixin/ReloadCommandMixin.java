package aji.tims.mixin;

import aji.tims.ThisIsMyServer;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {
    @Inject(method = "method_13530", at = @At("RETURN"))
    private static void reload(CommandContext context, CallbackInfoReturnable<Integer> cir) {
        ThisIsMyServer.configManager.reload();
    }
}
