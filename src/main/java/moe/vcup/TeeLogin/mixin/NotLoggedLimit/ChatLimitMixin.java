package moe.vcup.TeeLogin.mixin.NotLoggedLimit;

import moe.vcup.TeeLogin.utils.LoginManager;
import moe.vcup.TeeLogin.utils.Messenger;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatLimitMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "canAcceptMessage(Ljava/lang/String;Ljava/time/Instant;Lnet/minecraft/network/message/LastSeenMessageList$Acknowledgment;)Z",
            at = @At("HEAD"), cancellable = true)
    private void main(String message, Instant timestamp, LastSeenMessageList.Acknowledgment acknowledgment, CallbackInfoReturnable<Boolean> cir) {
        if (!LoginManager.playerIsLogged(player) && message.startsWith("!!")) {
            cir.setReturnValue(false);
            Messenger.m(player, true, "使用/login登录之后再发送该消息: ", message);
        }
    }
}
