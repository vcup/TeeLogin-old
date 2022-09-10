package moe.vcup.TeeLogin.mixin;

import moe.vcup.TeeLogin.utils.LoginManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerLoginMixin{
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void OnPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        LoginManager.NewLoginPlayer(player);
    }
}
