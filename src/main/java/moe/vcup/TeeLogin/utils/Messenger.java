package moe.vcup.TeeLogin.utils;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;

public class Messenger {
    public static void m(ServerPlayerEntity player, boolean actionBar, Object ... fields){
        BaseText message = new LiteralText("");
        for (Object o: fields){
            if (o instanceof BaseText) {
                message.append((BaseText) o);
                continue;
            }
            var txt = o.toString();
            message.append(txt);
        }
        player.sendMessage(message, actionBar);
    }
}
