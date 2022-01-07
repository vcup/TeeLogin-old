package moe.vcup.TeeLogin.mixin;

import moe.vcup.TeeLogin.TeeLogin;
import moe.vcup.TeeLogin.utils.LoginManager;
import moe.vcup.TeeLogin.utils.Messenger;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerLoginMixin{
    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void OnPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        LoginManager.NewLoginPlayer(player);
    }
}
