package moe.vcup.TeeLogin.mixin.NotLoggedLimit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.vcup.TeeLogin.utils.LoginManager;
import moe.vcup.TeeLogin.utils.Messenger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public class CommandLimitMixin {
    @Inject(method = "execute", cancellable = true, at = @At("HEAD"))
    private void main(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        if (commandSource.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = commandSource.getPlayer();
            if (!LoginManager.playerIsLogged(player) && !command.contains("login")) {
                Messenger.m(player, true, "使用/login登录后再使用", command);
                cir.setReturnValue(0);
            }
        }
    }
}
