package moe.vcup.TeeLogin.mixin.RedirectJoinMessage;

import moe.vcup.TeeLogin.Callbacks.PlayerLoginCallback;
import moe.vcup.TeeLogin.utils.LoginManager;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class LeftMessageMixin {
    @Shadow public ServerPlayerEntity player;

    @Redirect(method = "onDisconnected", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
    ))
    public void onLeft(PlayerManager instance, Text message, boolean overlay){
        LoginManager.registerCallback(this.player, new PlayerLoginCallback() {
            @Override
            public void onLogged() {
            }

            @Override
            public void onLogout() {
                instance.broadcast(message, overlay);
            }
        });
    }
}
