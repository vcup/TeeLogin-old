package moe.vcup.TeeLogin.mixin;

import moe.vcup.TeeLogin.utils.LoginManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerLogoutMixin {
    @Shadow @Final public ClientConnection connection;

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onDisconnected", at = @At(value = "TAIL"))
    public void onPlayerLogout(CallbackInfo ci){
        LoginManager.LoggedOutPlayer(this.player);
    }
}
