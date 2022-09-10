package moe.vcup.TeeLogin.mixin;

import com.mojang.authlib.GameProfile;
import moe.vcup.TeeLogin.utils.LoginManager;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public abstract class DisableLoggedPlayerOffsiteLogin {

    @Shadow public abstract @Nullable ServerPlayerEntity getPlayer(String name);

    @Inject(method = "checkCanJoin", cancellable = true, at = @At(
            value = "HEAD"
    ))
    private void main_mixin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir){
        Text text = Text.translatable("multiplayer.disconnect.name_taken");
        var player = getPlayer(profile.getName());
        if (LoginManager.playerIsLogged(player)) cir.setReturnValue(text);
    }
}