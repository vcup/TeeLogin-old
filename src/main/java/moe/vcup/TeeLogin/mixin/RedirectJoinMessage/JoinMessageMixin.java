package moe.vcup.TeeLogin.mixin.RedirectJoinMessage;

import moe.vcup.TeeLogin.utils.LoginManager;
import moe.vcup.TeeLogin.Callbacks.PlayerLoginCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(PlayerManager.class)
public abstract class JoinMessageMixin {
    @Shadow public abstract void broadcast(Text message, MessageType type, UUID sender);

    @Redirect(method = "onPlayerConnect", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"
    ))
    public void onJoin(PlayerManager playerManager, Text message, MessageType type, UUID uuid) {
        LoginManager.registerCallback(thisPlayer, new PlayerLoginCallback() {
            @Override
            public void onLogged() {
                broadcast(message, type, uuid);
            }

            @Override
            public void onLogout() {}
        });
    }

    public ServerPlayerEntity thisPlayer;

    @Inject(method = "onPlayerConnect", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V",
            shift = At.Shift.BEFORE
    ))
    public void getPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        thisPlayer = player;
    }
}
