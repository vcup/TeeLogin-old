package moe.vcup.TeeLogin.mixin.NotLoggedLimit;

import moe.vcup.TeeLogin.utils.LoginManager;
import moe.vcup.TeeLogin.utils.Messenger;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatLimitMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleMessage", cancellable = true, at = @At("HEAD"))
    private void main(TextStream.Message message, CallbackInfo ci) {
        if (!LoginManager.playerIsLogged(player) && message.getRaw().startsWith("!!")) {
            ci.cancel();
            Messenger.m(player, true, "使用/login登录之后再发送该消息: ", message.getRaw());
        }
    }
}
