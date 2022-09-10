package moe.vcup.TeeLogin.mixin.RedirectJoinMessage;

import moe.vcup.TeeLogin.Callbacks.PlayerLoginCallback;
import moe.vcup.TeeLogin.utils.LoginManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerManager.class)
public abstract class JoinMessageMixin {
    @Shadow public abstract void broadcast(Text message, boolean overlay);

    @Redirect(method = "onPlayerConnect", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
    ))
    public void onJoin(PlayerManager instance, Text message, boolean overlay) {
        LoginManager.registerCallback(thisPlayer, new PlayerLoginCallback() {
            @Override
            public void onLogged() {
                broadcast(message, overlay);
            }

            @Override
            public void onLogout() {}
        });
    }

    public ServerPlayerEntity thisPlayer;

    @Inject(method = "onPlayerConnect", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V",
            shift = At.Shift.BEFORE
    ))
    public void getPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        thisPlayer = player;
    }
}
