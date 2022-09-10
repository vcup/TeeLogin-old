package moe.vcup.TeeLogin.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Messenger {
    public static void m(ServerPlayerEntity player, boolean actionBar, Object ... fields){
        MutableText message = Text.empty().copy();

        for (Object o: fields){
            if (o instanceof Text) {
                message.append((Text) o);
                continue;
            }
            var txt = o.toString();
            message.append(txt);
        }
        player.sendMessage(message, actionBar);
    }
}
